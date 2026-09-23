package com.superbrix.ia.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.superbrix.ia.data.model.Evento
import com.superbrix.ia.data.model.IACategorias
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class EventoUiState(
    val eventosList: List<Evento> = emptyList(),
    val isAnalyzing: Boolean = false,
    val ultimoEventoReportado: Evento? = null,
    val eventoSeleccionado: Evento? = null
)

class EventoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EventoUiState())
    val uiState: StateFlow<EventoUiState> = _uiState.asStateFlow()

    // CONSTANTES DE CONFIGURACIÓN
    private val GEMINI_API_KEY = "TU_API_KEY_AQUI"
    private val GOOGLE_APPS_SCRIPT_URL = "https://script.google.com/macros/s/AKfycbyEP7gj1XO2Wysa-0gVJLH46w-OH5vEnzGp6wspTzi2GKR2G18UhfB44SilHQQwTym_/exec"

    init {
        loadMockEvents()
    }

    private fun loadMockEvents() {
        _uiState.update { it.copy(eventosList = emptyList()) }
    }

    fun reportarNovedad(
        descripcionText: String,
        ordenCodigo: String = "OP-001",
        maquinaNombre: String = "Fresadora 01",
        operarioNombre: String = "Sara",
        onAnalyzed: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAnalyzing = true) }

            val horaActual = getCurrentTime()
            val (duracionMinutosCalculada, duracionSegundosCalculada) = calcularDuraciones(horaActual)
            val finalDescripcion = if (descripcionText.isBlank()) "Paré la máquina" else descripcionText

            // 1. Llamada a Gemini en IO Thread
            val iaResult = withContext(Dispatchers.IO) {
                clasificarConGemini(finalDescripcion)
            }

            val rawCat = iaResult.optString("categoria", IACategorias.FALLA_TECNICA)
            val categoriaNormalizada = IACategorias.TODAS.find { 
                it.equals(rawCat, ignoreCase = true) || 
                it.replace("ó", "o").replace("í", "i").equals(rawCat.replace("ó", "o").replace("í", "i"), ignoreCase = true) 
            } ?: rawCat

            val nuevoEvento = Evento(
                id = "EVT-${(1000..9999).random()}",
                fecha = getCurrentDate(),
                horaInicio = horaActual,
                horaFin = horaActual,
                operario = operarioNombre,
                ordenProduccion = ordenCodigo,
                maquina = maquinaNombre,
                tipoEvento = "Interrupción",
                descripcion = iaResult.optString("descripcion", finalDescripcion),
                categoria = categoriaNormalizada,
                duracionMinutos = 0, // Inicia con 0, se actualizará al reanudar
                duracionSegundos = 0,
                confianzaIA = "${(iaResult.optDouble("confianza", 0.95) * 100).toInt()}%"
            )

            // 2. Enviar a Google Sheets en IO Thread
            withContext(Dispatchers.IO) {
                // Primero, si hubo un evento anterior, enviamos su finalización con la duración real
                val lastEvent = _uiState.value.eventosList.firstOrNull()
                if (lastEvent != null && (duracionMinutosCalculada > 0 || duracionSegundosCalculada > 0)) {
                    val completedEvent = lastEvent.copy(
                        id = "EVT-FIN-${lastEvent.id}",
                        horaFin = horaActual,
                        duracionMinutos = duracionMinutosCalculada,
                        duracionSegundos = duracionSegundosCalculada,
                        descripcion = "Cierre de ${lastEvent.categoria} (${duracionMinutosCalculada}m ${duracionSegundosCalculada}s)"
                    )
                    enviarAGoogleSheets(completedEvent)
                }

                // Luego enviamos el nuevo evento que acaba de empezar
                enviarAGoogleSheets(nuevoEvento)
            }

            // 3. Actualizar UI
            _uiState.update { state ->
                val updatedList = state.eventosList.toMutableList()
                if (updatedList.isNotEmpty() && (duracionMinutosCalculada > 0 || duracionSegundosCalculada > 0)) {
                    updatedList[0] = updatedList[0].copy(
                        horaFin = horaActual,
                        duracionMinutos = duracionMinutosCalculada,
                        duracionSegundos = duracionSegundosCalculada
                    )
                }
                state.copy(
                    isAnalyzing = false,
                    ultimoEventoReportado = nuevoEvento,
                    eventosList = listOf(nuevoEvento) + updatedList
                )
            }

            onAnalyzed()
        }
    }

    fun reportarEventoDirecto(
        ordenCodigo: String,
        maquinaNombre: String,
        tipoEvento: String,
        descripcion: String,
        categoria: String,
        operarioNombre: String = "Sara"
    ) {
        viewModelScope.launch {
            val horaActual = getCurrentTime()
            val (duracionMinutosCalculada, duracionSegundosCalculada) = calcularDuraciones(horaActual)

            val nuevoEvento = Evento(
                id = "EVT-${(1000..9999).random()}",
                fecha = getCurrentDate(),
                horaInicio = horaActual,
                horaFin = horaActual,
                operario = operarioNombre,
                ordenProduccion = ordenCodigo,
                maquina = maquinaNombre,
                tipoEvento = tipoEvento,
                descripcion = descripcion,
                categoria = categoria,
                duracionMinutos = 0, // Inicia con 0
                duracionSegundos = 0,
                confianzaIA = "100%"
            )

            withContext(Dispatchers.IO) {
                // Si hubo un evento anterior, lo completamos
                val lastEvent = _uiState.value.eventosList.firstOrNull()
                if (lastEvent != null && (duracionMinutosCalculada > 0 || duracionSegundosCalculada > 0)) {
                    val completedEvent = lastEvent.copy(
                        id = "EVT-FIN-${lastEvent.id}",
                        horaFin = horaActual,
                        duracionMinutos = duracionMinutosCalculada,
                        duracionSegundos = duracionSegundosCalculada,
                        descripcion = "Cierre de ${lastEvent.categoria} (${duracionMinutosCalculada}m ${duracionSegundosCalculada}s)"
                    )
                    enviarAGoogleSheets(completedEvent)
                }
                
                enviarAGoogleSheets(nuevoEvento)
            }

            _uiState.update { state ->
                val updatedList = state.eventosList.toMutableList()
                if (updatedList.isNotEmpty() && (duracionMinutosCalculada > 0 || duracionSegundosCalculada > 0)) {
                    updatedList[0] = updatedList[0].copy(
                        horaFin = horaActual,
                        duracionMinutos = duracionMinutosCalculada,
                        duracionSegundos = duracionSegundosCalculada
                    )
                }
                state.copy(
                    ultimoEventoReportado = nuevoEvento,
                    eventosList = listOf(nuevoEvento) + updatedList
                )
            }
        }
    }

    private fun clasificarConGemini(mensaje: String): JSONObject {
        try {
            if (GEMINI_API_KEY == "TU_API_KEY_AQUI") {
                return JSONObject().apply {
                    put("categoria", IACategorias.LOGISTICA_MATERIALES)
                    put("descripcion", mensaje)
                    put("confianza", 0.95)
                }
            }

            val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-lite:generateContent?key=$GEMINI_API_KEY")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.connectTimeout = 3000
            connection.readTimeout = 4000
            connection.doOutput = true

            val prompt = """
                Eres el clasificador de IA de planta industrial de SuperBrix.
                Mensaje: "$mensaje"
                Debes clasificarla en EXACTAMENTE UNA de estas 8 categorías:
                1. Producción Activa
                2. Alistamiento y Preparación (Setup)
                3. Espera de Materiales / Logística
                4. Falla Técnica / Mantenimiento
                5. Calidad y Aprobación
                6. Instrucciones / Coordinación
                7. Salud y Seguridad (Accidentes)
                8. Otros / Novedad Excepcional

                Ejemplos de clasificación:
                - "se paró la máquina por un ruido extraño" -> Falla Técnica / Mantenimiento
                - "me corté el dedo" -> Salud y Seguridad (Accidentes)
                - "voy al baño" -> Otros / Novedad Excepcional
                - "estoy esperando la broca" -> Espera de Materiales / Logística

                Responde ÚNICAMENTE un bloque JSON válido con este formato (sin markdown):
                {"categoria": "Nombre Exacto", "descripcion": "Explicación breve", "confianza": 0.95}
            """.trimIndent()

            val payload = JSONObject().apply {
                put("contents", JSONArray().put(JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().apply {
                        put("text", prompt)
                    }))
                }))
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.1)
                    put("responseMimeType", "application/json")
                })
            }

            OutputStreamWriter(connection.outputStream).use { it.write(payload.toString()) }

            if (connection.responseCode == 200) {
                val responseString = connection.inputStream.bufferedReader().use { it.readText() }
                val geminiResponse = JSONObject(responseString)
                val text = geminiResponse.getJSONArray("candidates")
                    .getJSONObject(0).getJSONObject("content").getJSONArray("parts")
                    .getJSONObject(0).getString("text")
                return JSONObject(text)
            } else {
                Log.e("GeminiError", "Status: ${connection.responseCode}")
            }
        } catch (e: Exception) {
            Log.e("GeminiError", e.message ?: "Error desconocido")
        }

        // Smart local fallback
        val lower = mensaje.lowercase()
        val cat = when {
            lower.contains("material") || lower.contains("broca") || lower.contains("insumo") -> IACategorias.LOGISTICA_MATERIALES
            lower.contains("produccion") || lower.contains("inicio") -> IACategorias.PRODUCCION_ACTIVA
            lower.contains("prepar") || lower.contains("setup") || lower.contains("ajuste") -> IACategorias.SETUP
            lower.contains("inspector") || lower.contains("calidad") || lower.contains("planos") -> IACategorias.CALIDAD
            lower.contains("jefe") || lower.contains("supervisor") || lower.contains("instruccion") -> IACategorias.COORDINACION
            lower.contains("accidente") || lower.contains("corte") || lower.contains("sangre") || lower.contains("salud") -> IACategorias.SALUD_SEGURIDAD
            lower.contains("baño") || lower.contains("personal") || lower.contains("otro") -> IACategorias.OTROS
            else -> IACategorias.FALLA_TECNICA
        }

        return JSONObject().apply {
            put("categoria", cat)
            put("descripcion", mensaje)
            put("confianza", 0.90)
        }
    }

    private fun enviarAGoogleSheets(evento: Evento) {
        if (GOOGLE_APPS_SCRIPT_URL == "TU_URL_DE_APPS_SCRIPT_AQUI") return

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
                put("estado", "PROCESADO")
            }

            val client = OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .build()

            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val requestBody = payload.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(GOOGLE_APPS_SCRIPT_URL)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string()
            Log.d("Sheets", "Status Code: ${response.code}, Response: $responseString")
        } catch (e: Exception) {
            Log.e("SheetsError", e.message ?: "Error enviando a Sheets con OkHttp")
        }
    }

    fun seleccionarEvento(evento: Evento) {
        _uiState.update { it.copy(eventoSeleccionado = evento) }
    }

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun calcularDuraciones(horaActual: String): Pair<Int, Int> {
        val lastEvent = _uiState.value.eventosList.firstOrNull() ?: return Pair(0, 0)
        return try {
            val sdf = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
            val d1 = sdf.parse(lastEvent.horaInicio)
            val d2 = sdf.parse(horaActual)
            if (d1 != null && d2 != null) {
                val diffMs = d2.time - d1.time
                val totalSeconds = (diffMs / 1000).coerceAtLeast(0).toInt()
                val minutes = totalSeconds / 60
                val seconds = totalSeconds % 60
                Pair(minutes, seconds)
            } else Pair(0, 0)
        } catch (e: Exception) {
            Pair(0, 0)
        }
    }
}
