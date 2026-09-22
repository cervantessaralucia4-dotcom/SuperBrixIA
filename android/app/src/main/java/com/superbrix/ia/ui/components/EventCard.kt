package com.superbrix.ia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbrix.ia.data.model.Evento
import com.superbrix.ia.data.model.IACategorias

@Composable
fun EventCard(
    evento: Evento,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (badgeBgColor, badgeTextColor) = when (evento.categoria) {
        IACategorias.PRODUCCION_ACTIVA -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        IACategorias.SETUP -> Color(0xFFF3E5F5) to Color(0xFF7B1FA2)
        IACategorias.LOGISTICA_MATERIALES -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        IACategorias.FALLA_TECNICA -> Color(0xFFFFEBEE) to Color(0xFFC62828)
        IACategorias.CALIDAD -> Color(0xFFE0F2F1) to Color(0xFF00695C)
        IACategorias.COORDINACION -> Color(0xFFE8EAF6) to Color(0xFF283593)
        else -> Color(0xFFF1F5F9) to Color(0xFF475569)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable(onClick = onClick),
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${evento.ordenProduccion} • ${evento.maquina}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = evento.horaInicio,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${if (evento.tipoEvento.contains("Interrup", true)) "⏸" else "▶"} ${evento.tipoEvento}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = evento.descripcion,
                    fontSize = 13.sp,
                    color = Color(0xFF334155),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                // IA Category Badge
                Box(
                    modifier = Modifier
                        .background(badgeBgColor, shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = evento.categoria,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Detalle",
                tint = Color.Gray
            )
        }
    }
}
