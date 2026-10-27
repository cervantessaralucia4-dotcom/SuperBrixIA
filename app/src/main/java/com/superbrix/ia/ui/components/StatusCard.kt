package com.superbrix.ia.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbrix.ia.ui.theme.*

/**
 * Tarjeta de Estado Industrial Principal con cronómetro en vivo y pulso visual.
 *
 * En estado "Produciendo", muestra una animación de pulso suave en el halo indicador.
 * Las cifras del cronómetro utilizan TimerTextStyle (monoespaciado y tabular)
 * para lectura rápida sin saltos de layout.
 */
@Composable
fun StatusCard(
    isLaborActive: Boolean,
    isPaused: Boolean,
    statusText: String,
    timerFormatted: String,
    timerLabel: String = if (isPaused) "TIEMPO EN PAUSA" else "TIEMPO TRANSCURRIDO",
    modifier: Modifier = Modifier
) {
    // Animación de pulso continuo cuando está produciendo
    val infiniteTransition = rememberInfiniteTransition(label = "PulseTransition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isLaborActive) 1.35f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = if (isLaborActive) 0.15f else 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
    )

    val (accentColor, bgColor, stateLabel, stateIcon) = when {
        isLaborActive -> Tuple4(
            BrixStateProducing,
            BrixStateProducingBg,
            "PRODUCIENDO",
            Icons.Default.PrecisionManufacturing
        )
        isPaused -> Tuple4(
            BrixStatePaused,
            BrixStatePausedBg,
            "EN PAUSA",
            Icons.Default.PauseCircle
        )
        else -> Tuple4(
            BrixTextSecondary,
            BrixDarkSurfaceVariant,
            "EN ESPERA",
            Icons.Default.HourglassBottom
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = BrixDarkSurface),
        border = BorderStroke(1.5.dp, if (isLaborActive) accentColor.copy(alpha = 0.6f) else BrixDarkBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header del Estado con Badge y Pulso
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(contentAlignment = Alignment.Center) {
                        // Halo de pulso
                        if (isLaborActive) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .scale(pulseScale)
                                    .clip(CircleShape)
                                    .background(accentColor.copy(alpha = pulseAlpha))
                            )
                        }
                        // Núcleo del punto
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(accentColor)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = stateLabel,
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        letterSpacing = 1.sp
                    )
                }

                // Icono semántico del estado
                Icon(
                    imageVector = stateIcon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Cronómetro Principal
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(InputShape)
                    .background(BrixDarkSurfaceVariant)
                    .padding(vertical = 16.dp, horizontal = 12.dp)
            ) {
                Text(
                    text = timerLabel,
                    style = Typography.labelSmall,
                    color = BrixTextSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = timerFormatted,
                    style = TimerTextStyle,
                    color = BrixTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mensaje descriptivo de estado
            Text(
                text = statusText,
                style = Typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = BrixTextSecondary,
                maxLines = 2
            )
        }
    }
}

private data class Tuple4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
