package com.superbrix.ia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbrix.ia.data.model.Evento
import com.superbrix.ia.data.model.IACategorias
import com.superbrix.ia.ui.theme.IndustrialGrayBackground
import com.superbrix.ia.ui.theme.SuperBrixNavyPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleEventoScreen(
    evento: Evento?,
    onBackClick: () -> Unit
) {
    val evt = evento ?: Evento(
        id = "EVT-1002",
        fecha = "2026-09-22",
        horaInicio = "10:32 AM",
        horaFin = "10:47 AM",
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
                        "Detalle del evento",
                        fontWeight = FontWeight.Bold,
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "ID: ${evt.id}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(text = "Tipo de Evento:", color = Color.Gray, fontSize = 12.sp)
                    Text(
                        text = evt.tipoEvento,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = SuperBrixNavyPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Fecha:", color = Color.Gray, fontSize = 12.sp)
                            Text(
                                text = evt.fecha,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Hora de inicio:", color = Color.Gray, fontSize = 12.sp)
                            Text(
                                text = evt.horaInicio,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Hora finalización:", color = Color.Gray, fontSize = 12.sp)
                            Text(
                                text = evt.horaFin ?: "10:47 AM",
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Duración:", color = Color.Gray, fontSize = 12.sp)
                            Text(
                                text = "${evt.duracionMinutos} minutos",
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Operario:", color = Color.Gray, fontSize = 12.sp)
                            Text(
                                text = evt.operario,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Orden de Producción:", color = Color.Gray, fontSize = 12.sp)
                            Text(
                                text = evt.ordenProduccion,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Máquina:", color = Color.Gray, fontSize = 12.sp)
                    Text(
                        text = evt.maquina,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Descripción:", color = Color.Gray, fontSize = 12.sp)
                    Text(
                        text = evt.descripcion,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Categoría IA:", color = Color.Gray, fontSize = 12.sp)
                    Text(
                        text = evt.categoria,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF0288D1)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Confianza de clasificación:", color = Color.Gray, fontSize = 12.sp)
                    Text(
                        text = evt.confianzaIA,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }
            }
        }
    }
}
