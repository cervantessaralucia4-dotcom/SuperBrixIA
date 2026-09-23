package com.superbrix.ia.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SuperBrixBlueAccent,
    secondary = IndustrialBlueTech,
    tertiary = IndustrialGreenActive,
    background = SuperBrixDarkSurface,
    surface = Color(0xFF1E293B),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFF4F6F9), // Text should be light on dark bg
    onSurface = Color(0xFFF4F6F9) // Text should be light on dark surface
)

private val LightColorScheme = lightColorScheme(
    primary = SuperBrixNavyPrimary,
    secondary = SuperBrixBlueAccent,
    tertiary = IndustrialGreenActive,
    background = IndustrialGrayBackground,
    surface = IndustrialCardSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = IndustrialTextPrimary,
    onSurface = IndustrialTextPrimary,
    outline = IndustrialBorderGray
)

@Composable
fun SuperBrixIATheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
