package com.example.bienestarsalud.ui.screens.add_record

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bienestarsalud.ui.theme.PrimaryBlue
import com.example.bienestarsalud.ui.theme.PrimaryGreen


@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecordScreen(
    onBack: () -> Unit,
    vm: AddRecordViewModel = hiltViewModel() // Aquí importará la clase del otro archivo automáticamente
) {
    if (vm.saved) {
        LaunchedEffect(Unit) { onBack() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Día") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { p ->
        Column(
            modifier = Modifier
                .padding(p)
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. AGUA
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.WaterDrop, null, tint = PrimaryBlue)
                        Spacer(Modifier.width(8.dp))
                        Text("Agua: ${vm.water} vasos", fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = vm.water.toFloat(),
                        onValueChange = { vm.water = it.toInt() },
                        valueRange = 0f..15f,
                        steps = 14
                    )
                }
            }

            // 2. SUEÑO
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6))) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bedtime, null, tint = Color(0xFF7E57C2))
                        Spacer(Modifier.width(8.dp))
                        Text("Sueño: ${String.format("%.1f", vm.sleep)} horas", fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = vm.sleep,
                        onValueChange = { vm.sleep = it },
                        valueRange = 0f..12f
                    )
                }
            }

            // 3. ÁNIMO
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4))) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SentimentSatisfied, null, tint = Color(0xFFFFC107))
                        Spacer(Modifier.width(8.dp))
                        Text("Ánimo (1-5): ${vm.mood}", fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = vm.mood.toFloat(),
                        onValueChange = { vm.mood = it.toInt() },
                        valueRange = 1f..5f,
                        steps = 3
                    )
                }
            }

            // 4. NOTA
            OutlinedTextField(
                value = vm.note,
                onValueChange = { vm.note = it },
                label = { Text("Nota del día (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                shape = MaterialTheme.shapes.medium
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { vm.save() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Text("Guardar Progreso")
            }
        }
    }
}