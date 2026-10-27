package com.superbrix.ia.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbrix.ia.ui.theme.*

/**
 * Tarjeta de Operación Industrial para BRIX360.
 * Muestra los 4 datos operativos de piso: Orden de Producción (OP),
 * Área de Trabajo, Operación y Máquina Asignada.
 */
@Composable
fun OperationCard(
    ordenCodigo: String,
    maquinaNombre: String,
    areaNombre: String = "CNC",
    operacionNombre: String = "Mecanizado",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = BrixDarkSurface),
        border = BorderStroke(1.5.dp, BrixDarkBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ESTACIÓN Y LABOR ACTIVA",
                    style = Typography.labelSmall,
                    color = BrixTextSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )

                // Badge de Área
                Surface(
                    shape = ChipShape,
                    color = BrixVioletPrimary.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, BrixVioletPrimary.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = BrixCyanAccent,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = areaNombre,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrixCyanAccent
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Grid de 2x2 para OP, Máquina, Área y Operación
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Columna 1: OP
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(BrixDarkSurfaceVariant, shape = InputShape)
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = null,
                            tint = BrixCyanAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ORDEN",
                            style = Typography.labelSmall,
                            color = BrixTextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = ordenCodigo.ifBlank { "Sin asignar" },
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BrixTextPrimary
                    )
                }

                // Columna 2: Máquina
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(BrixDarkSurfaceVariant, shape = InputShape)
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PrecisionManufacturing,
                            contentDescription = null,
                            tint = BrixOrangeAction,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MÁQUINA",
                            style = Typography.labelSmall,
                            color = BrixTextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = maquinaNombre.ifBlank { "No aplica" },
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BrixTextPrimary,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Operación específica
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrixDarkSurfaceVariant, shape = InputShape)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Operación en proceso:",
                    style = Typography.bodySmall,
                    color = BrixTextSecondary
                )
                Text(
                    text = operacionNombre,
                    style = Typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = BrixCyanAccent
                )
            }
        }
    }
}
