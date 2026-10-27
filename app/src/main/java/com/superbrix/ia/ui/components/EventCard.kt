package com.superbrix.ia.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbrix.ia.data.model.Evento
import com.superbrix.ia.data.model.IACategorias
import com.superbrix.ia.data.model.SyncStatus
import com.superbrix.ia.ui.theme.*

/**
 * Tarjeta de Evento para Línea de Tiempo e Historial en BRIX360.
 * Cero emojis: utiliza iconos vectoriales Material para indicar tipo de evento
 * y sincronización.
 */
@Composable
fun EventCard(
    evento: Evento,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = when (evento.categoria) {
        IACategorias.PRODUCCION_ACTIVA -> BrixStateProducing
        IACategorias.SETUP -> Color(0xFFA855F7)
        IACategorias.LOGISTICA_MATERIALES -> BrixOrangeAction
        IACategorias.FALLA_TECNICA -> BrixStateCritical
        IACategorias.CALIDAD -> Color(0xFF14B8A6)
        IACategorias.COORDINACION -> Color(0xFF6366F1)
        IACategorias.SALUD_SEGURIDAD -> Color(0xFFEF4444)
        IACategorias.OTROS -> Color(0xFF94A3B8)
        else -> BrixCyanAccent
    }

    val isInterruption = evento.tipoEvento.contains("Interrup", ignoreCase = true)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = BrixDarkSurface),
        border = BorderStroke(
            1.2.dp,
            if (evento.esNovedadSeguridad) BrixStateCritical else BrixDarkBorder
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Banda vertical indicadora de categoría
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(categoryColor)
            )

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Header de la tarjeta: OP / Máquina y Hora
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${evento.ordenProduccion} • ${evento.maquina}",
                            style = Typography.labelSmall,
                            color = BrixCyanAccent,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = evento.horaInicio,
                            style = Typography.labelSmall,
                            color = BrixTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Tipo de Evento con Icono Material
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isInterruption) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                            contentDescription = null,
                            tint = if (isInterruption) BrixOrangeAction else BrixStateProducing,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = evento.tipoEvento,
                            style = Typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = BrixTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Descripción
                    Text(
                        text = evento.descripcion,
                        style = Typography.bodyMedium,
                        color = BrixTextSecondary,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Fila inferior: Chip de Categoría IA y Duración
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Chip de Categoría
                        Surface(
                            shape = ChipShape,
                            color = categoryColor.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, categoryColor.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = evento.categoria,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = categoryColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        // Duración si aplica
                        if (evento.duracionMinutos > 0 || evento.duracionSegundos > 0) {
                            Surface(
                                shape = ChipShape,
                                color = BrixDarkSurfaceVariant
                            ) {
                                Text(
                                    text = "${evento.duracionMinutos}m ${evento.duracionSegundos}s",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrixTextSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Estado de Sincronización
                        val syncIcon = if (evento.syncStatus == SyncStatus.ENVIADO) Icons.Default.CloudDone else Icons.Default.CloudUpload
                        val syncTint = if (evento.syncStatus == SyncStatus.ENVIADO) BrixStateProducing else BrixTextSecondary
                        Icon(
                            imageVector = syncIcon,
                            contentDescription = "Estado de sincronización",
                            tint = syncTint,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Ver detalle",
                    tint = BrixTextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
