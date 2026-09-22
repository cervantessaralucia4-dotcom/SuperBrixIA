package com.superbrix.ia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbrix.ia.data.model.Evento
import com.superbrix.ia.data.model.IACategorias
import com.superbrix.ia.ui.theme.IndustrialGrayBackground
import com.superbrix.ia.ui.theme.IndustrialRedAlert
import com.superbrix.ia.ui.theme.SuperBrixNavyPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultadoReporteScreen(
    evento: Evento?,
    onBackHomeClick: () -> Unit,
    onViewHistoryClick: () -> Unit
) {
    val evt = evento ?: Evento(
        id = "EVT-1002",
        fecha = "2026-09-22",
        horaInicio = "10:32 AM",
        operario = "Sara",
        ordenProduccion = "OP-001",
        maquina = "Fresadora 01",
        tipoEvento = "Interrupción",
        descripcion = "Paré la fresadora porque estoy esperando la broca de 1/2 pulgada.",
        categoria = IACategorias.LOGISTICA_MATERIALES,
        duracionMinutos = 15,
        confianzaIA = "95%"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Evento registrado",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SuperBrixNavyPrimary)
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
            // Success Badge Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.height(32.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "✓ Registro completado",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Description Box
            Text(
                text = "DESCRIPCIÓN DE LA NOVEDAD",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(
                    text = "\"${evt.descripcion}\"",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI Categorization Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "CATEGORIZACIÓN IA",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Categoría IA:", fontSize = 13.sp, color = Color.Gray)
                    Text(
                        text = evt.categoria,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0288D1)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Operario:", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                text = evt.operario,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SuperBrixNavyPrimary
                            )
                        }

                        Column {
                            Text(text = "Orden:", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                text = evt.ordenProduccion,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SuperBrixNavyPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Máquina:", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                text = evt.maquina,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SuperBrixNavyPrimary
                            )
                        }

                        Column {
                            Text(text = "Hora:", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                text = evt.horaInicio,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SuperBrixNavyPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Estado:", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        text = "Interrupción registrada",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = IndustrialRedAlert
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Action Buttons
            Button(
                onClick = onBackHomeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SuperBrixNavyPrimary,
                    contentColor = Color.White
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Home, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "VOLVER AL INICIO",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onViewHistoryClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.History, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "VER HISTORIAL",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
