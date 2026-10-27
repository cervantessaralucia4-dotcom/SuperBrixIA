package com.superbrix.ia.ui.screens

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbrix.ia.core.config.AppConfig
import com.superbrix.ia.core.network.NetworkMonitor
import com.superbrix.ia.core.notifications.PlantNotificationHelper
import com.superbrix.ia.data.local.PreferencesManager
import com.superbrix.ia.ui.components.ConfirmDialog
import com.superbrix.ia.ui.components.PrimaryButton
import com.superbrix.ia.ui.components.SecondaryButton
import com.superbrix.ia.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Pantalla de Configuración Industrial de BRIX360.
 *
 * Características:
 * - Monitor en tiempo real de conectividad de red de planta (sin estados falsos fijos).
 * - Persistencia real de preferencias en DataStore (Tema oscuro, Notificaciones, Reconocimiento de voz).
 * - Cola de eventos pendientes con botón de sincronización manual forzada.
 * - Diálogo de confirmación para restablecer datos.
 * - Cero emojis en la interfaz.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionScreen(
    pendingSyncCount: Int = 0,
    onManualSyncClick: () -> Unit = {},
    onResetDataClick: () -> Unit = {},
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val prefsManager = remember { PreferencesManager(context) }
    val networkMonitor = remember { NetworkMonitor(context) }

    // Estados reactivos persistidos
    val isDarkMode by prefsManager.isDarkModeFlow.collectAsState(initial = true)
    val notificationsEnabled by prefsManager.notificationsEnabledFlow.collectAsState(initial = true)
    val voiceEnabled by prefsManager.voiceEnabledFlow.collectAsState(initial = true)

    // Estado reactivo de red real
    val isOnline by networkMonitor.isOnlineFlow.collectAsState(initial = networkMonitor.isOnline())

    var showResetConfirmDialog by remember { mutableStateOf(false) }

    // Permiso de notificaciones en Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        coroutineScope.launch {
            prefsManager.setNotificationsEnabled(isGranted)
        }
        if (isGranted) {
            PlantNotificationHelper.createNotificationChannel(context)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Configuración",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrixDarkBackground)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BrixDarkBackground)
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // SECCIÓN 1: Interfaz y Tema
                ConfigSectionTitle(title = "INTERFAZ Y VISUALIZACIÓN")

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    colors = CardDefaults.cardColors(containerColor = BrixDarkSurface),
                    border = BorderStroke(1.dp, BrixDarkBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.Brightness4, contentDescription = null, tint = BrixCyanAccent)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Tema Oscuro Industrial",
                                        style = Typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = BrixTextPrimary
                                    )
                                    Text(
                                        text = "Alto contraste para iluminación de planta",
                                        style = Typography.bodySmall,
                                        color = BrixTextSecondary
                                    )
                                }
                            }
                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = { checked ->
                                    coroutineScope.launch { prefsManager.setDarkMode(checked) }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = BrixVioletPrimary,
                                    uncheckedThumbColor = BrixTextDisabled,
                                    uncheckedTrackColor = BrixDarkSurfaceVariant
                                )
                            )
                        }
                    }
                }

                // SECCIÓN 2: Sincronización y Red en Tiempo Real
                ConfigSectionTitle(title = "SINCRONIZACIÓN Y RED INDUSTRIAL")

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    colors = CardDefaults.cardColors(containerColor = BrixDarkSurface),
                    border = BorderStroke(1.dp, BrixDarkBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Estado real de red
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Estado de Red en Planta:", style = Typography.labelSmall, color = BrixTextSecondary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(if (isOnline) BrixStateProducing else BrixStatePaused)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isOnline) "En Línea (Conectado)" else "Fuera de Línea (Modo Local)",
                                        style = Typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isOnline) BrixStateProducing else BrixStatePaused
                                    )
                                }
                            }
                            Icon(
                                imageVector = if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
                                contentDescription = null,
                                tint = if (isOnline) BrixStateProducing else BrixStatePaused
                            )
                        }

                        HorizontalDivider(color = BrixDarkBorder, thickness = 0.8.dp)

                        // Servidor configurado
                        Column {
                            Text(text = "Endpoint de Backend:", style = Typography.labelSmall, color = BrixTextSecondary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = AppConfig.BASE_URL,
                                style = Typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = BrixCyanAccent
                            )
                        }

                        HorizontalDivider(color = BrixDarkBorder, thickness = 0.8.dp)

                        // Cola de eventos pendientes
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Eventos en Cola Local:", style = Typography.labelSmall, color = BrixTextSecondary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (pendingSyncCount == 0) "Todos los eventos sincronizados" else "$pendingSyncCount eventos pendientes",
                                    style = Typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (pendingSyncCount == 0) BrixTextPrimary else BrixOrangeAction
                                )
                            }

                            Button(
                                onClick = {
                                    onManualSyncClick()
                                    Toast.makeText(context, "Sincronizando eventos con el servidor...", Toast.LENGTH_SHORT).show()
                                },
                                shape = ButtonShape,
                                colors = ButtonDefaults.buttonColors(containerColor = BrixVioletPrimary)
                            ) {
                                Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sincronizar", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // SECCIÓN 3: Alertas y Asistencia Operativa
                ConfigSectionTitle(title = "ALERTAS Y ASISTENCIA DE PLANTA")

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    colors = CardDefaults.cardColors(containerColor = BrixDarkSurface),
                    border = BorderStroke(1.dp, BrixDarkBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.Notifications, contentDescription = null, tint = BrixOrangeAction)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Notificaciones de Seguridad",
                                        style = Typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = BrixTextPrimary
                                    )
                                    Text(
                                        text = "Alertas de emergencia y fallas críticas",
                                        style = Typography.bodySmall,
                                        color = BrixTextSecondary
                                    )
                                }
                            }
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = { checked ->
                                    if (checked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        coroutineScope.launch { prefsManager.setNotificationsEnabled(checked) }
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = BrixVioletPrimary
                                )
                            )
                        }

                        HorizontalDivider(color = BrixDarkBorder, modifier = Modifier.padding(vertical = 12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = BrixCyanAccent)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Reconocimiento de Voz",
                                        style = Typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = BrixTextPrimary
                                    )
                                    Text(
                                        text = "Habilitar micrófono para dictado de novedad",
                                        style = Typography.bodySmall,
                                        color = BrixTextSecondary
                                    )
                                }
                            }
                            Switch(
                                checked = voiceEnabled,
                                onCheckedChange = { checked ->
                                    coroutineScope.launch { prefsManager.setVoiceEnabled(checked) }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = BrixVioletPrimary
                                )
                            )
                        }
                    }
                }

                // SECCIÓN 4: Seguridad y Acerca de
                ConfigSectionTitle(title = "SEGURIDAD Y ACERCA DE")

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    colors = CardDefaults.cardColors(containerColor = BrixDarkSurface),
                    border = BorderStroke(1.dp, BrixDarkBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = BrixCyanAccent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Autenticación Criptográfica: SHA-256 Activa",
                                style = Typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = BrixTextPrimary
                            )
                        }

                        Text(
                            text = "BRIX360 (SuperBrix) v${AppConfig.APP_VERSION_NAME} • Build ${AppConfig.APP_VERSION_CODE}",
                            style = Typography.bodySmall,
                            color = BrixTextSecondary
                        )

                        Text(
                            text = "Eslogan: Tu aliado en la planta",
                            style = Typography.bodySmall,
                            color = BrixCyanAccent,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Acción Crítica: Restablecer Datos de Demostración con Confirmación
                SecondaryButton(
                    text = "Restablecer datos de demostración",
                    onClick = { showResetConfirmDialog = true },
                    icon = Icons.Default.Refresh
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Diálogo de Confirmación para Restablecer Datos
    if (showResetConfirmDialog) {
        ConfirmDialog(
            title = "¿Restablecer datos de demostración?",
            message = "Esta acción reiniciará los registros temporales y volverá a los valores iniciales de prueba en planta. ¿Desea continuar?",
            confirmText = "Sí, Restablecer",
            dismissText = "Cancelar",
            icon = Icons.Default.Refresh,
            confirmColor = BrixOrangeAction,
            onConfirm = {
                showResetConfirmDialog = false
                onResetDataClick()
                Toast.makeText(context, "Datos de demostración restablecidos", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showResetConfirmDialog = false }
        )
    }
}

@Composable
private fun ConfigSectionTitle(title: String) {
    Text(
        text = title,
        style = Typography.labelSmall,
        color = BrixTextSecondary,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp
    )
}
