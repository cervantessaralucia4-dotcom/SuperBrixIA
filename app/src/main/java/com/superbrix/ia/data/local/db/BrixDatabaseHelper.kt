package com.superbrix.ia.data.local.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.superbrix.ia.data.model.Evento
import com.superbrix.ia.data.model.Operario
import com.superbrix.ia.data.model.SyncStatus

/**
 * Base de Datos Local SQLite Industrial para BRIX360.
 *
 * Provee almacenamiento persistente offline para:
 * - Tabla 'eventos': Registro de labores, paros, duración y estado de sincronización (PENDIENTE, ENVIADO, ERROR).
 * - Tabla 'operarios': Catálogo de operarios y áreas de manufactura.
 *
 * No depende de generadores externos, garantizando compatibilidad 100%
 * desde Android 7.0 (API 24) hasta Android 16 (API 36).
 */
class BrixDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "brix360_industrial.db"
        const val DATABASE_VERSION = 1

        // Tabla Eventos
        const val TABLE_EVENTOS = "eventos"
        const val COL_EVT_ID = "id"
        const val COL_EVT_FECHA = "fecha"
        const val COL_EVT_HORA_INICIO = "hora_inicio"
        const val COL_EVT_HORA_FIN = "hora_fin"
        const val COL_EVT_OPERARIO = "operario"
        const val COL_EVT_ORDEN = "orden_produccion"
        const val COL_EVT_MAQUINA = "maquina"
        const val COL_EVT_TIPO = "tipo_evento"
        const val COL_EVT_DESCRIPCION = "descripcion"
        const val COL_EVT_CATEGORIA = "categoria"
        const val COL_EVT_DUR_MIN = "duracion_minutos"
        const val COL_EVT_DUR_SEG = "duracion_segundos"
        const val COL_EVT_CONFIANZA = "confianza_ia"
        const val COL_EVT_CAT_CORREGIDA = "categoria_corregida"
        const val COL_EVT_CAT_ORIGINAL = "categoria_original_ia"
        const val COL_EVT_SYNC_STATUS = "sync_status"
        const val COL_EVT_FOTO_URI = "foto_uri"
        const val COL_EVT_ES_SEGURIDAD = "es_novedad_seguridad"
        const val COL_EVT_ANULADO = "anulado"
        const val COL_EVT_MOTIVO_ANULACION = "motivo_anulacion"
        const val COL_EVT_TIMESTAMP = "created_at"

        // Tabla Operarios
        const val TABLE_OPERARIOS = "operarios"
        const val COL_OP_ID = "id"
        const val COL_OP_NOMBRE = "nombre"
        const val COL_OP_CEDULA = "cedula"
        const val COL_OP_CODIGO = "codigo"
        const val COL_OP_ROL = "rol"
        const val COL_OP_AREA = "area"
        const val COL_OP_CT = "centro_trabajo"
        const val COL_OP_ESTADO = "estado"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createEventosTable = """
            CREATE TABLE $TABLE_EVENTOS (
                $COL_EVT_ID TEXT PRIMARY KEY,
                $COL_EVT_FECHA TEXT NOT NULL,
                $COL_EVT_HORA_INICIO TEXT NOT NULL,
                $COL_EVT_HORA_FIN TEXT,
                $COL_EVT_OPERARIO TEXT NOT NULL,
                $COL_EVT_ORDEN TEXT NOT NULL,
                $COL_EVT_MAQUINA TEXT NOT NULL,
                $COL_EVT_TIPO TEXT NOT NULL,
                $COL_EVT_DESCRIPCION TEXT NOT NULL,
                $COL_EVT_CATEGORIA TEXT NOT NULL,
                $COL_EVT_DUR_MIN INTEGER NOT NULL DEFAULT 0,
                $COL_EVT_DUR_SEG INTEGER NOT NULL DEFAULT 0,
                $COL_EVT_CONFIANZA TEXT NOT NULL DEFAULT '95%',
                $COL_EVT_CAT_CORREGIDA INTEGER NOT NULL DEFAULT 0,
                $COL_EVT_CAT_ORIGINAL TEXT,
                $COL_EVT_SYNC_STATUS TEXT NOT NULL DEFAULT 'PENDIENTE',
                $COL_EVT_FOTO_URI TEXT,
                $COL_EVT_ES_SEGURIDAD INTEGER NOT NULL DEFAULT 0,
                $COL_EVT_ANULADO INTEGER NOT NULL DEFAULT 0,
                $COL_EVT_MOTIVO_ANULACION TEXT,
                $COL_EVT_TIMESTAMP INTEGER NOT NULL
            );
        """.trimIndent()

        val createOperariosTable = """
            CREATE TABLE $TABLE_OPERARIOS (
                $COL_OP_ID TEXT PRIMARY KEY,
                $COL_OP_NOMBRE TEXT NOT NULL,
                $COL_OP_CEDULA TEXT,
                $COL_OP_CODIGO TEXT NOT NULL UNIQUE,
                $COL_OP_ROL TEXT NOT NULL DEFAULT 'Operario',
                $COL_OP_AREA TEXT NOT NULL,
                $COL_OP_CT TEXT,
                $COL_OP_ESTADO TEXT NOT NULL DEFAULT 'Activo'
            );
        """.trimIndent()

        db.execSQL(createEventosTable)
        db.execSQL(createOperariosTable)

        // Seed inicial de operarios
        seedOperarios(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EVENTOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_OPERARIOS")
        onCreate(db)
    }

    private fun seedOperarios(db: SQLiteDatabase) {
        val ops = listOf(
            Operario(id = "1", nombre = "Juan Pérez", cedula = "72234620", codigo = "COD-100", rol = "Operario CNC", area = "CNC", centroTrabajo = "CT-Mecanizado"),
            Operario(id = "2", nombre = "Sara Gómez", cedula = "72234621", codigo = "COD-101", rol = "Operaria Ensamble", area = "Ensamble 1", centroTrabajo = "CT-Soldadura"),
            Operario(id = "3", nombre = "Carlos Mendoza", cedula = "72234622", codigo = "COD-102", rol = "Operario Pintura", area = "Pintura", centroTrabajo = "CT-Pintura")
        )
        ops.forEach { op ->
            val values = ContentValues().apply {
                put(COL_OP_ID, op.id)
                put(COL_OP_NOMBRE, op.nombre)
                put(COL_OP_CEDULA, op.cedula)
                put(COL_OP_CODIGO, op.codigo)
                put(COL_OP_ROL, op.rol)
                put(COL_OP_AREA, op.area)
                put(COL_OP_CT, op.centroTrabajo)
                put(COL_OP_ESTADO, op.estado)
            }
            db.insert(TABLE_OPERARIOS, null, values)
        }
    }

    // CRUD Eventos
    fun insertEvento(evento: Evento) {
        writableDatabase.use { db ->
            val values = ContentValues().apply {
                put(COL_EVT_ID, evento.id)
                put(COL_EVT_FECHA, evento.fecha)
                put(COL_EVT_HORA_INICIO, evento.horaInicio)
                put(COL_EVT_HORA_FIN, evento.horaFin)
                put(COL_EVT_OPERARIO, evento.operario)
                put(COL_EVT_ORDEN, evento.ordenProduccion)
                put(COL_EVT_MAQUINA, evento.maquina)
                put(COL_EVT_TIPO, evento.tipoEvento)
                put(COL_EVT_DESCRIPCION, evento.descripcion)
                put(COL_EVT_CATEGORIA, evento.categoria)
                put(COL_EVT_DUR_MIN, evento.duracionMinutos)
                put(COL_EVT_DUR_SEG, evento.duracionSegundos)
                put(COL_EVT_CONFIANZA, evento.confianzaIA)
                put(COL_EVT_CAT_CORREGIDA, if (evento.categoriaCorregida) 1 else 0)
                put(COL_EVT_CAT_ORIGINAL, evento.categoriaOriginalIA)
                put(COL_EVT_SYNC_STATUS, evento.syncStatus.name)
                put(COL_EVT_FOTO_URI, evento.fotoUri)
                put(COL_EVT_ES_SEGURIDAD, if (evento.esNovedadSeguridad) 1 else 0)
                put(COL_EVT_ANULADO, if (evento.anulado) 1 else 0)
                put(COL_EVT_MOTIVO_ANULACION, evento.motivoAnulacion)
                put(COL_EVT_TIMESTAMP, System.currentTimeMillis())
            }
            db.insertWithOnConflict(TABLE_EVENTOS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        }
    }

    fun updateEvento(evento: Evento) {
        insertEvento(evento)
    }

    fun getAllEventos(): List<Evento> {
        val list = mutableListOf<Evento>()
        readableDatabase.rawQuery("SELECT * FROM $TABLE_EVENTOS ORDER BY $COL_EVT_TIMESTAMP DESC", null).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(cursorToEvento(cursor))
            }
        }
        return list
    }

    fun getPendingEventos(): List<Evento> {
        val list = mutableListOf<Evento>()
        readableDatabase.rawQuery("SELECT * FROM $TABLE_EVENTOS WHERE $COL_EVT_SYNC_STATUS = 'PENDIENTE'", null).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(cursorToEvento(cursor))
            }
        }
        return list
    }

    fun markEventAsSynced(eventoId: String) {
        writableDatabase.use { db ->
            val values = ContentValues().apply {
                put(COL_EVT_SYNC_STATUS, SyncStatus.ENVIADO.name)
            }
            db.update(TABLE_EVENTOS, values, "$COL_EVT_ID = ?", arrayOf(eventoId))
        }
    }

    fun clearAllEventos() {
        writableDatabase.use { db ->
            db.delete(TABLE_EVENTOS, null, null)
        }
    }

    private fun cursorToEvento(cursor: Cursor): Evento {
        return Evento(
            id = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVT_ID)),
            fecha = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVT_FECHA)),
            horaInicio = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVT_HORA_INICIO)),
            horaFin = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVT_HORA_FIN)),
            operario = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVT_OPERARIO)),
            ordenProduccion = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVT_ORDEN)),
            maquina = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVT_MAQUINA)),
            tipoEvento = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVT_TIPO)),
            descripcion = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVT_DESCRIPCION)),
            categoria = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVT_CATEGORIA)),
            duracionMinutos = cursor.getInt(cursor.getColumnIndexOrThrow(COL_EVT_DUR_MIN)),
            duracionSegundos = cursor.getInt(cursor.getColumnIndexOrThrow(COL_EVT_DUR_SEG)),
            confianzaIA = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVT_CONFIANZA)),
            categoriaCorregida = cursor.getInt(cursor.getColumnIndexOrThrow(COL_EVT_CAT_CORREGIDA)) == 1,
            categoriaOriginalIA = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVT_CAT_ORIGINAL)),
            syncStatus = try {
                SyncStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVT_SYNC_STATUS)))
            } catch (e: Exception) {
                SyncStatus.PENDIENTE
            },
            fotoUri = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVT_FOTO_URI)),
            esNovedadSeguridad = cursor.getInt(cursor.getColumnIndexOrThrow(COL_EVT_ES_SEGURIDAD)) == 1,
            anulado = cursor.getInt(cursor.getColumnIndexOrThrow(COL_EVT_ANULADO)) == 1,
            motivoAnulacion = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVT_MOTIVO_ANULACION))
        )
    }

    // CRUD Operarios
    fun getAllOperarios(): List<Operario> {
        val list = mutableListOf<Operario>()
        readableDatabase.rawQuery("SELECT * FROM $TABLE_OPERARIOS ORDER BY $COL_OP_NOMBRE ASC", null).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(
                    Operario(
                        id = cursor.getString(cursor.getColumnIndexOrThrow(COL_OP_ID)),
                        nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_OP_NOMBRE)),
                        cedula = cursor.getString(cursor.getColumnIndexOrThrow(COL_OP_CEDULA)) ?: "",
                        codigo = cursor.getString(cursor.getColumnIndexOrThrow(COL_OP_CODIGO)),
                        rol = cursor.getString(cursor.getColumnIndexOrThrow(COL_OP_ROL)),
                        area = cursor.getString(cursor.getColumnIndexOrThrow(COL_OP_AREA)),
                        centroTrabajo = cursor.getString(cursor.getColumnIndexOrThrow(COL_OP_CT)) ?: "",
                        estado = cursor.getString(cursor.getColumnIndexOrThrow(COL_OP_ESTADO))
                    )
                )
            }
        }
        return list
    }

    fun insertOperario(op: Operario) {
        writableDatabase.use { db ->
            val values = ContentValues().apply {
                put(COL_OP_ID, op.id)
                put(COL_OP_NOMBRE, op.nombre)
                put(COL_OP_CEDULA, op.cedula)
                put(COL_OP_CODIGO, op.codigo)
                put(COL_OP_ROL, op.rol)
                put(COL_OP_AREA, op.area)
                put(COL_OP_CT, op.centroTrabajo)
                put(COL_OP_ESTADO, op.estado)
            }
            db.insertWithOnConflict(TABLE_OPERARIOS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        }
    }

    fun deleteOperario(opId: String) {
        writableDatabase.use { db ->
            db.delete(TABLE_OPERARIOS, "$COL_OP_ID = ?", arrayOf(opId))
        }
    }
}
