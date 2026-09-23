package com.superbrix.ia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbrix.ia.data.model.Operario
import com.superbrix.ia.data.model.Evento
import com.superbrix.ia.ui.theme.IndustrialGrayBackground
import com.superbrix.ia.ui.theme.IndustrialRedAlert
import com.superbrix.ia.ui.theme.SuperBrixNavyPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    operarios: List<Operario>,
    eventos: List<Evento> = emptyList(),
    onAddOperario: (String, String, String) -> Unit,
    onEditOperario: (String, String, String, String) -> Unit,
    onDeleteOperario: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var operarioToEdit by remember { mutableStateOf<Operario?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administración", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SuperBrixNavyPrimary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    operarioToEdit = null
                    showDialog = true
                },
                containerColor = SuperBrixNavyPrimary
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Añadir", tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(IndustrialGrayBackground)
                .padding(padding)
                .padding(16.dp)
        ) {
            val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { uriHandler.openUri("https://docs.google.com/spreadsheets/d/1okZRQ37KSMZ497iIltWPRjC7SrxYiLPc5iT28I3QXEo/edit?usp=sharing") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F9D58)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Ver Base de Datos", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { uriHandler.openUri("https://datastudio.google.com/reporting/7917f5be-a89a-40a1-87a4-aef50f1b42f8") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Ver Dashboard", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Operarios Registrados",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SuperBrixNavyPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(operarios) { op ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = op.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SuperBrixNavyPrimary)
                                Text(text = "Código: ${op.codigo} | Área: ${op.area}", color = Color.Gray, fontSize = 14.sp)
                                
                                val totalEventos = eventos.count { it.operario == op.nombre }
                                val parosList = eventos.filter { it.operario == op.nombre && (it.duracionMinutos > 0 || it.duracionSegundos > 0) && it.categoria != "Producción Activa" }
                                val pMin = parosList.sumOf { it.duracionMinutos }
                                val pSeg = parosList.sumOf { it.duracionSegundos }
                                val totalParosMin = pMin + (pSeg / 60)
                                val totalParosSeg = pSeg % 60
                                Text(
                                    text = "Actividad Hoy: $totalEventos eventos | Paros: ${totalParosMin}m ${totalParosSeg}s",
                                    color = Color(0xFFE67E22), // Naranja para resaltar
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            IconButton(onClick = {
                                operarioToEdit = op
                                showDialog = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF0288D1))
                            }
                            IconButton(onClick = { onDeleteOperario(op.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = IndustrialRedAlert)
                            }
                        }
                    }
                }
            }
        }

        if (showDialog) {
            OperarioDialog(
                operarios = operarios,
                operarioToEdit = operarioToEdit,
                onDismiss = { showDialog = false },
                onConfirm = { id, nombre, codigo, area ->
                    if (id == null) {
                        onAddOperario(nombre, codigo, area)
                    } else {
                        onEditOperario(id, nombre, codigo, area)
                    }
                    showDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperarioDialog(
    operarios: List<Operario>,
    operarioToEdit: Operario?,
    onDismiss: () -> Unit,
    onConfirm: (String?, String, String, String) -> Unit
) {
    var nombre by remember { mutableStateOf(operarioToEdit?.nombre ?: "") }
    var codigo by remember { mutableStateOf(operarioToEdit?.codigo ?: "") }
    var area by remember { mutableStateOf(operarioToEdit?.area ?: "CNC") }
    val areas = listOf("CNC", "Ensamble 1", "Ensamble 2", "Pintura")
    var expanded by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

    val isEditing = operarioToEdit != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Editar Operario" else "Registrar Operario") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") },
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
                )
                OutlinedTextField(
                    value = codigo, onValueChange = { codigo = it }, label = { Text("Código") },
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = area,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.menuAnchor(),
                        label = { Text("Área") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        areas.forEach { a ->
                            DropdownMenuItem(text = { Text(a) }, onClick = { area = a; expanded = false })
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (nombre.isNotBlank() && codigo.isNotBlank()) {
                    val codeExists = operarios.any { it.codigo.equals(codigo, ignoreCase = true) && it.id != operarioToEdit?.id }
                    if (codeExists) {
                        android.widget.Toast.makeText(context, "Ese código ya está registrado por otro operario", android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        onConfirm(operarioToEdit?.id, nombre, codigo, area)
                    }
                }
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
