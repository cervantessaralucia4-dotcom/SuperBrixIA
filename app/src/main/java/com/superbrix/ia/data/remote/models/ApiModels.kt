package com.superbrix.ia.data.remote.models

import com.google.gson.annotations.SerializedName

data class ClasificacionRequest(
    @SerializedName("mensaje") val mensaje: String
)

data class ClasificacionResponse(
    @SerializedName("categoria") val categoria: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("confianza") val confianza: Double
)

data class EventoSyncDto(
    @SerializedName("id") val id: String,
    @SerializedName("fecha") val fecha: String,
    @SerializedName("hora_inicio") val horaInicio: String,
    @SerializedName("hora_fin") val horaFin: String?,
    @SerializedName("operario_id") val operarioId: String,
    @SerializedName("orden_id") val ordenId: String,
    @SerializedName("maquina_id") val maquinaId: String,
    @SerializedName("tipo_evento") val tipoEvento: String,
    @SerializedName("mensaje") val mensaje: String?,
    @SerializedName("categoria") val categoria: String?,
    @SerializedName("descripcion") val descripcion: String?,
    @SerializedName("confianza_ia") val confianzaIa: Double?,
    @SerializedName("duracion_minutos") val duracionMinutos: Int?,
    @SerializedName("duracion_segundos") val duracionSegundos: Int? = 0,
    @SerializedName("estado") val estado: String = "PROCESADO",
    @SerializedName("categoria_corregida") val categoriaCorregida: Boolean = false,
    @SerializedName("categoria_original_ia") val categoriaOriginalIa: String? = null,
    @SerializedName("foto_uri") val fotoUri: String? = null
)
