package com.superbrix.ia.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Paleta de Colores Oficial BRIX360 (SuperBrix).
 * Alineada con la identidad visual del logo (cubo con degradado violeta -> cian -> naranja
 * sobre fondo azul noche industrial).
 */

// Fondos y Superficies (Dark Theme por defecto)
val BrixDarkBackground = Color(0xFF0A192F)       // Fondo azul noche base
val BrixDarkSurface = Color(0xFF111F38)          // Superficie de tarjetas / contenedores
val BrixDarkSurfaceVariant = Color(0xFF1E293B)   // Superficie secundaria / elevación sutil
val BrixDarkBorder = Color(0xFF283A5A)           // Borde sutil para tarjetas industriales

// Textos
val BrixTextPrimary = Color(0xFFF4F6F9)          // Texto principal de alto contraste
val BrixTextSecondary = Color(0xFF94A3B8)        // Texto secundario / etiquetas descriptivas
val BrixTextDisabled = Color(0xFF64748B)         // Texto inactivo / placeholder

// Acento Primario (Gradiente Violeta -> Cian)
val BrixVioletPrimary = Color(0xFF7C3AED)        // Violeta tecnológico
val BrixCyanAccent = Color(0xFF00C2FF)           // Cian vibrante
val BrixPrimaryGradient = Brush.horizontalGradient(
    listOf(BrixVioletPrimary, BrixCyanAccent)
)
val BrixPrimaryGradientVertical = Brush.verticalGradient(
    listOf(BrixVioletPrimary, BrixCyanAccent)
)

// Acción Crítica / Energía
val BrixOrangeAction = Color(0xFFFF8A00)         // Naranja industrial
val BrixOrangeLight = Color(0xFFFFB84D)
val BrixOrangeGradient = Brush.horizontalGradient(
    listOf(Color(0xFFFF9D2E), Color(0xFFFF7A00))
)

// Estados Semánticos de Planta (OEE y Seguridad)
val BrixStateProducing = Color(0xFF1DB954)       // Verde: Produciendo / Activo
val BrixStateProducingBg = Color(0x221DB954)     // Fondo translúcido de pulso

val BrixStatePaused = Color(0xFFF39C12)          // Amarillo/Ámbar: En pausa / Interrupción leve
val BrixStatePausedBg = Color(0x22F39C12)

val BrixStateCritical = Color(0xFFE63946)        // Rojo: Falla Técnica / Salud y Seguridad
val BrixStateCriticalBg = Color(0x22E63946)

val BrixStateInfo = Color(0xFF0288D1)            // Azul técnico: Info / Coordinación
val BrixStateInfoBg = Color(0x220288D1)

// Colores para Tema Claro (Opcional vía Configuración)
val BrixLightBackground = Color(0xFFF4F6F9)
val BrixLightSurface = Color(0xFFFFFFFF)
val BrixLightSurfaceVariant = Color(0xFFE2E8F0)
val BrixLightTextPrimary = Color(0xFF0A192F)
val BrixLightTextSecondary = Color(0xFF475569)
val BrixLightBorder = Color(0xFFCBD5E1)

// Compatibilidad con referencias previas
val SuperBrixNavyPrimary = BrixDarkBackground
val SuperBrixBlueAccent = BrixOrangeAction
val SuperBrixDarkSurface = BrixDarkBackground
val IndustrialGreenActive = BrixStateProducing
val IndustrialOrangePause = BrixStatePaused
val IndustrialRedAlert = BrixStateCritical
val IndustrialBlueTech = BrixStateInfo
val IndustrialGrayBackground = BrixDarkBackground
val IndustrialCardSurface = BrixDarkSurface
val IndustrialTextPrimary = BrixTextPrimary
val IndustrialTextSecondary = BrixTextSecondary
val IndustrialBorderGray = BrixDarkBorder
