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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IndustrialGrayBackground)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Formato FO-A-MA-01 Reference Badge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE0F2FE), shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "📄 Formato Oficial SuperBrix FO-A-MA-01: Control de Tiempos",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0288D1)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Header
        Header(
            userName = homeUiState.operario.nombre,
            title = "Registro de operación",
            onProfileClick = onProfileClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Operation Card
        OperationCard(
            ordenCodigo = homeUiState.ordenActiva.codigo,
            maquinaNombre = homeUiState.maquinaActiva.nombre,
            estadoText = homeUiState.laborEstadoText,
            isLaborActive = homeUiState.isLaborActive
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "ACCIONES PRINCIPALES",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 1-Tap Quick Start (Highest Automation for Operator)
        if (!homeUiState.isLaborActive) {
            PrimaryButton(
                text = "⚡ INICIO RÁPIDO (${homeUiState.ordenActiva.codigo})",
                onClick = onQuickStartClick,
                icon = Icons.Default.FlashOn,
                containerColor = IndustrialGreenActive
            )

            Spacer(modifier = Modifier.height(12.dp))

            SecondaryButton(
                text = "▶ Cambiar Orden / Máquina",
                onClick = onStartLaborClick,
                icon = Icons.Default.PlayArrow
            )
        } else {
            // Active Labor Actions: Finish, Interrupt, Resume
            PrimaryButton(
                text = "🏁 FINALIZAR LABOR",
                onClick = onFinishLaborClick,
                icon = Icons.Default.CheckCircle,
                containerColor = SuperBrixNavyPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            PrimaryButton(
                text = "⏸ REPORTAR INTERRUPCIÓN",
                onClick = onReportInterruptionClick,
                icon = Icons.Default.PauseCircle,
                containerColor = IndustrialRedAlert
            )

            Spacer(modifier = Modifier.height(12.dp))

            PrimaryButton(
                text = "▶▶ REANUDAR LABOR",
                onClick = onResumeLaborClick,
                icon = Icons.Default.PlayArrow,
                containerColor = Color(0xFF0288D1)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Secondary Actions Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SecondaryButton(
                text = "📋 Historial",
                onClick = onHistoryClick,
                icon = Icons.Default.History,
                modifier = Modifier.weight(1f)
            )

            SecondaryButton(
                text = "👤 Perfil",
                onClick = onProfileClick,
                icon = Icons.Default.Person,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Status Card
        StatusCard(
            statusMessage = if (!homeUiState.isLaborActive && homeUiState.laborEstadoText.contains("Sin labor", true)) {
                "Listo para trabajar"
            } else {
                homeUiState.laborEstadoText
            }
        )
    }
}
