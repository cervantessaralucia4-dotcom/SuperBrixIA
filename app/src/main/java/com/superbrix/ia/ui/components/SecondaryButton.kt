package com.superbrix.ia.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbrix.ia.ui.theme.*

/**
 * Botón secundario para BRIX360.
 * Diseñado con borde industrial sutil, fondo de tarjeta y texto nítido.
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = ButtonShape,
        border = BorderStroke(1.5.dp, BrixDarkBorder),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = BrixDarkSurface,
            contentColor = BrixTextPrimary,
            disabledContainerColor = BrixDarkSurfaceVariant,
            disabledContentColor = BrixTextDisabled
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BrixCyanAccent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = text,
                style = Typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (enabled) BrixTextPrimary else BrixTextDisabled
            )
        }
    }
}
