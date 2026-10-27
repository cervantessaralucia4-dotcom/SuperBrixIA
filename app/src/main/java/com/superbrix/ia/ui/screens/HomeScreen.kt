package com.superbrix.ia.ui.screens

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbrix.ia.ui.components.*
import com.superbrix.ia.ui.theme.*
import com.superbrix.ia.viewmodel.HomeUiState
import kotlinx.coroutines.delay

/**
 * Pantalla Principal de Control Operativo en Piso de Planta (BRIX360).
 *
 * Incluye:
 * - StatusCard con pulso en vivo cuando produce y cronómetro tabular monoespaciado.
 * - Tarjeta informativa de estación con OP, Área y Máquina.
 * - Botón primario dinámico gigante (64 dp) que guía al operario paso a paso.
 * - Diálogo de confirmación para finalizar labor.
 * - Acciones secundarias de cambio de estación, historial e inicio rápido.
 */
@Composable
fun HomeScreen(
    homeUiState: HomeUiState,
    onStartLaborClick: () -> Unit,
    onQuickStartClick: () -> Unit,
    onReportInterruptionClick: () -> Unit,
    onResumeLaborClick: () -> Unit,
    onFinishLaborClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val view = LocalView.current

    // Estado del diálogo de confirmación para finalizar labor
    var showFinishConfirmDialog by remember { mutableStateOf(false) }

    // Cronómetro en vivo (Tiempo de producción o pausa)
    var elapsedTimeFormatted by remember { mutableStateOf("00:00:00") }
    val isPaused = !homeUiState.isLaborActive && homeUiState.interruptionStartTime != null

    LaunchedEffect(homeUiState.isLaborActive, homeUiState.interruptionStartTime) {
        val startTime = homeUiState.interruptionStartTime ?: System.currentTimeMillis()
        while (true) {
            val totalSeconds = if (homeUiState.isLaborActive || isPaused) {
                ((System.currentTimeMillis() - startTime) / 1000).coerceAtLeast(0)
            } else 0L

            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60
            elapsedTimeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            delay(1000L)
        }
    }

    fun triggerHaptic() {
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrixDarkBackground)
            .verticalScroll(scrollState)
    ) {
        // Cabecera Industrial con saludo, operario y área
        Header(
            userName = homeUiState.operario.nombre,
            areaName = homeUiState.areaActiva,
            title = "Registro FO-A-MA-01",
            onProfileClick = onProfileClick
        )

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // 1. Tarjeta Grande de Estado y Cronómetro en Vivo
                StatusCard(
                    isLaborActive = homeUiState.isLaborActive,
                    isPaused = isPaused,
                    statusText = homeUiState.laborEstadoText,
                    timerFormatted = elapsedTimeFormatted,
                    timerLabel = if (isPaused) "TIEMPO EN INTERRUPCIÓN" else "TIEMPO DE PRODUCCIÓN"
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 2. Tarjeta de Operación (OP, Máquina, Área, Labor)
                OperationCard(
                    ordenCodigo = homeUiState.ordenActiva.codigo,
                    maquinaNombre = homeUiState.maquinaActiva.nombre,
                    areaNombre = homeUiState.areaActiva,
                    operacionNombre = homeUiState.tipoOperacionActiva
                )

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "ACCIÓN OPERATIVA PRINCIPAL",
                    style = Typography.labelSmall,
                    color = BrixTextSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 3. UN ÚNICO BOTÓN PRIMARIO ENORME (64 dp)
                when {
                    homeUiState.isLaborActive -> {
                        // Estado Produciendo -> Botón de Parada / Novedad (Naranja crítico)
                        PrimaryButton(
                            text = "REPORTAR INTERRUPCIÓN",
                            onClick = {
                                triggerHaptic()
                                onReportInterruptionClick()
                            },
                            icon = Icons.Default.PauseCircle,
                            containerColor = BrixOrangeAction,
                            modifier = Modifier.height(64.dp)
                        )
                    }
                    isPaused -> {
                        // Estado en Pausa -> Botón Reanudar (Degradado violeta-cian)
                        PrimaryButton(
                            text = "REANUDAR LABOR",
                            onClick = {
                                triggerHaptic()
                                onResumeLaborClick()
                            },
                            icon = Icons.Default.PlayArrow,
                            modifier = Modifier.height(64.dp)
                        )
                    }
                    else -> {
                        // Estado Sin Labor -> Botón Iniciar Rápido (Verde Producción)
                        PrimaryButton(
                            text = "INICIAR LABOR (${homeUiState.ordenActiva.codigo})",
                            onClick = {
                                triggerHaptic()
                                onQuickStartClick()
                            },
                            icon = Icons.Default.FlashOn,
                            containerColor = BrixStateProducing,
                            modifier = Modifier.height(64.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 4. Acciones Secundarias (Finalizar, Historial, Cambiar OP/Máquina)
                if (homeUiState.isLaborActive || isPaused) {
                    SecondaryButton(
                        text = "Finalizar Labor Actual",
                        onClick = {
                            triggerHaptic()
                            showFinishConfirmDialog = true
                        },
                        icon = Icons.Default.CheckCircle
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SecondaryButton(
                        text = "Cambiar OP / Máquina",
                        onClick = onStartLaborClick,
                        icon = Icons.Default.SyncAlt,
                        modifier = Modifier.weight(1f)
                    )

                    SecondaryButton(
                        text = "Historial",
                        onClick = onHistoryClick,
                        icon = Icons.Default.History,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }

    // Diálogo de Confirmación para Finalizar Labor
    if (showFinishConfirmDialog) {
        ConfirmDialog(
            title = "¿Finalizar labor actual?",
            message = "Se registrará el cierre de la orden ${homeUiState.ordenActiva.codigo} en la máquina ${homeUiState.maquinaActiva.nombre}. ¿Desea continuar?",
            confirmText = "Sí, Finalizar",
            dismissText = "Continuar labor",
            icon = Icons.Default.CheckCircle,
            confirmColor = BrixVioletPrimary,
            onConfirm = {
                showFinishConfirmDialog = false
                onFinishLaborClick()
            },
            onDismiss = { showFinishConfirmDialog = false }
        )
    }
}
