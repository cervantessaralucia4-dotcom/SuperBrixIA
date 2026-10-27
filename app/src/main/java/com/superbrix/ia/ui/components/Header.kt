package com.superbrix.ia.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbrix.ia.ui.theme.*

/**
 * Cabecera principal para BRIX360 con identidad de marca, avatar con iniciales,
 * área asignada y acceso a perfil.
 */
@Composable
fun Header(
    userName: String = "Operario",
    areaName: String = "Planta General",
    title: String = "Control de Operaciones",
    onProfileClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val cleanName = userName.ifBlank { "Operario" }
    val initials = cleanName.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .map { it.first().uppercase() }
        .joinToString("")
        .ifEmpty { "OP" }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BrixDarkBackground)
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 16.dp)
    ) {
        // Barra superior de Marca BRIX360
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(BrixCyanAccent)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "BRIX360",
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = BrixTextPrimary,
                        letterSpacing = 1.5.sp
                    )
                }
                Text(
                    text = "TU ALIADO EN LA PLANTA",
                    style = Typography.labelSmall,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrixCyanAccent,
                    letterSpacing = 1.sp
                )
            }

            // Avatar de Operario con botón a perfil
            if (onProfileClick != null) {
                IconButton(
                    onClick = onProfileClick,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(BrixDarkSurfaceVariant)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = initials,
                            style = Typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = BrixCyanAccent
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Saludo personalizado y área
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Hola, $cleanName",
                    style = Typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = BrixTextPrimary
                )
                Text(
                    text = title,
                    style = Typography.bodyMedium,
                    color = BrixTextSecondary
                )
            }

            if (areaName.isNotBlank()) {
                Surface(
                    shape = ChipShape,
                    color = BrixDarkSurface,
                    border = BorderStroke(1.dp, BrixDarkBorder)
                ) {
                    Text(
                        text = areaName,
                        style = Typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = BrixTextSecondary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Línea divisoria decorativa con el degradado de marca
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(BrixPrimaryGradient)
        )
    }
}
