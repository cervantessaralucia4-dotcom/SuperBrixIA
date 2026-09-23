package com.superbrix.ia.data.model

object IACategorias {
    const val PRODUCCION_ACTIVA = "Producción Activa"
    const val SETUP = "Alistamiento y Preparación (Setup)"
    const val LOGISTICA_MATERIALES = "Espera de Materiales / Logística"
    const val FALLA_TECNICA = "Falla Técnica / Mantenimiento"
    const val CALIDAD = "Calidad y Aprobación"
    const val COORDINACION = "Instrucciones / Coordinación"
    const val SALUD_SEGURIDAD = "Salud y Seguridad (Accidentes)"
    const val OTROS = "Otros / Novedad Excepcional"

    val TODAS = listOf(
        PRODUCCION_ACTIVA,
        SETUP,
        LOGISTICA_MATERIALES,
        FALLA_TECNICA,
        CALIDAD,
        COORDINACION,
        SALUD_SEGURIDAD,
        OTROS
    )
}

data class Evento(
    val id: String,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String? = null,
    val operario: String = "Sara",
    val ordenProduccion: String = "OP-001",
    val maquina: String = "Fresadora 01",
    val tipoEvento: String, // e.g. "Inicio de labor", "Interrupción"
    val descripcion: String,
    val categoria: String, // One of IACategorias
    val duracionMinutos: Int = 0,
    val duracionSegundos: Int = 0,
    val confianzaIA: String = "94%"
)
