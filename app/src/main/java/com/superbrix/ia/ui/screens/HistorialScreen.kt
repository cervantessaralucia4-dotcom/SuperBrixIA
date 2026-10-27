package com.superbrix.ia.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbrix.ia.data.model.Evento
import com.superbrix.ia.data.model.IACategorias
import com.superbrix.ia.ui.components.EmptyStateView
import com.superbrix.ia.ui.components.EventCard
import com.superbrix.ia.ui.theme.*

/**
 * Pantalla de Historial y Métricas de Planta para BRIX360.
 *
 * Características:
 * - Tarjetas de resumen OEE (Órdenes, Tiempo de Producción, Tiempo Muerto).
 * - Gráfico comparativo de distribución por categoría.
 * - Filtros por categoría y estado.
 * - Exportación y compartición de reporte de turno en formato CSV estándar.
 * - Línea de tiempo visual sin emojis.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    eventosList: List<Evento>,
    onEventClick: (Evento) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var selectedCategoryFilter by remember { mutableStateOf("TODAS") }

    // Métricas OEE
    val numeroOrdenes = remember(eventosList) {
        eventosList.map { it.ordenProduccion }.distinct().count { it.isNotBlank() && it != "No aplica" }
    }

    val tpTotalSecs = remember(eventosList) {
        eventosList.filter { it.categoria == IACategorias.PRODUCCION_ACTIVA }
            .sumOf { (it.duracionMinutos * 60) + it.duracionSegundos }
    }
    val tpMin = tpTotalSecs / 60
    val tpSec = tpTotalSecs % 60

    val tmTotalSecs = remember(eventosList) {
        eventosList.filter {
            it.categoria == IACategorias.FALLA_TECNICA ||
            it.categoria == IACategorias.LOGISTICA_MATERIALES ||
            it.categoria == IACategorias.SALUD_SEGURIDAD
        }.sumOf { (it.duracionMinutos * 60) + it.duracionSegundos }
    }
    val tmMin = tmTotalSecs / 60
    val tmSec = tmTotalSecs % 60

    // Filtro activo
    val filteredList = remember(eventosList, selectedCategoryFilter) {
        if (selectedCategoryFilter == "TODAS") eventosList
        else eventosList.filter { it.categoria.equals(selectedCategoryFilter, ignoreCase = true) }
    }

    fun exportarTurnoACSV() {
        if (eventosList.isEmpty()) {
            Toast.makeText(context, "No hay eventos para exportar en este turno", Toast.LENGTH_SHORT).show()
            return
        }

        val csvBuilder = StringBuilder()
        csvBuilder.append("ID,Fecha,HoraInicio,HoraFin,Operario,OrdenProduccion,Maquina,TipoEvento,Categoria,DuracionMin,DuracionSeg,ConfianzaIA,CategoriaCorregida,Anulado,Descripcion\n")

        eventosList.forEach { ev ->
            val cleanDesc = ev.descripcion.replace("\"", "\"\"").replace("\n", " ")
            csvBuilder.append("\"${ev.id}\",\"${ev.fecha}\",\"${ev.horaInicio}\",\"${ev.horaFin ?: ""}\",\"${ev.operario}\",\"${ev.ordenProduccion}\",\"${ev.maquina}\",\"${ev.tipoEvento}\",\"${ev.categoria}\",${ev.duracionMinutos},${ev.duracionSegundos},\"${ev.confianzaIA}\",${ev.categoriaCorregida},${ev.anulado},\"$cleanDesc\"\n")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, "Reporte_Turno_BRIX360_${eventosList.firstOrNull()?.fecha ?: "Hoy"}.csv")
            putExtra(Intent.EXTRA_TEXT, csvBuilder.toString())
            type = "text/csv"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Exportar y compartir reporte de turno")
        context.startActivity(shareIntent)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Historial y Estadísticas",
                        style = Typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = BrixTextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = BrixTextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { exportarTurnoACSV() }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Exportar CSV",
                            tint = BrixCyanAccent
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrixDarkBackground)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BrixDarkBackground)
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            // Dashboard de Estadísticas OEE
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = CardShape,
                colors = CardDefaults.cardColors(containerColor = BrixDarkSurface),
                border = BorderStroke(1.5.dp, BrixDarkBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Órdenes
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "ÓRDENES", style = Typography.labelSmall, color = BrixTextSecondary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "$numeroOrdenes", style = Typography.headlineMedium, fontWeight = FontWeight.Black, color = BrixCyanAccent)
                    }

                    Box(modifier = Modifier.width(1.dp).height(44.dp).background(BrixDarkBorder))

                    // Producción
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "PRODUCCIÓN", style = Typography.labelSmall, color = BrixTextSecondary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "${tpMin}m ${tpSec}s", style = Typography.titleMedium, fontWeight = FontWeight.Black, color = BrixStateProducing)
                    }

                    Box(modifier = Modifier.width(1.dp).height(44.dp).background(BrixDarkBorder))

                    // Tiempo Muerto
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "T. MUERTO", style = Typography.labelSmall, color = BrixTextSecondary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "${tmMin}m ${tmSec}s", style = Typography.titleMedium, fontWeight = FontWeight.Black, color = BrixStateCritical)
                    }
                }
            }

            // Barra de Filtros Horizontales
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filterCategories = listOf("TODAS") + IACategorias.TODAS
                filterCategories.forEach { cat ->
                    val isSelected = selectedCategoryFilter == cat
                    Surface(
                        modifier = Modifier.clickable { selectedCategoryFilter = cat },
                        shape = ChipShape,
                        color = if (isSelected) BrixVioletPrimary else BrixDarkSurface,
                        border = BorderStroke(1.dp, if (isSelected) BrixCyanAccent else BrixDarkBorder)
                    ) {
                        Text(
                            text = if (cat == "TODAS") "Todas las categorías" else cat,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else BrixTextSecondary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            // Título de Sección
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LÍNEA DE TIEMPO OPERATIVA (${filteredList.size})",
                    style = Typography.labelSmall,
                    color = BrixTextSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Exportar CSV ↑",
                    style = Typography.labelSmall,
                    color = BrixCyanAccent,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { exportarTurnoACSV() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Lista o Estado Vacío
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateView(
                        title = "Sin eventos para este filtro",
                        message = "No se encontraron interrupciones ni labores registradas con la categoría seleccionada.",
                        icon = Icons.Default.Assessment
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    itemsIndexed(filteredList, key = { _, ev -> ev.id }) { index, evento ->
                        val dotColor = when (evento.categoria) {
                            IACategorias.PRODUCCION_ACTIVA -> BrixStateProducing
                            IACategorias.SETUP -> Color(0xFFA855F7)
                            IACategorias.LOGISTICA_MATERIALES -> BrixOrangeAction
                            IACategorias.FALLA_TECNICA -> BrixStateCritical
                            IACategorias.CALIDAD -> Color(0xFF14B8A6)
                            IACategorias.COORDINACION -> Color(0xFF6366F1)
                            IACategorias.SALUD_SEGURIDAD -> Color(0xFFEF4444)
                            else -> Color(0xFF94A3B8)
                        }

                        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                            // Timeline con conector vertical
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(28.dp)
                                    .fillMaxHeight()
                            ) {
                                Spacer(modifier = Modifier.height(24.dp))
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(dotColor)
                                )
                                if (index != filteredList.lastIndex) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .weight(1f)
                                            .background(BrixDarkBorder)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Box(modifier = Modifier.weight(1f).padding(bottom = 6.dp)) {
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
