package com.superbrix.ia.data.repository

import android.content.Context
import com.superbrix.ia.data.local.db.BrixDatabaseHelper
import com.superbrix.ia.data.model.Operario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio de Operarios de Planta para BRIX360.
 *
 * Administra el catálogo de operarios en SQLite local, garantizando
 * unicidad de códigos y persistencia entre sesiones.
 */
class OperarioRepository(context: Context) {

    private val dbHelper = BrixDatabaseHelper(context)

    suspend fun getAllOperarios(): List<Operario> = withContext(Dispatchers.IO) {
        dbHelper.getAllOperarios()
    }

    suspend fun insertOperario(operario: Operario) = withContext(Dispatchers.IO) {
        dbHelper.insertOperario(operario)
    }

    suspend fun deleteOperario(operarioId: String) = withContext(Dispatchers.IO) {
        dbHelper.deleteOperario(operarioId)
    }
}
