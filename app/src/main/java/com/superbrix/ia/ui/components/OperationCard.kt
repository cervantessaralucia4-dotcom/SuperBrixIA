package com.superbrix.ia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbrix.ia.ui.theme.IndustrialGreenActive
import com.superbrix.ia.ui.theme.IndustrialRedAlert
import com.superbrix.ia.ui.theme.SuperBrixNavyPrimary

@Composable
fun OperationCard(
    ordenCodigo: String,
    maquinaNombre: String,
    estadoText: String,
    isLaborActive: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "INFORMACIÓN DE OPERACIÓN",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Orden de producción:",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = ordenCodigo,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuperBrixNavyPrimary
                    )
                }

                Column {
                    Text(
                        text = "Máquina:",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = maquinaNombre,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuperBrixNavyPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val (badgeBg, badgeText) = if (isLaborActive) {
                IndustrialGreenActive.copy(alpha = 0.12f) to IndustrialGreenActive
            } else if (estadoText.contains("Interrup", true)) {
                IndustrialRedAlert.copy(alpha = 0.12f) to IndustrialRedAlert
            } else {
                Color(0xFFF1F5F9) to Color.Gray
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(badgeBg, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Estado actual: $estadoText",
                    color = badgeText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
