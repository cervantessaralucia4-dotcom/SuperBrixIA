package com.superbrix.ia.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbrix.ia.ui.theme.*

/**
 * Diálogo de confirmación industrial reutilizable.
 * Previene acciones críticas accidentales en piso de planta con botones táctiles grandes.
 */
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String = "Confirmar",
    dismissText: String = "Cancelar",
    icon: ImageVector = Icons.Default.Warning,
    confirmColor: Color = BrixOrangeAction,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = CardShape,
        containerColor = BrixDarkSurface,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = confirmColor,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = title,
                style = Typography.titleLarge,
                color = BrixTextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = message,
                style = Typography.bodyLarge,
                color = BrixTextSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = ButtonShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = confirmColor,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .height(48.dp)
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    text = confirmText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = ButtonShape,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = BrixTextSecondary
                ),
                modifier = Modifier.height(48.dp)
            ) {
                Text(
                    text = dismissText,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp
                )
            }
        }
    )
}
