package com.superbrix.ia.data.model

data class Operario(
    val id: String = "",
    val nombre: String = "",
    val cedula: String = "",
    val codigo: String = "",
    val rol: String = "Operario",
    val area: String = "CNC",
    val centroTrabajo: String = "",
    val estado: String = "Activo"
)
