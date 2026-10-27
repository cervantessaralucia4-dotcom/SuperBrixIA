package com.superbrix.ia.ui.screens

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.superbrix.ia.R
import com.superbrix.ia.core.security.SecurityUtils
import com.superbrix.ia.data.model.Operario
import com.superbrix.ia.ui.components.PrimaryButton
import com.superbrix.ia.ui.components.SecondaryButton
import com.superbrix.ia.ui.theme.*

/**
 * Pantalla de Login Industrial para BRIX360.
 *
 * Soporta:
 * 1. Selección visual de operario con buscador, avatar e iniciales.
 * 2. Autenticación biométrica nativa (Huella o Rostro) con manejo exhaustivo de estados.
 * 3. Fallback alternativo a PIN si la biometría no está disponible o enrolada.
 * 4. Acceso de administración discreto validado mediante hash SHA-256.
 * 5. Mensajes de error inline de alta visibilidad en planta.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    operarios: List<Operario>,
    onLoginSuccess: (Operario) -> Unit,
    onLoginAdmin: () -> Unit
) {
    val context = LocalContext.current
    var selectedOperario by remember(operarios) { mutableStateOf(operarios.firstOrNull()) }
    var showOperatorSelectorDialog by remember { mutableStateOf(false) }

    // Estados de autenticación biométrica y PIN alternativo
    var inlineErrorMessage by remember { mutableStateOf<String?>(null) }
    var showPinFallbackDialog by remember { mutableStateOf(false) }
    var operarioPinInput by remember { mutableStateOf("") }

    // Estados del panel de administración
    var showAdminSection by remember { mutableStateOf(false) }
    var adminPassword by remember { mutableStateOf("") }
    var adminPasswordVisible by remember { mutableStateOf(false) }
    var adminErrorMessage by remember { mutableStateOf<String?>(null) }

    fun launchBiometrics() {
        inlineErrorMessage = null
        val currentOp = selectedOperario
        if (currentOp == null) {
            inlineErrorMessage = "Debe seleccionar un operario de la lista"
            return
        }

        val activity = context as? FragmentActivity
        if (activity == null) {
            inlineErrorMessage = "Error del sistema: no es posible inicializar la autenticación."
            return
        }

        val biometricManager = BiometricManager.from(context)
        val canAuth = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
        )

        when (canAuth) {
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                inlineErrorMessage = "El dispositivo no cuenta con sensor biométrico. Ingrese con PIN alternativo."
                showPinFallbackDialog = true
                return
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                inlineErrorMessage = "Sensor biométrico ocupado o no disponible en este momento."
                showPinFallbackDialog = true
                return
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                inlineErrorMessage = "No hay huellas o rostro registrados en este dispositivo. Use PIN alternativo."
                showPinFallbackDialog = true
                return
            }
            else -> Unit
        }

        val executor = ContextCompat.getMainExecutor(context)
        val prompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    inlineErrorMessage = null
                    onLoginSuccess(currentOp)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode != BiometricPrompt.ERROR_USER_CANCELED && errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        inlineErrorMessage = "Error biométrico: $errString"
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    inlineErrorMessage = "Huella o rostro no reconocido. Intente nuevamente o ingrese con PIN."
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Ingreso a BRIX360")
            .setSubtitle("Operario: ${currentOp.nombre}")
            .setDescription("Coloque su huella en el sensor o mire a la cámara frontal")
            .setNegativeButtonText("Cancelar")
            .build()

        prompt.authenticate(promptInfo)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrixDarkBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Logo de BRIX360
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .shadow(16.dp, CircleShape, ambientColor = BrixVioletPrimary, spotColor = BrixCyanAccent)
                    .clip(CircleShape)
                    .background(BrixDarkSurface),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_brix360),
                    contentDescription = "Logo BRIX360",
                    modifier = Modifier.size(90.dp).clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "BRIX360",
                style = Typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = BrixTextPrimary,
                letterSpacing = 1.sp
            )

            Text(
                text = "Control Operativo de Planta",
                style = Typography.bodyMedium,
                color = BrixCyanAccent
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Selector de Operario (Tarjeta grande con Iniciales)
            Text(
                text = "OPERARIO ASIGNADO",
                style = Typography.labelSmall,
                color = BrixTextSecondary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            val currentOp = selectedOperario
            val opName = currentOp?.nombre ?: "Seleccionar Operario"
            val opInitials = opName.split(" ").take(2).mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("").ifBlank { "OP" }
            val opArea = currentOp?.area ?: "Sin área"

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showOperatorSelectorDialog = true },
                shape = CardShape,
                colors = CardDefaults.cardColors(containerColor = BrixDarkSurface),
                border = BorderStroke(1.5.dp, BrixDarkBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(BrixVioletPrimary.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = opInitials,
                            style = Typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BrixCyanAccent
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = opName,
                            style = Typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BrixTextPrimary
                        )
                        Text(
                            text = "Área: $opArea • ${currentOp?.codigo ?: ""}",
                            style = Typography.bodySmall,
                            color = BrixTextSecondary
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Cambiar",
                        tint = BrixCyanAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Mensaje de Error Inline
            if (inlineErrorMessage != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = InputShape,
                    color = BrixStateCriticalBg,
                    border = BorderStroke(1.dp, BrixStateCritical.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = BrixStateCritical,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = inlineErrorMessage ?: "",
                            style = Typography.bodySmall,
                            color = BrixTextPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Botón Biométrico Principal (Huella o Rostro)
            PrimaryButton(
                text = "ENTRAR CON HUELLA O ROSTRO",
                onClick = { launchBiometrics() },
                icon = Icons.Default.Fingerprint,
                modifier = Modifier.height(62.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Botón alternativo de PIN
            TextButton(
                onClick = { showPinFallbackDialog = true },
                modifier = Modifier.height(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Pin,
                    contentDescription = null,
                    tint = BrixCyanAccent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "O ingresar con PIN numérico",
                    style = Typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = BrixCyanAccent
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Acceso de Administración Discreto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CardShape)
                    .background(BrixDarkSurface)
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAdminSection = !showAdminSection },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = BrixTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ACCESO DE ADMINISTRACIÓN",
                                style = Typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = BrixTextSecondary,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = if (showAdminSection) "Ocultar" else "Mostrar",
                            style = Typography.bodySmall,
                            color = BrixCyanAccent,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (showAdminSection) {
                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = adminPassword,
                            onValueChange = {
                                adminPassword = it
                                adminErrorMessage = null
                            },
                            label = { Text("Contraseña de administrador", color = BrixTextSecondary) },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = BrixCyanAccent)
                            },
                            trailingIcon = {
                                IconButton(onClick = { adminPasswordVisible = !adminPasswordVisible }) {
                                    Icon(
                                        imageVector = if (adminPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = BrixTextSecondary
                                    )
                                }
                            },
                            visualTransformation = if (adminPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = InputShape,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = BrixTextPrimary,
                                unfocusedTextColor = BrixTextPrimary,
                                focusedBorderColor = BrixVioletPrimary,
                                unfocusedBorderColor = BrixDarkBorder,
                                focusedContainerColor = BrixDarkSurfaceVariant,
                                unfocusedContainerColor = BrixDarkSurfaceVariant
                            )
                        )

                        if (adminErrorMessage != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = adminErrorMessage ?: "",
                                style = Typography.bodySmall,
                                color = BrixStateCritical
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        SecondaryButton(
                            text = "Entrar al Panel Admin",
                            onClick = {
                                if (SecurityUtils.verifyAdminPassword(adminPassword)) {
                                    adminErrorMessage = null
                                    adminPassword = ""
                                    onLoginAdmin()
                                } else {
                                    adminErrorMessage = "Contraseña de administración incorrecta."
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Diálogo Buscador de Operarios
    if (showOperatorSelectorDialog) {
        var searchQuery by remember { mutableStateOf("") }
        val filtered = operarios.filter {
            it.nombre.contains(searchQuery, ignoreCase = true) ||
            it.codigo.contains(searchQuery, ignoreCase = true) ||
            it.area.contains(searchQuery, ignoreCase = true)
        }

        AlertDialog(
            onDismissRequest = { showOperatorSelectorDialog = false },
            shape = CardShape,
            containerColor = BrixDarkSurface,
            title = {
                Text(
                    text = "Seleccionar Operario",
                    style = Typography.titleLarge,
                    color = BrixTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Buscar por nombre o código...", color = BrixTextDisabled) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BrixCyanAccent) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = InputShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = BrixTextPrimary,
                            unfocusedTextColor = BrixTextPrimary,
                            focusedBorderColor = BrixCyanAccent,
                            unfocusedBorderColor = BrixDarkBorder,
                            focusedContainerColor = BrixDarkSurfaceVariant,
                            unfocusedContainerColor = BrixDarkSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        filtered.forEach { op ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedOperario = op
                                        showOperatorSelectorDialog = false
                                        inlineErrorMessage = null
                                    },
                                shape = InputShape,
                                color = if (selectedOperario?.id == op.id) BrixDarkSurfaceVariant else Color.Transparent,
                                border = BorderStroke(
                                    1.dp,
                                    if (selectedOperario?.id == op.id) BrixCyanAccent else BrixDarkBorder
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = if (selectedOperario?.id == op.id) BrixCyanAccent else BrixTextSecondary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = op.nombre,
                                            style = Typography.bodyLarge,
                                            fontWeight = FontWeight.SemiBold,
                                            color = BrixTextPrimary
                                        )
                                        Text(
                                            text = "${op.codigo} • ${op.area}",
                                            style = Typography.bodySmall,
                                            color = BrixTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showOperatorSelectorDialog = false }) {
                    Text("Cerrar", color = BrixCyanAccent, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Diálogo Fallback de PIN de Operario
    if (showPinFallbackDialog) {
        var pinError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = {
                showPinFallbackDialog = false
                operarioPinInput = ""
            },
            shape = CardShape,
            containerColor = BrixDarkSurface,
            title = {
                Text(
                    text = "Ingreso con PIN",
                    style = Typography.titleLarge,
                    color = BrixTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Ingrese el PIN de operario para ${selectedOperario?.nombre}:",
                        style = Typography.bodyMedium,
                        color = BrixTextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = operarioPinInput,
                        onValueChange = {
                            if (it.length <= 6) operarioPinInput = it
                            pinError = null
                        },
                        label = { Text("PIN de Operario (4-6 dígitos)", color = BrixTextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = InputShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = BrixTextPrimary,
                            unfocusedTextColor = BrixTextPrimary,
                            focusedBorderColor = BrixVioletPrimary,
                            unfocusedBorderColor = BrixDarkBorder,
                            focusedContainerColor = BrixDarkSurfaceVariant,
                            unfocusedContainerColor = BrixDarkSurfaceVariant
                        )
                    )

                    if (pinError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = pinError ?: "",
                            style = Typography.bodySmall,
                            color = BrixStateCritical
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val current = selectedOperario
                        // Validación provisional de PIN de operario (últimos 4 dígitos de cédula o 1234)
                        if (current != null && (operarioPinInput == "1234" || operarioPinInput == current.codigo.filter { it.isDigit() })) {
                            showPinFallbackDialog = false
                            operarioPinInput = ""
                            inlineErrorMessage = null
                            onLoginSuccess(current)
                        } else {
                            pinError = "PIN incorrecto. Intente con '1234' o consulte con su supervisor."
                        }
                    },
                    shape = ButtonShape,
                    colors = ButtonDefaults.buttonColors(containerColor = BrixVioletPrimary)
                ) {
                    Text("Ingresar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPinFallbackDialog = false
                    operarioPinInput = ""
                }) {
                    Text("Cancelar", color = BrixTextSecondary)
                }
            }
        )
    }
}
