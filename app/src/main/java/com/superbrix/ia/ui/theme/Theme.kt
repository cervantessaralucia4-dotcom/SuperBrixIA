package com.superbrix.ia.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// CompositionLocal para consultar o forzar el modo oscuro en cualquier parte del árbol Compose
val LocalIsDarkTheme = staticCompositionLocalOf { true }

private val BrixDarkColorScheme = darkColorScheme(
    primary = BrixVioletPrimary,
    onPrimary = BrixTextPrimary,
    primaryContainer = BrixDarkSurfaceVariant,
    onPrimaryContainer = BrixCyanAccent,
    secondary = BrixCyanAccent,
    onSecondary = BrixDarkBackground,
    tertiary = BrixOrangeAction,
    onTertiary = BrixDarkBackground,
    background = BrixDarkBackground,
    onBackground = BrixTextPrimary,
    surface = BrixDarkSurface,
    onSurface = BrixTextPrimary,
    surfaceVariant = BrixDarkSurfaceVariant,
    onSurfaceVariant = BrixTextSecondary,
    outline = BrixDarkBorder,
    error = BrixStateCritical,
    onError = BrixTextPrimary
)

private val BrixLightColorScheme = lightColorScheme(
    primary = BrixVioletPrimary,
    onPrimary = BrixLightSurface,
    primaryContainer = BrixLightSurfaceVariant,
    onPrimaryContainer = BrixVioletPrimary,
    secondary = BrixCyanAccent,
    onSecondary = BrixLightTextPrimary,
    tertiary = BrixOrangeAction,
    onTertiary = BrixLightSurface,
    background = BrixLightBackground,
    onBackground = BrixLightTextPrimary,
    surface = BrixLightSurface,
    onSurface = BrixLightTextPrimary,
    surfaceVariant = BrixLightSurfaceVariant,
    onSurfaceVariant = BrixLightTextSecondary,
    outline = BrixLightBorder,
    error = BrixStateCritical,
    onError = BrixLightSurface
)

/**
 * Tema principal BRIX360.
 *
 * Configurado con MODO OSCURO POR DEFECTO para garantizar legibilidad,
 * descanso visual del operario en planta y contraste industrial óptimo.
 */
@Composable
fun SuperBrixIATheme(
    darkTheme: Boolean = true, // Modo oscuro activado por defecto
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) BrixDarkColorScheme else BrixLightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalIsDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}
