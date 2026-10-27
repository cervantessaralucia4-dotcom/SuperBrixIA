package com.superbrix.ia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbrix.ia.core.config.AppConfig
import com.superbrix.ia.data.model.Evento
import com.superbrix.ia.data.model.Operario
import com.superbrix.ia.ui.components.ConfirmDialog
import com.superbrix.ia.ui.components.EmptyStateView
import com.superbrix.ia.ui.theme.*

/**
 * Panel de Administración Industrial para BRIX360.
 *
 * Características:
 * - CRUD completo de operarios con validación rigurosa de código único.
 * - Filtro por área de trabajo y buscador en tiempo real.
 * - Diálogo de confirmación para eliminar operario.
 * - Enlaces directos a Google Sheets y Looker Studio desde AppConfig.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    operarios: List<Operario>,
    eventos: List<Evento> = emptyList(),
    onAddOperario: (nombre: String, codigo: String, area: String) -> Unit,
    onEditOperario: (id: String, nombre: String, codigo: String, area: String) -> Unit,
    onDeleteOperario: (id: String) -> Unit,
    onBackClick: () -> Unit
) {
    val uriHandler = LocalUriHandler.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedAreaFilter by remember { mutableStateOf("TODAS") }

    var showFormDialog by remember { mutableStateOf(false) }
    var operarioToEdit by remember { mutableStateOf<Operario?>(null) }

    var operarioToDelete by remember { mutableStateOf<Operario?>(null) }

    val areasList = listOf("TODAS", "CNC", "Ensamble 1", "Ensamble 2", "Pintura")

    val filteredOperarios = remember(operarios, searchQuery, selectedAreaFilter) {
        operarios.filter { op ->
            val matchesArea = (selectedAreaFilter == "TODAS" || op.area.equals(selectedAreaFilter, ignoreCase = true))
            val matchesSearch = op.nombre.contains(searchQuery, ignoreCase = true) ||
                                op.codigo.contains(searchQuery, ignoreCase = true)
            matchesArea && matchesSearch
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Panel de Administración",
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    operarioToEdit = null
                    showFormDialog = true
                },
                containerColor = BrixVioletPrimary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Registrar Operario")
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
                    .widthIn(max = 640.dp)
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                // Accesos Rápidos a Base de Datos y Dashboard
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { uriHandler.openUri(AppConfig.SHEETS_URL) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F9D58)),
                        shape = ButtonShape
                    ) {
                        Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Base de Datos", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { uriHandler.openUri(AppConfig.LOOKER_STUDIO_URL) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4)),
                        shape = ButtonShape
                    ) {
                        Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Dashboard OEE", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Campo de Búsqueda
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar operario por nombre o código...", color = BrixTextDisabled) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BrixCyanAccent) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = InputShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = BrixTextPrimary,
                        unfocusedTextColor = BrixTextPrimary,
                        focusedBorderColor = BrixCyanAccent,
                        unfocusedBorderColor = BrixDarkBorder,
                        focusedContainerColor = BrixDarkSurface,
                        unfocusedContainerColor = BrixDarkSurface
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Chips de Filtro por Área
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    areasList.forEach { areaItem ->
                        val isSelected = selectedAreaFilter == areaItem
                        Surface(
                            modifier = Modifier.clickable { selectedAreaFilter = areaItem },
                            shape = ChipShape,
                            color = if (isSelected) BrixVioletPrimary else BrixDarkSurface,
                            border = BorderStroke(1.dp, if (isSelected) BrixCyanAccent else BrixDarkBorder)
                        ) {
                            Text(
                                text = areaItem,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else BrixTextSecondary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "OPERARIOS REGISTRADOS (${filteredOperarios.size})",
                    style = Typography.labelSmall,
                    color = BrixTextSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Lista de Operarios
                if (filteredOperarios.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        EmptyStateView(
                            title = "No se encontraron operarios",
                            message = "Intente con otro término de búsqueda o filtre por otra área."
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredOperarios, key = { it.id }) { op ->
                            val opInitials = op.nombre.split(" ").take(2).mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("").ifBlank { "OP" }
                            val totalEventos = eventos.count { it.operario.contains(op.nombre, ignoreCase = true) }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = CardShape,
                                colors = CardDefaults.cardColors(containerColor = BrixDarkSurface),
                                border = BorderStroke(1.dp, BrixDarkBorder)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(BrixVioletPrimary.copy(alpha = 0.25f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = opInitials,
                                            style = Typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = BrixCyanAccent
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = op.nombre,
                                            style = Typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = BrixTextPrimary
                                        )
                                        Text(
                                            text = "Código: ${op.codigo} • ${op.area}",
                                            style = Typography.bodySmall,
                                            color = BrixTextSecondary
                                        )
                                        Text(
                                            text = "Actividad turno: $totalEventos eventos registrados",
                                            style = Typography.labelSmall,
                                            color = BrixCyanAccent
                                        )
                                    }

                                    // Botón Editar
                                    IconButton(onClick = {
                                        operarioToEdit = op
                                        showFormDialog = true
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Editar",
                                            tint = BrixCyanAccent
                                        )
                                    }

                                    // Botón Eliminar (con diálogo de confirmación)
                                    IconButton(onClick = {
                                        operarioToDelete = op
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Eliminar",
                                            tint = BrixStateCritical
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo Validado de Crear / Editar Operario
    if (showFormDialog) {
        AdminOperarioFormDialog(
            existingOperarios = operarios,
            operarioToEdit = operarioToEdit,
            onDismiss = { showFormDialog = false },
            onConfirm = { id, nombre, codigo, area ->
                if (id == null) {
                    onAddOperario(nombre, codigo, area)
                } else {
                    onEditOperario(id, nombre, codigo, area)
                }
                showFormDialog = false
            }
        )
    }

    // Diálogo de Confirmación para Eliminar Operario
    if (operarioToDelete != null) {
        val op = operarioToDelete!!
        ConfirmDialog(
            title = "¿Eliminar operario?",
            message = "¿Está seguro de eliminar a '${op.nombre}' (${op.codigo}) del registro de planta? Esta acción es irreversible.",
            confirmText = "Sí, Eliminar",
            dismissText = "Cancelar",
            icon = Icons.Default.Delete,
            confirmColor = BrixStateCritical,
            onConfirm = {
                onDeleteOperario(op.id)
                operarioToDelete = null
            },
            onDismiss = { operarioToDelete = null }
        )
    }
}

/**
 * Diálogo validado para registrar o editar operarios.
 * Valida obligatoriedad de campos y unicidad de código de operario.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminOperarioFormDialog(
    existingOperarios: List<Operario>,
    operarioToEdit: Operario?,
    onDismiss: () -> Unit,
    onConfirm: (id: String?, nombre: String, codigo: String, area: String) -> Unit
) {
    var nombre by remember { mutableStateOf(operarioToEdit?.nombre ?: "") }
    var codigo by remember { mutableStateOf(operarioToEdit?.codigo ?: "") }
    var area by remember { mutableStateOf(operarioToEdit?.area ?: "CNC") }

    var nombreError by remember { mutableStateOf<String?>(null) }
    var codigoError by remember { mutableStateOf<String?>(null) }

    val areasDisponibles = listOf("CNC", "Ensamble 1", "Ensamble 2", "Pintura")
    var areaMenuExpanded by remember { mutableStateOf(false) }

    val isEditing = operarioToEdit != null

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = CardShape,
        containerColor = BrixDarkSurface,
        title = {
            Text(
                text = if (isEditing) "Editar Operario" else "Registrar Nuevo Operario",
                style = Typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = BrixTextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Campo Nombre
                OutlinedTextField(
                    value = nombre,
                    onValueChange = {
                        nombre = it
                        nombreError = null
                    },
                    label = { Text("Nombre Completo *", color = BrixTextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = InputShape,
                    isError = nombreError != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = BrixTextPrimary,
                        unfocusedTextColor = BrixTextPrimary,
                        focusedBorderColor = BrixCyanAccent,
                        unfocusedBorderColor = BrixDarkBorder,
                        focusedContainerColor = BrixDarkSurfaceVariant,
                        unfocusedContainerColor = BrixDarkSurfaceVariant
                    )
                )
                if (nombreError != null) {
                    Text(text = nombreError ?: "", style = Typography.bodySmall, color = BrixStateCritical)
                }

                // Campo Código
                OutlinedTextField(
                    value = codigo,
                    onValueChange = {
                        codigo = it.trim().uppercase()
                        codigoError = null
                    },
                    label = { Text("Código de Operario (ej. COD-102) *", color = BrixTextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = InputShape,
                    isError = codigoError != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = BrixTextPrimary,
                        unfocusedTextColor = BrixTextPrimary,
                        focusedBorderColor = BrixCyanAccent,
                        unfocusedBorderColor = BrixDarkBorder,
                        focusedContainerColor = BrixDarkSurfaceVariant,
                        unfocusedContainerColor = BrixDarkSurfaceVariant
                    )
                )
                if (codigoError != null) {
                    Text(text = codigoError ?: "", style = Typography.bodySmall, color = BrixStateCritical)
                }

                // Selector de Área
                ExposedDropdownMenuBox(
                    expanded = areaMenuExpanded,
                    onExpandedChange = { areaMenuExpanded = !areaMenuExpanded }
                ) {
                    OutlinedTextField(
                        value = area,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Área de Manufactura", color = BrixTextSecondary) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = areaMenuExpanded) },
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
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
                    ExposedDropdownMenu(
                        expanded = areaMenuExpanded,
                        onDismissRequest = { areaMenuExpanded = false }
                    ) {
                        areasDisponibles.forEach { a ->
                            DropdownMenuItem(
                                text = { Text(a) },
                                onClick = {
                                    area = a
                                    areaMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    var hasError = false
                    if (nombre.trim().isBlank()) {
                        nombreError = "El nombre es obligatorio"
                        hasError = true
                    }
                    if (codigo.trim().isBlank()) {
                        codigoError = "El código de operario es obligatorio"
                        hasError = true
                    } else {
                        // Validación de código único
                        val duplicate = existingOperarios.any {
                            it.codigo.equals(codigo.trim(), ignoreCase = true) &&
                            it.id != operarioToEdit?.id
                        }
                        if (duplicate) {
                            codigoError = "El código '$codigo' ya está registrado por otro operario"
                            hasError = true
                        }
                    }

                    if (!hasError) {
                        onConfirm(operarioToEdit?.id, nombre.trim(), codigo.trim(), area)
                    }
                },
                shape = ButtonShape,
                colors = ButtonDefaults.buttonColors(containerColor = BrixVioletPrimary)
            ) {
                Text(if (isEditing) "Guardar Cambios" else "Registrar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = BrixTextSecondary)
            }
        }
    )
}
