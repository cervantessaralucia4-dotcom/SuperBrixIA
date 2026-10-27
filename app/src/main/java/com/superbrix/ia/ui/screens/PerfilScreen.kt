package com.superbrix.ia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbrix.ia.data.model.Operario
import com.superbrix.ia.ui.components.ConfirmDialog
import com.superbrix.ia.ui.components.PrimaryButton
import com.superbrix.ia.ui.components.SecondaryButton
import com.superbrix.ia.ui.theme.*

/**
 * Pantalla de Perfil de Operario para BRIX360.
 *
 * Características:
 * - Avatar con iniciales en alto contraste.
 * - Información formal de planta (FO-A-MA-01).
 * - Acceso a Configuración de la aplicación.
 * - Diálogo de confirmación para cerrar sesión segura.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    operario: Operario = Operario(),
    onSettingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }

    val cleanName = operario.nombre.ifBlank { "Operario de Planta" }
    val initials = cleanName.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString("")
        .ifEmpty { "OP" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Perfil del Operario",
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
                    .widthIn(max = 560.dp)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Avatar con Iniciales
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(BrixPrimaryGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        style = Typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = cleanName,
                    style = Typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = BrixTextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = ChipShape,
                        color = BrixDarkSurfaceVariant
                    ) {
                        Text(
                            text = operario.rol,
                            style = Typography.bodySmall,
                            color = BrixCyanAccent,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = ChipShape,
                        color = BrixStateProducingBg
                    ) {
                        Text(
                            text = operario.estado.ifBlank { "Activo" },
                            style = Typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = BrixStateProducing,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Tarjeta de Datos del Operario
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    colors = CardDefaults.cardColors(containerColor = BrixDarkSurface),
                    border = BorderStroke(1.dp, BrixDarkBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "FICHA OPERATIVA (FO-A-MA-01)",
                            style = Typography.labelSmall,
                            color = BrixCyanAccent,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        PerfilRowItem(
                            label = "Cédula de Empleado",
                            value = operario.cedula.ifBlank { "No registrada" }
                        )

                        PerfilRowItem(
                            label = "Código de Operario",
                            value = operario.codigo.ifBlank { "OP-001" }
                        )

                        PerfilRowItem(
                            label = "Área Asignada",
                            value = operario.area.ifBlank { "Planta General" }
                        )

                        PerfilRowItem(
                            label = "Centro de Trabajo",
                            value = operario.centroTrabajo.ifBlank { "CT-General" }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Botón Configuración
                SecondaryButton(
                    text = "Configuración del Sistema",
                    onClick = onSettingsClick,
                    icon = Icons.Default.Settings
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Botón Cerrar Sesión (con confirmación)
                PrimaryButton(
                    text = "CERRAR SESIÓN",
                    onClick = { showLogoutConfirmDialog = true },
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    containerColor = BrixStateCritical,
                    modifier = Modifier.height(58.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Diálogo de Confirmación para Cerrar Sesión
    if (showLogoutConfirmDialog) {
        ConfirmDialog(
            title = "¿Cerrar sesión de operario?",
            message = "¿Está seguro de cerrar la sesión activa de $cleanName? Para volver a registrar operaciones deberá autenticarse nuevamente.",
            confirmText = "Sí, Cerrar Sesión",
            dismissText = "Cancelar",
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            confirmColor = BrixStateCritical,
            onConfirm = {
                showLogoutConfirmDialog = false
                onLogoutClick()
            },
            onDismiss = { showLogoutConfirmDialog = false }
        )
    }
}

@Composable
private fun PerfilRowItem(
    label: String,
    value: String
) {
    Column {
        Text(text = label, style = Typography.labelSmall, color = BrixTextSecondary)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = Typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = BrixTextPrimary
        )
    }
}
