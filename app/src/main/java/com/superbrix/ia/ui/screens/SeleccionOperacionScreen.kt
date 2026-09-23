package com.superbrix.ia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbrix.ia.ui.theme.IndustrialGrayBackground
import com.superbrix.ia.ui.theme.SuperBrixNavyPrimary
import com.superbrix.ia.viewmodel.DatosSuperBrix

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeleccionOperacionScreen(
    initialOrder: String,
    initialMachine: String,
    initialOperation: String,
    initialArea: String = "Ensamble 1",
    onConfirmStartLabor: (String, String, String, String) -> Unit, // OP, Maquina, Operacion, Area
    onBackClick: () -> Unit
) {
    var selectedOP by remember { mutableStateOf(if(initialOrder.isBlank()) DatosSuperBrix.ordenesProduccion.first() else initialOrder) }
    var selectedArea by remember { mutableStateOf(initialArea) }
    
    val operaciones = DatosSuperBrix.areasYOperaciones[selectedArea] ?: listOf("N/A")
    val maquinas = DatosSuperBrix.maquinasPorArea[selectedArea] ?: listOf("No aplica")
    
    var selectedOperation by remember { mutableStateOf(operaciones.first()) }
    var selectedMachine by remember { mutableStateOf(maquinas.first()) }

    // Reset dependents when area changes
    LaunchedEffect(selectedArea) {
        selectedOperation = DatosSuperBrix.areasYOperaciones[selectedArea]?.firstOrNull() ?: "N/A"
        selectedMachine = DatosSuperBrix.maquinasPorArea[selectedArea]?.firstOrNull() ?: "No aplica"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Iniciar Labor", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Atrás", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SuperBrixNavyPrimary)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(IndustrialGrayBackground)
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            Text("1. Selecciona la OP", fontWeight = FontWeight.Bold, color = SuperBrixNavyPrimary)
            DropdownSelector(options = DatosSuperBrix.ordenesProduccion, selected = selectedOP) { selectedOP = it }

            var showPinDialog by remember { mutableStateOf(false) }
            var pendingArea by remember { mutableStateOf<String?>(null) }
            var supervisorPin by remember { mutableStateOf("") }
            val context = androidx.compose.ui.platform.LocalContext.current

            Text("2. Selecciona el Área", fontWeight = FontWeight.Bold, color = SuperBrixNavyPrimary)
            DropdownSelector(options = DatosSuperBrix.areasYOperaciones.keys.toList(), selected = selectedArea) { newArea -> 
                if (newArea != initialArea) {
                    pendingArea = newArea
                    showPinDialog = true
                } else {
                    selectedArea = newArea
                }
            }

            if (showPinDialog) {
                AlertDialog(
                    onDismissRequest = { 
                        showPinDialog = false
                        pendingArea = null
                        supervisorPin = ""
                    },
                    title = { Text("Autorización de Supervisor") },
                    text = {
                        Column {
                            Text("Para trabajar en un área diferente a la asignada ($initialArea), ingresa el PIN.")
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = supervisorPin,
                                onValueChange = { supervisorPin = it },
                                label = { Text("PIN de Supervisor") },
                                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black
                                )
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            if (supervisorPin == "1234" || supervisorPin == "superbrix") {
                                selectedArea = pendingArea ?: initialArea
                                showPinDialog = false
                                pendingArea = null
                                supervisorPin = ""
                                android.widget.Toast.makeText(context, "Área cambiada exitosamente", android.widget.Toast.LENGTH_SHORT).show()
                            } else {
                                android.widget.Toast.makeText(context, "PIN Incorrecto", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Text("Autorizar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { 
                            showPinDialog = false
                            pendingArea = null
                            supervisorPin = ""
                        }) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            Text("3. Selecciona la Operación", fontWeight = FontWeight.Bold, color = SuperBrixNavyPrimary)
            DropdownSelector(options = operaciones, selected = selectedOperation) { selectedOperation = it }

            Text("4. Máquina (Si aplica)", fontWeight = FontWeight.Bold, color = SuperBrixNavyPrimary)
            DropdownSelector(options = maquinas, selected = selectedMachine) { selectedMachine = it }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onConfirmStartLabor(selectedOP, selectedMachine, selectedOperation, selectedArea) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("INICIAR OPERACIÓN", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(options: List<String>, selected: String, onSelection: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = SuperBrixNavyPrimary
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelection(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
