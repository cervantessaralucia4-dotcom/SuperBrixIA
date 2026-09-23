package com.superbrix.ia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbrix.ia.ui.components.Header
import com.superbrix.ia.ui.components.OperationCard
import com.superbrix.ia.ui.components.PrimaryButton
import com.superbrix.ia.ui.components.SecondaryButton
import com.superbrix.ia.ui.components.StatusCard
import com.superbrix.ia.ui.theme.IndustrialGrayBackground
import com.superbrix.ia.ui.theme.IndustrialGreenActive
import com.superbrix.ia.ui.theme.IndustrialRedAlert
import com.superbrix.ia.ui.theme.SuperBrixNavyPrimary
import com.superbrix.ia.ui.theme.SuperBrixBlueAccent
import com.superbrix.ia.viewmodel.HomeUiState

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

    var elapsedTimeFormatted by remember { mutableStateOf("00:00") }

    LaunchedEffect(homeUiState.interruptionStartTime) {
        if (homeUiState.interruptionStartTime != null) {
            while (true) {
                val diffSecs = (System.currentTimeMillis() - homeUiState.interruptionStartTime) / 1000
                val mins = diffSecs / 60
                val secs = diffSecs % 60
                elapsedTimeFormatted = String.format("%02d:%02d", mins, secs)
                delay(1000L)
            }
        } else {
            elapsedTimeFormatted = "00:00"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IndustrialGrayBackground)
            .verticalScroll(scrollState)
    ) {
        // Header (Now full width)
        Header(
            userName = homeUiState.operario.nombre,
            title = "Registro de operación",
            onProfileClick = onProfileClick
        )
        
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
            // Formato FO-A-MA-01 Reference Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE0F2FE), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "📄 Formato Oficial SuperBrix FO-A-MA-01: Control de Tiempos",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0288D1)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Operation Card
            OperationCard(
                ordenCodigo = homeUiState.ordenActiva.codigo,
                maquinaNombre = homeUiState.maquinaActiva.nombre,
                estadoText = homeUiState.laborEstadoText,
                isLaborActive = homeUiState.isLaborActive
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "ACCIONES PRINCIPALES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 1-Tap Quick Start (Highest Automation for Operator)
            if (homeUiState.isLaborActive) {
                // Active Labor Actions: Finish, Interrupt
                PrimaryButton(
                    text = "FINALIZAR LABOR",
                    onClick = onFinishLaborClick,
                    icon = Icons.Default.CheckCircle,
                    containerColor = SuperBrixNavyPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                PrimaryButton(
                    text = "REPORTAR INTERRUPCIÓN",
                    onClick = onReportInterruptionClick,
                    icon = Icons.Default.PauseCircle,
                    containerColor = IndustrialRedAlert
                )
            } else if (homeUiState.laborEstadoText == "Interrupción registrada") {
                // Paused Labor Actions: Resume, Finish
                PrimaryButton(
                    text = "REANUDAR LABOR",
                    onClick = onResumeLaborClick,
                    icon = Icons.Default.PlayArrow,
                    containerColor = Color(0xFF0288D1)
                )

                Spacer(modifier = Modifier.height(16.dp))
                
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Tiempo en pausa: $elapsedTimeFormatted",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = IndustrialRedAlert
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                PrimaryButton(
                    text = "FINALIZAR LABOR",
                    onClick = onFinishLaborClick,
                    icon = Icons.Default.CheckCircle,
                    containerColor = SuperBrixNavyPrimary
                )
            } else {
                // Idle State: Start New
                PrimaryButton(
                    text = "INICIO RÁPIDO (${homeUiState.ordenActiva.codigo})",
                    onClick = onQuickStartClick,
                    icon = Icons.Default.FlashOn,
                    containerColor = IndustrialGreenActive
                )

                Spacer(modifier = Modifier.height(16.dp))

                SecondaryButton(
                    text = "Cambiar Orden / Máquina",
                    onClick = onStartLaborClick,
                    icon = Icons.Default.PlayArrow
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Secondary Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SecondaryButton(
                    text = "Historial",
                    onClick = onHistoryClick,
                    icon = Icons.Default.History,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Status Card
            StatusCard(
                statusMessage = if (!homeUiState.isLaborActive && homeUiState.laborEstadoText.contains("Sin labor", true)) {
                    "Listo para trabajar"
                } else {
                    homeUiState.laborEstadoText
                }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
        }
    }
}
