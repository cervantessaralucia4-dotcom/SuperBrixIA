package com.superbrix.ia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbrix.ia.ui.theme.IndustrialBlueLight
import com.superbrix.ia.ui.theme.IndustrialBlueTech

@Composable
fun StatusCard(
    statusMessage: String = "Listo para trabajar",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(IndustrialBlueLight)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = IndustrialBlueTech
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "ESTADO: ",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = IndustrialBlueTech,
                letterSpacing = 0.5.sp
            )
            Text(
                text = statusMessage.uppercase(),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = IndustrialBlueTech
            )
        }
    }
}
