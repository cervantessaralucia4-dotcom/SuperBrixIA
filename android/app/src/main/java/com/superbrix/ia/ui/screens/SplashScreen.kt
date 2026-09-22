package com.superbrix.ia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbrix.ia.ui.theme.SuperBrixNavyPrimary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(2000)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SuperBrixNavyPrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "SUPERBRIX",
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 2.sp
            )
            Text(
                text = "IA Operativa",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF81D4FA)
            )

            Spacer(modifier = Modifier.height(48.dp))

            CircularProgressIndicator(
                color = Color(0xFF0288D1),
                strokeWidth = 3.dp,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}
