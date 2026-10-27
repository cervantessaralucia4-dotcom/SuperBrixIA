package com.superbrix.ia.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbrix.ia.R
import com.superbrix.ia.ui.components.PrimaryButton
import com.superbrix.ia.ui.components.SecondaryButton
import com.superbrix.ia.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Pantalla de Análisis de IA para BRIX360.
 *
 * Características:
 * - Animación de pulso continuo con el cubo del logo de BRIX360 (cero emojis).
 * - Temporizador con manejo de timeout industrial a los 10 segundos.
 * - Ofrece opción de reintento o guardado inmediato mediante clasificación heurística de respaldo.
 */
@Composable
fun AnalizandoScreen(
    onRetry: () -> Unit = {},
    onSaveFallback: () -> Unit = {}
) {
    var isTimedOut by remember { mutableStateOf(false) }

    // Detección de timeout a los 10 segundos
    LaunchedEffect(Unit) {
        delay(10000L)
        isTimedOut = true
    }

    // Animación de pulso suave sobre el logo
    val infiniteTransition = rememberInfiniteTransition(label = "CubePulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CubeScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrixDarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .widthIn(max = 500.dp)
                .padding(32.dp)
        ) {
            // Contenedor animado del Cubo BRIX360 (sin emojis)
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(pulseScale)
                    .shadow(20.dp, CircleShape, ambientColor = BrixVioletPrimary, spotColor = BrixCyanAccent)
                    .clip(CircleShape)
                    .background(BrixDarkSurface),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_brix360),
                    contentDescription = "Analizando Novedad",
                    modifier = Modifier
                        .size(115.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = if (isTimedOut) "El análisis está tomando más tiempo" else "Analizando novedad...",
                style = Typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = BrixTextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (isTimedOut)
                    "La conexión con el backend es lenta o inestable en este sector de la planta."
                else
                    "Interpretando causa raíz, porcentaje de confianza y categoría industrial oficial.",
                style = Typography.bodyMedium,
                color = BrixTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            if (!isTimedOut) {
                CircularProgressIndicator(
                    color = BrixCyanAccent,
                    strokeWidth = 3.5.dp,
                    modifier = Modifier.size(44.dp)
                )
            } else {
                // Panel de Contingencia por Timeout
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PrimaryButton(
                        text = "GUARDAR CON CLASIFICACIÓN LOCAL",
                        onClick = onSaveFallback,
                        icon = Icons.Default.Save,
                        containerColor = BrixStateProducing
                    )

                    SecondaryButton(
                        text = "Reintentar con Servidor",
                        onClick = {
                            isTimedOut = false
                            onRetry()
                        },
                        icon = Icons.Default.Refresh
                    )
                }
            }
        }
    }
}
