package com.superbrix.ia.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbrix.ia.ui.theme.IndustrialGrayBackground
import com.superbrix.ia.ui.theme.IndustrialRedAlert
import com.superbrix.ia.ui.theme.SuperBrixNavyPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReporteNovedadScreen(
    ordenCodigo: String = "OP-001",
    maquinaNombre: String = "Fresadora 01",
    onSubmitReport: (descripcion: String) -> Unit,
    onBackClick: () -> Unit
) {
    var descripcionText by remember { mutableStateOf("") }
    var isRecordingVoice by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Reportar novedad",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = IndustrialRedAlert)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(IndustrialGrayBackground)
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Info Box: OP y Máquina
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Orden de Producción",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = ordenCodigo,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuperBrixNavyPrimary
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Máquina Asignada",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = maquinaNombre,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuperBrixNavyPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Prompt
            Text(
                text = "¿Qué ocurrió?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SuperBrixNavyPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Large TextField
            OutlinedTextField(
                value = descripcionText,
                onValueChange = { descripcionText = it },
                placeholder = { Text("Describe brevemente la novedad...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Voice Action Button
            OutlinedButton(
                onClick = {
                    isRecordingVoice = !isRecordingVoice
                    if (isRecordingVoice && descripcionText.isBlank()) {
                        descripcionText = "Paré la fresadora porque estoy esperando la broca de 1/2 pulgada."
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = null,
                        tint = if (isRecordingVoice) IndustrialRedAlert else SuperBrixNavyPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isRecordingVoice) "Escuchando... 🎙" else "🎙 Mantener para hablar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isRecordingVoice) IndustrialRedAlert else SuperBrixNavyPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Example text small box
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Ejemplo:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Text(
                        text = "\"Paré la fresadora porque estoy esperando la broca.\"",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF334155)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Big Submit Button: ENVIAR NOVEDAD
            Button(
                onClick = {
                    val finalDesc = if (descripcionText.isBlank()) "Paré la fresadora porque estoy esperando la broca." else descripcionText
                    onSubmitReport(finalDesc)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = IndustrialRedAlert,
                    contentColor = Color.White
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ENVIAR NOVEDAD",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
