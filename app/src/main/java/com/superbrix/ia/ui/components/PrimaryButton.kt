package com.superbrix.ia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.superbrix.ia.ui.theme.SuperBrixNavyPrimary
import com.superbrix.ia.ui.theme.SuperBrixBlueAccent

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    containerColor: Color = SuperBrixNavyPrimary,
    contentColor: Color = Color.White,
    enabled: Boolean = true
) {
    val buttonShape = RoundedCornerShape(14.dp)
    
    val isOrange = containerColor == SuperBrixBlueAccent
    val brush = if (enabled) {
        if (isOrange) {
            Brush.horizontalGradient(listOf(Color(0xFFFF9D2E), Color(0xFFFF7A00)))
        } else if (containerColor == SuperBrixNavyPrimary) {
            Brush.horizontalGradient(listOf(Color(0xFF163E70), Color(0xFF081C33)))
        } else {
            Brush.horizontalGradient(listOf(containerColor, containerColor))
        }
    } else {
        Brush.horizontalGradient(listOf(Color.LightGray, Color.Gray))
    }
    
    val shadowColor = if (isOrange) Color(0xFFFF8A00).copy(alpha = 0.5f) else containerColor.copy(alpha = 0.4f)

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .shadow(
                elevation = if (enabled) 12.dp else 0.dp,
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
            disabledContentColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(icon, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
