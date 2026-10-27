package com.superbrix.ia.data.repository

import android.content.Context
import com.superbrix.ia.data.local.db.BrixDatabaseHelper
import com.superbrix.ia.data.model.Evento
import com.superbrix.ia.data.model.IACategorias
import com.superbrix.ia.data.model.SyncStatus
import com.superbrix.ia.data.sync.SyncWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio de Eventos Operativos de Planta para BRIX360.
 *
 * Implementa el patrón 'Offline-First':
 * - Todas las lecturas y escrituras ocurren en SQLite local inmediatamente.
 * - Cada inserción o modificación encola una sincronización en segundo plano con WorkManager.
 */
class EventoRepository(private val context: Context) {

    private val dbHelper = BrixDatabaseHelper(context)

    suspend fun getAllEventos(): List<Evento> = withContext(Dispatchers.IO) {
        dbHelper.getAllEventos()
    }

    suspend fun getPendingEventos(): List<Evento> = withContext(Dispatchers.IO) {
        dbHelper.getPendingEventos()
    }

    suspend fun insertEvento(evento: Evento) = withContext(Dispatchers.IO) {
        dbHelper.insertEvento(evento)
        SyncWorker.enqueueImmediateSync(context)
    }

    suspend fun updateEvento(evento: Evento) = withContext(Dispatchers.IO) {
        dbHelper.updateEvento(evento)
        SyncWorker.enqueueImmediateSync(context)
    }

    suspend fun anularEvento(eventoId: String, motivo: String): Evento? = withContext(Dispatchers.IO) {
        val existing = dbHelper.getAllEventos().find { it.id == eventoId } ?: return@withContext null
        val anulado = existing.copy(
            anulado = true,
            motivoAnulacion = motivo,
            syncStatus = SyncStatus.PENDIENTE
        )
        dbHelper.updateEvento(anulado)
        SyncWorker.enqueueImmediateSync(context)
        anulado
    }

    suspend fun corregirCategoria(eventoId: String, nuevaCategoria: String): Evento? = withContext(Dispatchers.IO) {
        val existing = dbHelper.getAllEventos().find { it.id == eventoId } ?: return@withContext null
        val corregido = existing.copy(
            categoria = nuevaCategoria,
            categoriaCorregida = (nuevaCategoria != existing.categoriaOriginalIA),
            esNovedadSeguridad = (nuevaCategoria == IACategorias.SALUD_SEGURIDAD),
            syncStatus = SyncStatus.PENDIENTE
        )
        dbHelper.updateEvento(corregido)
        SyncWorker.enqueueImmediateSync(context)
        corregido
    }

    suspend fun clearAllEventos() = withContext(Dispatchers.IO) {
        dbHelper.clearAllEventos()
    }

    fun triggerSync() {
        SyncWorker.enqueueImmediateSync(context)
    }
}
