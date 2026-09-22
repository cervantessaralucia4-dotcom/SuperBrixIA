package com.superbrix.ia.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbrix.ia.ui.theme.SuperBrixNavyPrimary

@Composable
fun Header(
    userName: String = "Sara",
    title: String = "Registro de operación",
    onProfileClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Hola, $userName",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = SuperBrixNavyPrimary
            )
            Text(
                text = title,
                fontSize = 15.sp,
                color = Color.Gray
            )
        }

        if (onProfileClick != null) {
            OutlinedButton(
                onClick = onProfileClick,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Person, contentDescription = "Perfil")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Perfil")
            }
        }
    }
}
