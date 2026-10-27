package com.superbrix.ia.data.sync

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.superbrix.ia.core.config.AppConfig
import com.superbrix.ia.data.local.db.BrixDatabaseHelper
import com.superbrix.ia.data.model.Evento
import com.superbrix.ia.data.remote.SuperBrixApiClient
import com.superbrix.ia.data.remote.models.EventoSyncDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Worker industrial de sincronización en segundo plano con WorkManager.
 *
 * Se ejecuta automáticamente:
 * 1. De forma periódica cada 15 minutos cuando hay conectividad de red.
 * 2. Inmediatamente bajo demanda cuando se recupera la red o se reporta un nuevo evento.
 *
 * Garantiza resiliencia 'Offline-First': los registros se guardan primero en SQLite local
 * y este worker los transmite al backend FastAPI y a Google Sheets.
 */
class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "BRIX_SyncWorker"
        const val PERIODIC_WORK_NAME = "brix360_periodic_sync"
        const val IMMEDIATE_WORK_NAME = "brix360_immediate_sync"

        /**
         * Programa la sincronización periódica en segundo plano.
         */
        fun schedulePeriodicSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val periodicRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                PERIODIC_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
            )
            Log.d(TAG, "Sincronización periódica programada (cada 15 min)")
        }

        /**
         * Dispara una sincronización inmediata cuando hay conexión de red.
         */
        fun enqueueImmediateSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val immediateRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                IMMEDIATE_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                immediateRequest
            )
            Log.d(TAG, "Sincronización inmediata solicitada a WorkManager")
        }
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val dbHelper = BrixDatabaseHelper(applicationContext)
        val pendingEvents = dbHelper.getPendingEventos()

        if (pendingEvents.isEmpty()) {
            Log.d(TAG, "No hay eventos pendientes para sincronizar.")
            return@withContext Result.success()
        }

        Log.i(TAG, "Iniciando sincronización de ${pendingEvents.size} evento(s) pendientes...")
        var allSucceeded = true

        for (evento in pendingEvents) {
            val success = syncSingleEvento(evento)
            if (success) {
                dbHelper.markEventAsSynced(evento.id)
                Log.d(TAG, "Evento ${evento.id} sincronizado y marcado como ENVIADO.")
            } else {
                Log.w(TAG, "Fallo al sincronizar evento ${evento.id}. Permanecerá PENDIENTE.")
                allSucceeded = false
            }
        }

        if (allSucceeded) {
            Result.success()
        } else {
            Result.retry()
        }
    }

    private suspend fun syncSingleEvento(evento: Evento): Boolean {
        var apiSuccess = false
        var sheetsSuccess = false

        // 1. Intento de sincronización hacia el Backend FastAPI
        try {
            val dto = EventoSyncDto(
                id = evento.id,
                fecha = evento.fecha,
                horaInicio = evento.horaInicio,
                horaFin = evento.horaFin,
                operarioId = evento.operario,
                ordenId = evento.ordenProduccion,
                maquinaId = evento.maquina,
                tipoEvento = evento.tipoEvento,
                mensaje = evento.descripcion,
                categoria = evento.categoria,
                descripcion = evento.descripcion,
                confianzaIa = evento.confianzaIA.replace("%", "").trim().toDoubleOrNull()?.div(100.0) ?: 0.95,
                duracionMinutos = evento.duracionMinutos,
                duracionSegundos = evento.duracionSegundos,
                estado = if (evento.anulado) "ANULADO" else "PROCESADO",
                categoriaCorregida = evento.categoriaCorregida,
                categoriaOriginalIa = evento.categoriaOriginalIA,
                fotoUri = evento.fotoUri
            )

            val response = SuperBrixApiClient.apiService.registrarEvento(dto)
            if (response.isSuccessful) {
                apiSuccess = true
            } else {
                Log.w(TAG, "API retorno HTTP ${response.code()} para ${evento.id}")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error de red conectando con API FastAPI: ${e.message}")
        }

        // 2. Intento de sincronización hacia Google Sheets (Apps Script)
        val scriptUrl = AppConfig.APPS_SCRIPT_URL
        if (scriptUrl.isNotBlank() && !scriptUrl.contains("TU_URL") && scriptUrl.startsWith("http")) {
            try {
                val payload = JSONObject().apply {
                    put("id", evento.id)
                    put("fecha", evento.fecha)
                    put("hora_inicio", evento.horaInicio)
                    put("hora_fin", evento.horaFin ?: "")
                    put("operario_id", evento.operario)
                    put("orden_id", evento.ordenProduccion)
                    put("maquina_id", evento.maquina)
                    put("tipo_evento", evento.tipoEvento)
                    put("mensaje", evento.descripcion)
                    put("categoria", evento.categoria)
                    put("descripcion", evento.descripcion)
                    put("confianza_ia", evento.confianzaIA)
                    put("duracion_minutos", evento.duracionMinutos)
                    put("duracion_segundos", evento.duracionSegundos)
                    put("estado", if (evento.anulado) "ANULADO" else "PROCESADO")
                    put("categoria_corregida", evento.categoriaCorregida)
                    put("categoria_original_ia", evento.categoriaOriginalIA ?: "")
                    put("anulado", evento.anulado)
                    put("motivo_anulacion", evento.motivoAnulacion ?: "")
                }

                val client = OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .build()

                val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
                val requestBody = payload.toString().toRequestBody(mediaType)
                val request = Request.Builder()
                    .url(scriptUrl)
                    .post(requestBody)
                    .build()

                val resp = client.newCall(request).execute()
                sheetsSuccess = resp.isSuccessful
                resp.close()
            } catch (e: Exception) {
                Log.w(TAG, "Error sincronizando con Sheets: ${e.message}")
            }
        } else {
            // Si no está configurada la URL de Sheets, no bloqueamos el flujo
            sheetsSuccess = true
        }

        // Se considera exitoso si al menos uno de los canales receptores respondió con éxito
        return apiSuccess || sheetsSuccess
    }
}
