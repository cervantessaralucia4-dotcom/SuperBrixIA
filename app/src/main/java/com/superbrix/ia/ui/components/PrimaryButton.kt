package com.superbrix.ia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbrix.ia.ui.theme.*

/**
 * Botón primario industrial para BRIX360.
 *
 * Características clave para uso en planta:
 * - Área táctil extendida de mínimo 58-62 dp (operación cómoda con guantes).
 * - Acento visual con el degradado del logo (violeta #7C3AED -> cian #00C2FF)
 *   o colores semánticos de acción crítica (naranja #FF8A00, verde #1DB954).
 * - Tipografía Inter Bold y soporte de iconos vectoriales Material.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    containerColor: Color? = null,
    customBrush: Brush? = null,
    contentColor: Color = Color.White,
    enabled: Boolean = true
) {
    val buttonShape = ButtonShape

    val effectiveBrush = when {
        !enabled -> Brush.horizontalGradient(listOf(Color(0xFF334155), Color(0xFF1E293B)))
        customBrush != null -> customBrush
        containerColor == BrixOrangeAction -> BrixOrangeGradient
        containerColor == BrixStateProducing -> Brush.horizontalGradient(listOf(Color(0xFF22C55E), Color(0xFF16A34A)))
        containerColor == BrixStateCritical -> Brush.horizontalGradient(listOf(Color(0xFFEF4444), Color(0xFFDC2626)))
        containerColor != null -> Brush.horizontalGradient(listOf(containerColor, containerColor))
        else -> BrixPrimaryGradient
    }

    val shadowColor = when (containerColor) {
        BrixOrangeAction -> BrixOrangeAction.copy(alpha = 0.35f)
        BrixStateProducing -> BrixStateProducing.copy(alpha = 0.35f)
        BrixStateCritical -> BrixStateCritical.copy(alpha = 0.35f)
        else -> BrixVioletPrimary.copy(alpha = 0.4f)
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .shadow(
                elevation = if (enabled) 10.dp else 0.dp,
                shape = buttonShape,
                ambientColor = shadowColor,
                spotColor = shadowColor
            ),
        shape = buttonShape,
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = contentColor,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = BrixTextDisabled
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(effectiveBrush),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Text(
                    text = text,
                    style = Typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
