package com.superbrix.ia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbrix.ia.data.model.Evento
import com.superbrix.ia.data.model.IACategorias
import com.superbrix.ia.ui.components.EventCard
import com.superbrix.ia.ui.theme.IndustrialGrayBackground
import com.superbrix.ia.ui.theme.SuperBrixNavyPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    eventosList: List<Evento>,
    onEventClick: (Evento) -> Unit,
    onBackClick: () -> Unit
) {
    val numeroOrdenes = eventosList.map { it.ordenProduccion }.distinct().count { it.isNotBlank() && it != "No aplica" }
    
    val tiempoProduccionMin = eventosList.filter { it.categoria == IACategorias.PRODUCCION_ACTIVA }.sumOf { it.duracionMinutos }
    val tiempoProduccionSeg = eventosList.filter { it.categoria == IACategorias.PRODUCCION_ACTIVA }.sumOf { it.duracionSegundos }
    val tpTotalMin = tiempoProduccionMin + (tiempoProduccionSeg / 60)
    val tpTotalSeg = tiempoProduccionSeg % 60

    val tiempoMuertoMin = eventosList.filter { it.categoria == IACategorias.FALLA_TECNICA || it.categoria == IACategorias.LOGISTICA_MATERIALES }.sumOf { it.duracionMinutos }
    val tiempoMuertoSeg = eventosList.filter { it.categoria == IACategorias.FALLA_TECNICA || it.categoria == IACategorias.LOGISTICA_MATERIALES }.sumOf { it.duracionSegundos }
    val tmTotalMin = tiempoMuertoMin + (tiempoMuertoSeg / 60)
    val tmTotalSeg = tiempoMuertoSeg % 60
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Historial y Estadísticas",
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
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            
            // Dashboard Estadísticas
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                ) {
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Órdenes", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text(text = "$numeroOrdenes", fontSize = 24.sp, fontWeight = FontWeight.Black, color = SuperBrixNavyPrimary)
                    }
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Producción", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text(text = "${tpTotalMin}m ${tpTotalSeg}s", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF2E7D32))
                    }
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "T. Muerto", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text(text = "${tmTotalMin}m ${tmTotalSeg}s", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFFC62828))
                    }
                }
            }
            
            Text(
                text = "LÍNEA DE TIEMPO DE EVENTOS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (eventosList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay eventos registrados en este turno.",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn {
                    itemsIndexed(eventosList) { index, evento ->
                        
                        val dotColor = when (evento.categoria) {
                            IACategorias.PRODUCCION_ACTIVA -> Color(0xFF2E7D32)
                            IACategorias.SETUP -> Color(0xFF7B1FA2)
                            IACategorias.LOGISTICA_MATERIALES -> Color(0xFFE65100)
                            IACategorias.FALLA_TECNICA -> Color(0xFFC62828)
                            IACategorias.CALIDAD -> Color(0xFF00695C)
                            IACategorias.COORDINACION -> Color(0xFF283593)
                            IACategorias.SALUD_SEGURIDAD -> Color(0xFFD32F2F)
                            IACategorias.OTROS -> Color(0xFF607D8B)
                            else -> Color(0xFF475569)
                        }
                        
                        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                            // Timeline visual
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(32.dp).fillMaxHeight()
                            ) {
                                Spacer(modifier = Modifier.height(24.dp))
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(dotColor)
                                )
                                if (index != eventosList.lastIndex) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .weight(1f)
                                            .background(Color.LightGray)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            // Content
                            Box(modifier = Modifier.weight(1f).padding(bottom = 8.dp)) {
                                EventCard(
                                    evento = evento,
                                    onClick = { onEventClick(evento) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
