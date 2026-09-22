package com.superbrix.ia.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbrix.ia.ui.theme.IndustrialGrayBackground
import com.superbrix.ia.ui.theme.SuperBrixNavyPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var notificacionesEnabled by remember { mutableStateOf(true) }
    var vozAsistidaEnabled by remember { mutableStateOf(true) }
    var modoOscuroEnabled by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Configuración",
                        fontWeight = FontWeight.Bold,
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
            // Section 1: Preferencias
            Text(
                text = "PREFERENCIAS DE INTERFAZ",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Notificaciones de Planta",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuperBrixNavyPrimary
                            )
                            Text(
                                text = "Alertas de interrupción y novedades",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        Switch(
                            checked = notificacionesEnabled,
                            onCheckedChange = { notificacionesEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = SuperBrixNavyPrimary)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Reconocimiento de Voz Asistido",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuperBrixNavyPrimary
                            )
                            Text(
                                text = "Habilitar comandos de voz rápida",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        Switch(
                            checked = vozAsistidaEnabled,
                            onCheckedChange = { vozAsistidaEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = SuperBrixNavyPrimary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 2: Sincronización y Red
            Text(
                text = "SINCRONIZACIÓN Y RED",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Estado de Sincronización:",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "🟢 En Línea • Sincronización Lista",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Servidor de Producción:",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "FastAPI / SuperBrix IA Backend (Mock Local)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1E293B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Action: Reset Demo Data
            Button(
                onClick = {
                    Toast.makeText(context, "Datos de demostración restablecidos", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SuperBrixNavyPrimary,
                    contentColor = Color.White
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Restablecer datos de demostración",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
