package com.superbrix.ia.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.superbrix.ia.ui.components.PrimaryButton
import com.superbrix.ia.ui.theme.*

/**
 * Pantalla de Reporte de Novedad e Interrupción para BRIX360.
 *
 * Características:
 * - Dictado por voz asistido con animación de pulso concéntrico.
 * - Chips de sugerencias operativas rápidas para registro en 1 toque.
 * - Soporte para adjuntar evidencia fotográfica de la avería.
 * - Botón fijo de envío con área táctil amplia para uso con guantes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReporteNovedadScreen(
    ordenCodigo: String = "OP-001",
    maquinaNombre: String = "Fresadora 01",
    onSubmitReport: (descripcion: String, fotoUri: String?) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var descripcionText by remember { mutableStateOf("") }
    var attachedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var isListeningVoice by remember { mutableStateOf(false) }

    // Animación de pulso para el botón de micrófono
    val infiniteTransition = rememberInfiniteTransition(label = "MicPulse")
    val micScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isListeningVoice) 1.25f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "MicScale"
    )

    // Selector de Voz
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isListeningVoice = false
        if (result.resultCode == Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                val spoken = matches[0]
                descripcionText = if (descripcionText.isBlank()) spoken else "$descripcionText $spoken"
            }
        }
    }

    // Selector de Foto (Galería o Cámara)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            attachedPhotoUri = uri
        }
    }

    val sugerenciasRapidas = listOf(
        "Falta material o insumo",
        "Falla técnica de máquina",
        "Espero instrucciones de supervisor",
        "Avería eléctrica / corte de luz",
        "Inspección de calidad y medición",
        "Accidente o emergencia de salud"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Reportar Novedad",
                        style = Typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = BrixTextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = BrixTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BrixDarkBackground
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = BrixDarkSurface,
                border = BorderStroke(1.dp, BrixDarkBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    PrimaryButton(
                        text = "ENVIAR A CLASIFICACIÓN IA",
                        onClick = {
                            if (descripcionText.trim().isBlank()) {
                                Toast.makeText(context, "Por favor describa lo ocurrido o grabe por voz", Toast.LENGTH_SHORT).show()
                            } else {
                                onSubmitReport(descripcionText.trim(), attachedPhotoUri?.toString())
                            }
                        },
                        icon = Icons.AutoMirrored.Filled.Send,
                        containerColor = BrixOrangeAction,
                        modifier = Modifier.height(60.dp)
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BrixDarkBackground)
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Tarjeta Resumen de la Estación Afectada
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    colors = CardDefaults.cardColors(containerColor = BrixDarkSurface),
                    border = BorderStroke(1.dp, BrixDarkBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "ORDEN AFECTADA", style = Typography.labelSmall, color = BrixTextSecondary)
                            Text(text = ordenCodigo, style = Typography.titleMedium, fontWeight = FontWeight.Bold, color = BrixCyanAccent)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "MÁQUINA ASIGNADA", style = Typography.labelSmall, color = BrixTextSecondary)
                            Text(text = maquinaNombre, style = Typography.titleMedium, fontWeight = FontWeight.Bold, color = BrixTextPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "¿QUÉ OCURRIÓ EN LA PLANTA?",
                    style = Typography.labelSmall,
                    color = BrixTextSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Campo de Texto de Descripción
                OutlinedTextField(
                    value = descripcionText,
                    onValueChange = { descripcionText = it },
                    placeholder = {
                        Text(
                            "Escriba o dicte la novedad (ej. 'se trabó el husillo por viruta acumulada')...",
                            color = BrixTextDisabled,
                            style = Typography.bodyMedium
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    shape = CardShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = BrixTextPrimary,
                        unfocusedTextColor = BrixTextPrimary,
                        focusedBorderColor = BrixVioletPrimary,
                        unfocusedBorderColor = BrixDarkBorder,
                        focusedContainerColor = BrixDarkSurface,
                        unfocusedContainerColor = BrixDarkSurface
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Controles de Entrada Rápida: Micrófono y Foto
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón Circular de Dictado por Voz con Animación
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clickable {
                                isListeningVoice = true
                                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-CO")
                                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Hable con claridad sobre lo ocurrido...")
                                }
                                try {
                                    speechLauncher.launch(intent)
                                } catch (e: Exception) {
                                    isListeningVoice = false
                                    Toast.makeText(context, "Entrada por voz no disponible", Toast.LENGTH_SHORT).show()
                                }
                            },
                        shape = ButtonShape,
                        color = if (isListeningVoice) BrixOrangeAction.copy(alpha = 0.2f) else BrixDarkSurface,
                        border = BorderStroke(1.5.dp, if (isListeningVoice) BrixOrangeAction else BrixDarkBorder)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Dictar por voz",
                                    tint = if (isListeningVoice) BrixOrangeAction else BrixCyanAccent,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .scale(micScale)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isListeningVoice) "ESCUCHANDO..." else "DICTAR VOZ",
                                style = Typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isListeningVoice) BrixOrangeAction else BrixTextPrimary
                            )
                        }
                    }

                    // Botón de Adjuntar Foto
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clickable {
                                photoPickerLauncher.launch("image/*")
                            },
                        shape = ButtonShape,
                        color = BrixDarkSurface,
                        border = BorderStroke(1.5.dp, BrixDarkBorder)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Adjuntar foto",
                                tint = BrixVioletPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (attachedPhotoUri != null) "FOTO LISTA" else "FOTO (OPCIONAL)",
                                style = Typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = BrixTextPrimary
                            )
                        }
                    }
                }

                // Previsualización de Foto si fue adjuntada
                if (attachedPhotoUri != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(CardShape)
                            .background(BrixDarkSurfaceVariant)
                    ) {
                        AsyncImage(
                            model = attachedPhotoUri,
                            contentDescription = "Evidencia fotográfica",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { attachedPhotoUri = null },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Quitar foto", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "SUGERENCIAS RÁPIDAS DE PLANTA",
                    style = Typography.labelSmall,
                    color = BrixTextSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Chips de Sugerencias Rápidas
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    sugerenciasRapidas.chunked(2).forEach { rowChips ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowChips.forEach { chipText ->
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            descripcionText = chipText
                                        },
                                    shape = ChipShape,
                                    color = BrixDarkSurface,
                                    border = BorderStroke(1.dp, BrixDarkBorder)
                                ) {
                                    Text(
                                        text = chipText,
                                        style = Typography.bodySmall,
                                        color = BrixTextPrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)
                                    )
                                }
                            }
                            if (rowChips.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp)) // Espacio para el bottomBar fijo
            }
        }
    }
}
