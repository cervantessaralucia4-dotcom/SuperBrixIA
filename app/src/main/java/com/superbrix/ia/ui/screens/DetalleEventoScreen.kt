package com.superbrix.ia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.superbrix.ia.data.model.Evento
import com.superbrix.ia.data.model.IACategorias
import com.superbrix.ia.data.model.SyncStatus
import com.superbrix.ia.ui.components.EmptyStateView
import com.superbrix.ia.ui.components.SecondaryButton
import com.superbrix.ia.ui.theme.*

/**
 * Pantalla de Detalle de Evento Operativo para BRIX360.
 *
 * Características:
 * - Cabecera semántica con categoría y estado de sincronización.
 * - Grid ordenado de trazabilidad industrial (OP, Máquina, Operario, Duración, Tiempos).
 * - Visualización de evidencia fotográfica adjunta.
 * - Soporte para anular eventos con motivo obligatorio de auditoría.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleEventoScreen(
    evento: Evento?,
    onAnularEvento: ((eventoId: String, motivo: String) -> Unit)? = null,
    onBackClick: () -> Unit
) {
    if (evento == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(BrixDarkBackground),
            contentAlignment = Alignment.Center
        ) {
            EmptyStateView(
                title = "No hay evento seleccionado",
                message = "Seleccione un evento desde el historial para consultar su detalle."
            )
        }
        return
    }

    var showAnularDialog by remember { mutableStateOf(false) }
    var motivoAnulacion by remember { mutableStateOf("") }
    var motivoError by remember { mutableStateOf<String?>(null) }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detalle del Evento",
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
                // Alerta si está Anulado
                if (evento.anulado) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = CardShape,
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF3B1E1E)),
                        border = BorderStroke(1.5.dp, BrixStateCritical)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Block, contentDescription = null, tint = BrixStateCritical)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "EVENTO ANULADO",
                                    style = Typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = BrixStateCritical
                                )
                            }
                            if (!evento.motivoAnulacion.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Motivo: ${evento.motivoAnulacion}",
                                    style = Typography.bodySmall,
                                    color = BrixTextSecondary
                                )
                            }
                        }
                    }
                }

                // Tarjeta de Categoría e Identificador
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    colors = CardDefaults.cardColors(containerColor = BrixDarkSurface),
                    border = BorderStroke(1.5.dp, categoryColor.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ID: ${evento.id}",
                                style = Typography.labelSmall,
                                color = BrixCyanAccent,
                                fontWeight = FontWeight.Bold
                            )

                            // Badge de Sincronización
                            val isSync = evento.syncStatus == SyncStatus.ENVIADO
                            Surface(
                                shape = ChipShape,
                                color = if (isSync) BrixStateProducingBg else BrixDarkSurfaceVariant
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isSync) Icons.Default.CloudDone else Icons.Default.CloudUpload,
                                        contentDescription = null,
                                        tint = if (isSync) BrixStateProducing else BrixTextSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isSync) "Sincronizado" else "En cola local",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSync) BrixStateProducing else BrixTextSecondary
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

                        Text(
                            text = "Tipo: ${evento.tipoEvento}",
                            style = Typography.bodyMedium,
                            color = BrixTextSecondary
                        )

                        if (evento.categoriaCorregida && !evento.categoriaOriginalIA.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Corregido de: ${evento.categoriaOriginalIA}",
                                style = Typography.labelSmall,
                                color = BrixOrangeAction
                            )
                        }
                    }
                }

                // Descripción
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    colors = CardDefaults.cardColors(containerColor = BrixDarkSurface),
                    border = BorderStroke(1.dp, BrixDarkBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "DESCRIPCIÓN OPERATIVA",
                            style = Typography.labelSmall,
                            color = BrixTextSecondary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "\"${evento.descripcion}\"",
                            style = Typography.bodyLarge,
                            color = BrixTextPrimary
                        )
                    }
                }

                // Grid Ordenado de Trazabilidad Industrial
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    colors = CardDefaults.cardColors(containerColor = BrixDarkSurface),
                    border = BorderStroke(1.dp, BrixDarkBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "TRAZABILIDAD DE PLANTA (FO-A-MA-01)",
                            style = Typography.labelSmall,
                            color = BrixCyanAccent,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Row(modifier = Modifier.fillMaxWidth()) {
                            DetailItem(title = "Fecha", value = evento.fecha, modifier = Modifier.weight(1f))
                            DetailItem(title = "Operario", value = evento.operario, modifier = Modifier.weight(1f))
                        }

                        Row(modifier = Modifier.fillMaxWidth()) {
                            DetailItem(title = "Orden de Producción", value = evento.ordenProduccion, modifier = Modifier.weight(1f))
                            DetailItem(title = "Máquina Asignada", value = evento.maquina, modifier = Modifier.weight(1f))
                        }

                        Row(modifier = Modifier.fillMaxWidth()) {
                            DetailItem(title = "Hora de Inicio", value = evento.horaInicio, modifier = Modifier.weight(1f))
                            DetailItem(title = "Hora de Fin", value = evento.horaFin ?: "En curso", modifier = Modifier.weight(1f))
                        }

                        Row(modifier = Modifier.fillMaxWidth()) {
                            DetailItem(
                                title = "Duración Total",
                                value = "${evento.duracionMinutos}m ${evento.duracionSegundos}s",
                                valueColor = BrixCyanAccent,
                                modifier = Modifier.weight(1f)
                            )
                            DetailItem(
                                title = "Confianza IA",
                                value = evento.confianzaIA,
                                valueColor = BrixStateProducing,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Evidencia Fotográfica si existe
                if (!evento.fotoUri.isNullOrBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = CardShape,
                        colors = CardDefaults.cardColors(containerColor = BrixDarkSurface),
                        border = BorderStroke(1.dp, BrixDarkBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "EVIDENCIA FOTOGRÁFICA",
                                style = Typography.labelSmall,
                                color = BrixTextSecondary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(CardShape)
                                    .background(BrixDarkSurfaceVariant)
                            ) {
                                AsyncImage(
                                    model = evento.fotoUri,
                                    contentDescription = "Evidencia de la avería",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Acción de Anular Evento
                if (!evento.anulado && onAnularEvento != null) {
                    SecondaryButton(
                        text = "Anular este evento con motivo",
                        onClick = { showAnularDialog = true },
                        icon = Icons.Default.DeleteForever
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Diálogo para Anular Evento con Motivo
    if (showAnularDialog) {
        AlertDialog(
            onDismissRequest = {
                showAnularDialog = false
                motivoAnulacion = ""
                motivoError = null
            },
            shape = CardShape,
            containerColor = BrixDarkSurface,
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = BrixStateCritical,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "¿Anular registro de evento?",
                    style = Typography.titleLarge,
                    color = BrixTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "El evento se marcará como anulado en auditoría. Ingrese el motivo de la anulación (mínimo 5 caracteres):",
                        style = Typography.bodyMedium,
                        color = BrixTextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = motivoAnulacion,
                        onValueChange = {
                            motivoAnulacion = it
                            motivoError = null
                        },
                        placeholder = { Text("Ej. Reporte duplicado por error táctil...", color = BrixTextDisabled) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = InputShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = BrixTextPrimary,
                            unfocusedTextColor = BrixTextPrimary,
                            focusedBorderColor = BrixStateCritical,
                            unfocusedBorderColor = BrixDarkBorder,
                            focusedContainerColor = BrixDarkSurfaceVariant,
                            unfocusedContainerColor = BrixDarkSurfaceVariant
                        )
                    )

                    if (motivoError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = motivoError ?: "",
                            style = Typography.bodySmall,
                            color = BrixStateCritical
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (motivoAnulacion.trim().length < 5) {
                            motivoError = "Por favor ingrese un motivo válido (mínimo 5 caracteres)"
                        } else {
                            onAnularEvento?.invoke(evento.id, motivoAnulacion.trim())
                            showAnularDialog = false
                        }
                    },
                    shape = ButtonShape,
                    colors = ButtonDefaults.buttonColors(containerColor = BrixStateCritical)
                ) {
                    Text("Confirmar Anulación", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAnularDialog = false
                    motivoAnulacion = ""
                    motivoError = null
                }) {
                    Text("Cancelar", color = BrixTextSecondary)
                }
            }
        )
    }
}

@Composable
private fun DetailItem(
    title: String,
    value: String,
    valueColor: Color = BrixTextPrimary,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = title, style = Typography.labelSmall, color = BrixTextSecondary)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = Typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}
