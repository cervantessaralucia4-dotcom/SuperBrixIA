package com.superbrix.ia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbrix.ia.ui.components.PrimaryButton
import com.superbrix.ia.ui.theme.IndustrialGrayBackground
import com.superbrix.ia.ui.theme.IndustrialGreenActive
import com.superbrix.ia.ui.theme.SuperBrixNavyPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeleccionOperacionScreen(
    initialOrder: String = "OP-001",
    initialMachine: String = "Fresadora 01",
    initialOperation: String = "Producción",
    onConfirmStartLabor: (orderId: String, machine: String, operationType: String) -> Unit,
    onBackClick: () -> Unit
) {
    val ordersList = listOf("OP-001", "OP-002", "OP-003")
    val machinesList = listOf("Fresadora 01", "Torno 01", "Soldadora 02", "Cortadora 01")
    val operationTypesList = listOf("Producción", "Setup", "Mantenimiento", "Calidad")

    var selectedOrder by remember { mutableStateOf(initialOrder) }
    var selectedMachine by remember { mutableStateOf(initialMachine) }
    var selectedOperation by remember { mutableStateOf(initialOperation) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Seleccionar operación",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SuperBrixNavyPrimary)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(IndustrialGrayBackground)
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Section 1: Orden de Producción
            Text(
                text = "1. ORDEN DE PRODUCCIÓN",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    ordersList.forEach { order ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedOrder = order }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selectedOrder == order),
                                onClick = { selectedOrder = order },
                                colors = RadioButtonDefaults.colors(selectedColor = SuperBrixNavyPrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = order,
                                fontSize = 16.sp,
                                fontWeight = if (selectedOrder == order) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 2: Máquina
            Text(
                text = "2. MÁQUINA",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    machinesList.forEach { machine ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedMachine = machine }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selectedMachine == machine),
                                onClick = { selectedMachine = machine },
                                colors = RadioButtonDefaults.colors(selectedColor = SuperBrixNavyPrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = machine,
                                fontSize = 16.sp,
                                fontWeight = if (selectedMachine == machine) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 3: Tipo de Operación
            Text(
                text = "3. TIPO DE OPERACIÓN",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    operationTypesList.forEach { opType ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedOperation = opType }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selectedOperation == opType),
                                onClick = { selectedOperation = opType },
                                colors = RadioButtonDefaults.colors(selectedColor = SuperBrixNavyPrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = opType,
                                fontSize = 16.sp,
                                fontWeight = if (selectedOperation == opType) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Main Big Button: INICIAR LABOR
            PrimaryButton(
                text = "▶ INICIAR LABOR",
                onClick = {
                    onConfirmStartLabor(selectedOrder, selectedMachine, selectedOperation)
                },
                icon = Icons.Default.PlayArrow,
                containerColor = IndustrialGreenActive
            )
        }
    }
}
