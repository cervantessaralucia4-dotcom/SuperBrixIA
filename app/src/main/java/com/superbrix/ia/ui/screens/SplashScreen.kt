package com.superbrix.ia.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbrix.ia.R
import com.superbrix.ia.core.config.AppConfig
import com.superbrix.ia.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Pantalla de inicio Splash con animación suave de logo, eslogan de marca
 * y versión de la aplicación.
 */
@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.85f) }

    LaunchedEffect(Unit) {
        scaleAnim.animateTo(1f, animationSpec = tween(durationMillis = 800))
        alphaAnim.animateTo(1f, animationSpec = tween(durationMillis = 800))
        delay(1400)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrixDarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(32.dp)
                .alpha(alphaAnim.value)
                .scale(scaleAnim.value)
        ) {
            // Contenedor del Logo con sombra suave
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .shadow(16.dp, CircleShape, ambientColor = BrixVioletPrimary, spotColor = BrixCyanAccent)
                    .clip(CircleShape)
                    .background(BrixDarkSurface),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_brix360),
                    contentDescription = "Logo BRIX360",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "BRIX360",
                style = Typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = BrixTextPrimary,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "TU ALIADO EN LA PLANTA",
                style = Typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = BrixCyanAccent,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            CircularProgressIndicator(
                color = BrixCyanAccent,
                strokeWidth = 3.dp,
                modifier = Modifier.size(36.dp)
            )
        }

        // Versión en la parte inferior
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp)
        ) {
            Text(
                text = "v${AppConfig.APP_VERSION_NAME} • SuperBrix Industrial",
                style = Typography.bodySmall,
                color = BrixTextDisabled
            )
        }
    }
}
