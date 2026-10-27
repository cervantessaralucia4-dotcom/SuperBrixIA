package com.superbrix.ia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbrix.ia.core.security.SecurityUtils
import com.superbrix.ia.ui.components.PrimaryButton
import com.superbrix.ia.ui.theme.*
import com.superbrix.ia.viewmodel.DatosSuperBrix

/**
 * Pantalla de Selección de Operación Industrial con Stepper Guiado y Verificación de PIN.
 *
 * Pasos:
 * 1. Orden de Producción (OP)
 * 2. Área de Trabajo (cambiar de área asignada requiere PIN de supervisor verificado con SHA-256)
 * 3. Tipo de Operación
 * 4. Máquina Asignada
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeleccionOperacionScreen(
    initialOrder: String,
    initialMachine: String,
    initialOperation: String,
    initialArea: String = "CNC",
    onConfirmStartLabor: (String, String, String, String) -> Unit, // OP, Maquina, Operacion, Area
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    var selectedOP by remember { mutableStateOf(if (initialOrder.isBlank()) DatosSuperBrix.ordenesProduccion.first() else initialOrder) }
    var selectedArea by remember { mutableStateOf(if (initialArea.isBlank()) "CNC" else initialArea) }

    val operaciones = DatosSuperBrix.areasYOperaciones[selectedArea] ?: listOf("Producción General")
    val maquinas = DatosSuperBrix.maquinasPorArea[selectedArea] ?: listOf("No aplica")

    var selectedOperation by remember { mutableStateOf(if (operaciones.contains(initialOperation)) initialOperation else operaciones.first()) }
    var selectedMachine by remember { mutableStateOf(if (maquinas.contains(initialMachine)) initialMachine else maquinas.first()) }

    // Diálogos de selección
    var pickerType by remember { mutableStateOf<String?>(null) } // "OP", "AREA", "OPERACION", "MAQUINA"

    // Diálogo de PIN de supervisor
    var showPinDialog by remember { mutableStateOf(false) }
    var pendingArea by remember { mutableStateOf<String?>(null) }
    var supervisorPinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedArea) {
        val ops = DatosSuperBrix.areasYOperaciones[selectedArea] ?: listOf("Producción General")
        val maqs = DatosSuperBrix.maquinasPorArea[selectedArea] ?: listOf("No aplica")
        if (!ops.contains(selectedOperation)) selectedOperation = ops.first()
        if (!maqs.contains(selectedMachine)) selectedMachine = maqs.first()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Configurar Labor",
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
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Paso 1: Orden de Producción
                StepSelectionCard(
                    stepNumber = "1",
                    title = "Orden de Producción (OP)",
                    currentValue = selectedOP,
                    icon = Icons.Default.Assignment,
                    onClick = { pickerType = "OP" }
                )

                // Paso 2: Área de Trabajo
                StepSelectionCard(
                    stepNumber = "2",
                    title = "Área de Manufactura",
                    currentValue = selectedArea,
                    icon = Icons.Default.LocationOn,
                    badgeText = if (selectedArea != initialArea) "Área modificada" else "Área asignada",
                    badgeColor = if (selectedArea != initialArea) BrixOrangeAction else BrixCyanAccent,
                    onClick = { pickerType = "AREA" }
                )

                // Paso 3: Tipo de Operación
                StepSelectionCard(
                    stepNumber = "3",
                    title = "Operación",
                    currentValue = selectedOperation,
                    icon = Icons.Default.Build,
                    onClick = { pickerType = "OPERACION" }
                )

                // Paso 4: Máquina Asignada
                StepSelectionCard(
                    stepNumber = "4",
                    title = "Máquina / Estación",
                    currentValue = selectedMachine,
                    icon = Icons.Default.PrecisionManufacturing,
                    onClick = { pickerType = "MAQUINA" }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Resumen antes de confirmar
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    colors = CardDefaults.cardColors(containerColor = BrixDarkSurfaceVariant),
                    border = BorderStroke(1.dp, BrixDarkBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "RESUMEN DE ASIGNACIÓN",
                            style = Typography.labelSmall,
                            color = BrixCyanAccent,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$selectedOP • $selectedOperation",
                            style = Typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BrixTextPrimary
                        )
                        Text(
                            text = "Estación: $selectedMachine en $selectedArea",
                            style = Typography.bodyMedium,
                            color = BrixTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botón Iniciar Operación
                PrimaryButton(
                    text = "INICIAR OPERACIÓN",
                    onClick = {
                        onConfirmStartLabor(selectedOP, selectedMachine, selectedOperation, selectedArea)
                    },
                    icon = Icons.Default.Check,
                    containerColor = BrixStateProducing,
                    modifier = Modifier.height(60.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Diálogo Selector Genérico con Buscador
    if (pickerType != null) {
        val (title, options, current) = when (pickerType) {
            "OP" -> Triple("Seleccionar Orden", DatosSuperBrix.ordenesProduccion, selectedOP)
            "AREA" -> Triple("Seleccionar Área", DatosSuperBrix.areasYOperaciones.keys.toList(), selectedArea)
            "OPERACION" -> Triple("Seleccionar Operación", operaciones, selectedOperation)
            else -> Triple("Seleccionar Máquina", maquinas, selectedMachine)
        }

        var searchTxt by remember { mutableStateOf("") }
        val filtered = options.filter { it.contains(searchTxt, ignoreCase = true) }

        AlertDialog(
            onDismissRequest = { pickerType = null },
            shape = CardShape,
            containerColor = BrixDarkSurface,
            title = {
                Text(
                    text = title,
                    style = Typography.titleLarge,
                    color = BrixTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = searchTxt,
                        onValueChange = { searchTxt = it },
                        placeholder = { Text("Buscar...", color = BrixTextDisabled) },
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
                            .heightIn(max = 260.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        filtered.forEach { opt ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        when (pickerType) {
                                            "OP" -> selectedOP = opt
                                            "AREA" -> {
                                                if (opt != initialArea) {
                                                    pendingArea = opt
                                                    showPinDialog = true
                                                } else {
                                                    selectedArea = opt
                                                }
                                            }
                                            "OPERACION" -> selectedOperation = opt
                                            "MAQUINA" -> selectedMachine = opt
                                        }
                                        pickerType = null
                                    },
                                shape = InputShape,
                                color = if (opt == current) BrixVioletPrimary.copy(alpha = 0.2f) else BrixDarkSurfaceVariant,
                                border = BorderStroke(1.dp, if (opt == current) BrixCyanAccent else BrixDarkBorder)
                            ) {
                                Text(
                                    text = opt,
                                    style = Typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (opt == current) BrixCyanAccent else BrixTextPrimary,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { pickerType = null }) {
                    Text("Cerrar", color = BrixTextSecondary)
                }
            }
        )
    }

    // Diálogo de Autorización de Supervisor con Verificación Segura SHA-256
    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = {
                showPinDialog = false
                pendingArea = null
                supervisorPinInput = ""
                pinError = null
            },
            shape = CardShape,
            containerColor = BrixDarkSurface,
            icon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = BrixOrangeAction,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Autorización de Supervisor",
                    style = Typography.titleLarge,
                    color = BrixTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "El operario está asignado al área $initialArea. Para laborar en $pendingArea se requiere la autorización del supervisor de turno.",
                        style = Typography.bodyMedium,
                        color = BrixTextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = supervisorPinInput,
                        onValueChange = {
                            supervisorPinInput = it
                            pinError = null
                        },
                        label = { Text("PIN de Supervisor", color = BrixTextSecondary) },
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
                        if (SecurityUtils.verifySupervisorPin(supervisorPinInput)) {
                            selectedArea = pendingArea ?: initialArea
                            showPinDialog = false
                            pendingArea = null
                            supervisorPinInput = ""
                            pinError = null
                        } else {
                            pinError = "PIN incorrecto. Consulte con el supervisor de planta."
                        }
                    },
                    shape = ButtonShape,
                    colors = ButtonDefaults.buttonColors(containerColor = BrixOrangeAction)
                ) {
                    Text("Autorizar Cambio", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPinDialog = false
                    pendingArea = null
                    supervisorPinInput = ""
                    pinError = null
                }) {
                    Text("Cancelar", color = BrixTextSecondary)
                }
            }
        )
    }
}

@Composable
private fun StepSelectionCard(
    stepNumber: String,
    title: String,
    currentValue: String,
    icon: ImageVector,
    badgeText: String? = null,
    badgeColor: Color = BrixCyanAccent,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
            // Círculo con el número de paso
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(BrixVioletPrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stepNumber,
                    style = Typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BrixCyanAccent
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = Typography.labelSmall,
                        color = BrixTextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    if (badgeText != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = ChipShape,
                            color = badgeColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = badgeText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currentValue,
                    style = Typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BrixTextPrimary
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Seleccionar",
                tint = BrixCyanAccent
            )
        }
    }
}
