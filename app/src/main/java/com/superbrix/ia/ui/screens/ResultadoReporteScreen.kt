package com.superbrix.ia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbrix.ia.data.model.Evento
import com.superbrix.ia.data.model.IACategorias
import com.superbrix.ia.ui.components.EmptyStateView
import com.superbrix.ia.ui.components.PrimaryButton
import com.superbrix.ia.ui.components.SecondaryButton
import com.superbrix.ia.ui.theme.*

/**
 * Pantalla de Resultado de Clasificación IA para BRIX360.
 *
 * Características:
 * - Alerta visual inmediata para eventos de Salud y Seguridad.
 * - Chip editable para corregir la categoría antes de finalizar.
 * - Barra visual de porcentaje de confianza del modelo.
 * - Registro auditado de corrección de categoría.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultadoReporteScreen(
    evento: Evento?,
    onCategoryCorrected: (String) -> Unit = {},
    onBackHomeClick: () -> Unit,
    onViewHistoryClick: () -> Unit
) {
    if (evento == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(BrixDarkBackground),
            contentAlignment = Alignment.Center
        ) {
            EmptyStateView(
                title = "No hay evento registrado",
                message = "Regrese al inicio para reportar una nueva novedad."
            )
        }
        return
    }

    var showCategoryCorrectionDialog by remember { mutableStateOf(false) }

    val categoryColor = when (evento.categoria) {
        IACategorias.SALUD_SEGURIDAD -> Color(0xFFEF4444)
        IACategorias.FALLA_TECNICA -> BrixStateCritical
        IACategorias.LOGISTICA_MATERIALES -> BrixOrangeAction
        IACategorias.PRODUCCION_ACTIVA -> BrixStateProducing
        IACategorias.SETUP -> Color(0xFFA855F7)
        IACategorias.CALIDAD -> Color(0xFF14B8A6)
        IACategorias.COORDINACION -> Color(0xFF6366F1)
        else -> Color(0xFF94A3B8)
    }

    // Extracción numérica de confianza
    val confidenceFloat = remember(evento.confianzaIA) {
        val digits = evento.confianzaIA.filter { it.isDigit() }
        (digits.toFloatOrNull() ?: 90f) / 100f
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Resultado de Clasificación",
                        style = Typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = BrixTextPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrixDarkBackground)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BrixDarkBackground)
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ALERTA VISIBLE INMEDIATA: Salud y Seguridad
                if (evento.categoria == IACategorias.SALUD_SEGURIDAD || evento.esNovedadSeguridad) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = CardShape,
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF7F1D1D)),
                        border = BorderStroke(2.dp, Color(0xFFEF4444))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = "Alerta de Salud",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "ALERTA DE SEGURIDAD INDUSTRIAL",
                                    style = Typography.titleSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Se ha notificado al personal médico de planta y supervisor SST.",
                                    style = Typography.bodySmall,
                                    color = Color(0xFFFCA5A5)
                                )
                            }
                        }
                    }
                }

                // Banner de Confirmación
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    color = BrixStateProducingBg,
                    border = BorderStroke(1.dp, BrixStateProducing.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = BrixStateProducing,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Interrupción registrada y guardada exitosamente",
                            style = Typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = BrixTextPrimary
                        )
                    }
                }

                // Tarjeta de Categoría IA con botón para corregir
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    colors = CardDefaults.cardColors(containerColor = BrixDarkSurface),
                    border = BorderStroke(1.5.dp, categoryColor.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CATEGORÍA INFERIDA POR IA",
                                style = Typography.labelSmall,
                                color = BrixTextSecondary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )

                            // Chip botón para Corregir Categoría
                            Surface(
                                modifier = Modifier.clickable { showCategoryCorrectionDialog = true },
                                shape = ChipShape,
                                color = BrixDarkSurfaceVariant,
                                border = BorderStroke(1.dp, BrixCyanAccent)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Corregir",
                                        tint = BrixCyanAccent,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Corregir",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrixCyanAccent
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = evento.categoria,
                            style = Typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = categoryColor
                        )

                        if (evento.categoriaCorregida) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Categoría corregida manualmente por el operario",
                                style = Typography.labelSmall,
                                color = BrixOrangeAction
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Barra de Porcentaje de Confianza
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Confianza del análisis:",
                                style = Typography.bodySmall,
                                color = BrixTextSecondary
                            )
                            Text(
                                text = evento.confianzaIA,
                                style = Typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = BrixCyanAccent
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { confidenceFloat },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = BrixCyanAccent,
                            trackColor = BrixDarkSurfaceVariant
                        )
                    }
                }

                // Descripción del Operario
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    colors = CardDefaults.cardColors(containerColor = BrixDarkSurface),
                    border = BorderStroke(1.dp, BrixDarkBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "DESCRIPCIÓN REPORTADA",
                            style = Typography.labelSmall,
                            color = BrixTextSecondary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "\"${evento.descripcion}\"",
                            style = Typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = BrixTextPrimary
                        )
                    }
                }

                // Grid de Detalles Operativos
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    colors = CardDefaults.cardColors(containerColor = BrixDarkSurface),
                    border = BorderStroke(1.dp, BrixDarkBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "Operario:", style = Typography.labelSmall, color = BrixTextSecondary)
                                Text(text = evento.operario, style = Typography.bodyLarge, fontWeight = FontWeight.Bold, color = BrixTextPrimary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "Hora de Parada:", style = Typography.labelSmall, color = BrixTextSecondary)
                                Text(text = evento.horaInicio, style = Typography.bodyLarge, fontWeight = FontWeight.Bold, color = BrixTextPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "Orden de Producción:", style = Typography.labelSmall, color = BrixTextSecondary)
                                Text(text = evento.ordenProduccion, style = Typography.bodyLarge, fontWeight = FontWeight.Bold, color = BrixCyanAccent)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "Máquina:", style = Typography.labelSmall, color = BrixTextSecondary)
                                Text(text = evento.maquina, style = Typography.bodyLarge, fontWeight = FontWeight.Bold, color = BrixTextPrimary)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Botones de Navegación
                PrimaryButton(
                    text = "VOLVER AL INICIO",
                    onClick = onBackHomeClick,
                    icon = Icons.Default.Home,
                    modifier = Modifier.height(60.dp)
                )

                SecondaryButton(
                    text = "Ver Historial del Turno",
                    onClick = onViewHistoryClick,
                    icon = Icons.Default.History
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Diálogo para Corregir Categoría
    if (showCategoryCorrectionDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryCorrectionDialog = false },
            shape = CardShape,
            containerColor = BrixDarkSurface,
            title = {
                Text(
                    text = "Corregir Categoría",
                    style = Typography.titleLarge,
                    color = BrixTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Seleccione la categoría correcta si la inferencia de la IA no coincide:",
                        style = Typography.bodySmall,
                        color = BrixTextSecondary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    IACategorias.TODAS.forEach { cat ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onCategoryCorrected(cat)
                                    showCategoryCorrectionDialog = false
                                },
                            shape = InputShape,
                            color = if (cat == evento.categoria) BrixVioletPrimary.copy(alpha = 0.25f) else BrixDarkSurfaceVariant,
                            border = BorderStroke(
                                1.dp,
                                if (cat == evento.categoria) BrixCyanAccent else BrixDarkBorder
                            )
                        ) {
                            Text(
                                text = cat,
                                style = Typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = if (cat == evento.categoria) BrixCyanAccent else BrixTextPrimary,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoryCorrectionDialog = false }) {
                    Text("Cerrar", color = BrixTextSecondary)
                }
            }
        )
    }
}
