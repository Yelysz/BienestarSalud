package com.example.bienestarsalud.ui.screens.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bienestarsalud.ui.theme.PrimaryGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    vm: GoalsViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            // CORRECCIÓN 1: Fondo dinámico
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // --- HEADER (Degradado se mantiene igual) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFF10B981), Color(0xFF3B82F6))
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(0.2f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Default.Flag, null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Mis Metas", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Define tus objetivos para mantenerte motivado.",
                    color = Color.White.copy(0.9f),
                    fontSize = 14.sp
                )
            }
        }

        // --- CONTENIDO ---
        if (vm.isLoading) {
            Box(Modifier.fillMaxSize().height(300.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryGreen)
            }
        } else {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                // 1. HIDRATACIÓN
                GoalCard(
                    title = "Hidratación",
                    icon = Icons.Default.WaterDrop,
                    color = Color(0xFF3B82F6),
                    valueDisplay = "${vm.waterGoal.toInt()} vasos"
                ) {
                    // CORRECCIÓN 2: Texto secundario dinámico
                    Text("Objetivo diario", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Slider(
                        value = vm.waterGoal,
                        onValueChange = { vm.waterGoal = it },
                        valueRange = 1f..15f,
                        steps = 13,
                        colors = SliderDefaults.colors(thumbColor = Color(0xFF3B82F6), activeTrackColor = Color(0xFF3B82F6))
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("1", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        Text("15", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }

                // 2. SUEÑO
                GoalCard(
                    title = "Sueño",
                    icon = Icons.Default.Bedtime,
                    color = Color(0xFF8B5CF6),
                    valueDisplay = String.format("%.1f h", vm.sleepGoal)
                ) {
                    Text("Horas por noche", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Slider(
                        value = vm.sleepGoal,
                        onValueChange = { vm.sleepGoal = it },
                        valueRange = 4f..12f,
                        steps = 15,
                        colors = SliderDefaults.colors(thumbColor = Color(0xFF8B5CF6), activeTrackColor = Color(0xFF8B5CF6))
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("4h", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        Text("12h", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }

                // 3. EJERCICIO SEMANAL
                GoalCard(
                    title = "Ejercicio Semanal",
                    icon = Icons.Default.CalendarToday,
                    color = Color(0xFF10B981),
                    valueDisplay = "${vm.workoutDaysGoal.toInt()} días"
                ) {
                    Text("Días activos por semana", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Slider(
                        value = vm.workoutDaysGoal,
                        onValueChange = { vm.workoutDaysGoal = it },
                        valueRange = 1f..7f,
                        steps = 5,
                        colors = SliderDefaults.colors(thumbColor = Color(0xFF10B981), activeTrackColor = Color(0xFF10B981))
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("1 día", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        Text("7 días", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }

                // 4. CALORÍAS
                GoalCard(
                    title = "Calorías Activas",
                    icon = Icons.Default.LocalFireDepartment,
                    color = Color(0xFFF97316),
                    valueDisplay = "${vm.caloriesGoal} kcal"
                ) {
                    OutlinedTextField(
                        value = vm.caloriesGoal,
                        onValueChange = { if (it.all { char -> char.isDigit() }) vm.caloriesGoal = it },
                        label = { Text("Meta diaria (Kcal)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        // CORRECCIÓN 3: Colores del input dinámicos
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFF97316),
                            focusedLabelColor = Color(0xFFF97316),
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    )
                }

                // BOTÓN GUARDAR
                Button(
                    onClick = { vm.saveGoals() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Guardar Metas", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                if (vm.saveMessage.isNotEmpty()) {
                    Text(
                        text = vm.saveMessage,
                        color = PrimaryGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                Spacer(Modifier.height(30.dp))
            }
        }
    }
}

// --- COMPONENTE DE TARJETA ---
@Composable
fun GoalCard(
    title: String,
    icon: ImageVector,
    color: Color,
    valueDisplay: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        // CORRECCIÓN 4: Color de tarjeta dinámico (Surface)
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(color.copy(0.1f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    // CORRECCIÓN 5: Título dinámico
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                }
                Text(valueDisplay, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = color)
            }

            Spacer(modifier = Modifier.height(16.dp))

            content()
        }
    }
}