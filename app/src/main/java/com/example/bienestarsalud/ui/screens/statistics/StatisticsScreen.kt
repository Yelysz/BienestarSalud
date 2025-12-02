package com.example.bienestarsalud.ui.screens.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bienestarsalud.ui.theme.PrimaryGreen

@Composable
fun StatisticsScreen(
    vm: StatisticsViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsState()
    var showEditGoals by remember { mutableStateOf(false) }

    // Refrescar al entrar
    LaunchedEffect(Unit) { vm.refresh() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .verticalScroll(rememberScrollState())
    ) {
        // ... (Header existente se mantiene igual) ...
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(Color(0xFF22C55E), Color(0xFF3B82F6))))
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Estadísticas", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.BarChart, null, tint = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp))
                TimeFilterChip("Resumen General", true)
            }
        }

        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {

            // 1. TARJETA DE PROGRESO SEMANAL (DINÁMICA)
            WeeklyProgressCard(
                currentCalories = state.totalCaloriesToday,
                goalCalories = state.userGoals.dailyCaloriesGoal,
                activeDays = state.activeDaysCount,
                goalDays = state.userGoals.weeklyWorkoutDaysGoal,
                daysStatus = state.last7DaysStatus,
                onEditClick = { showEditGoals = true }
            )

            // 2. Actividad de Hoy
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Actividad de Hoy", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Icon(Icons.Default.FitnessCenter, null, tint = Color(0xFFF97316))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
                        StatBox("${state.totalCaloriesToday}", "Kcal", Color(0xFFF97316))
                        StatBox("${state.totalMinutesToday}", "Minutos", Color(0xFF3B82F6))
                    }
                }
            }

            // 3. Promedios y Gráfica (Resto del código existente...)
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Promedios (7 días)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        CircularProgressItem((state.avgWater / 8.0).toFloat(), Color(0xFF3B82F6), "Agua", String.format("%.1f", state.avgWater))
                        CircularProgressItem((state.avgSleep / 8.0).toFloat(), Color(0xFF8B5CF6), "Sueño", String.format("%.1f h", state.avgSleep))
                        CircularProgressItem((state.avgMood / 5.0).toFloat(), Color(0xFFF59E0B), "Ánimo", String.format("%.1f", state.avgMood))
                    }
                }
            }
        }
    }

    // DIÁLOGO PARA EDITAR METAS
    if (showEditGoals) {
        EditGoalsDialog(
            currentCals = state.userGoals.dailyCaloriesGoal,
            currentDays = state.userGoals.weeklyWorkoutDaysGoal,
            onDismiss = { showEditGoals = false },
            onConfirm = { cals, days ->
                vm.updateUserGoals(cals, days)
                showEditGoals = false
            }
        )
    }
}

@Composable
fun StatBox(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = color)
        Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun CircularProgressItem(progress: Float, color: Color, label: String, textValue: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(70.dp)) {
            Canvas(modifier = Modifier.size(70.dp)) {
                // Fondo gris
                drawArc(
                    color = Color(0xFFE5E7EB),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 6.dp.toPx())
                )
                // Progreso de color
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = progress.coerceIn(0f, 1f) * 360f,
                    useCenter = false,
                    style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            Text(textValue, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = color)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}
@Composable
fun TimeFilterChip(text: String, selected: Boolean) {
    Box(
        modifier = Modifier
            .background(
                if (selected) Color.White.copy(alpha = 0.3f)
                else Color.White.copy(alpha = 0.1f),
                CircleShape
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// --- COMPONENTE DE PROGRESO SEMANAL ---
@Composable
fun WeeklyProgressCard(
    currentCalories: Int,
    goalCalories: Int,
    activeDays: Int,
    goalDays: Int,
    daysStatus: List<Pair<String, Boolean>>,
    onEditClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Progreso Semanal", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1F2937))
                // Botón de Editar Metas
                IconButton(onClick = onEditClick, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Edit, null, tint = PrimaryGreen)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Meta de Calorías (Barra)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Meta de Calorías (Hoy)", color = Color.Gray, fontSize = 14.sp)
                Text("$currentCalories / $goalCalories kcal", fontWeight = FontWeight.Bold, color = Color(0xFFF97316))
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = (currentCalories.toFloat() / goalCalories.toFloat()).coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = Color(0xFFEC4899),
                trackColor = Color(0xFFFEE2E2)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Días Activos (Cuadros Verdes)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Días Activos (7 días)", color = Color.Gray, fontSize = 14.sp)
                Text("$activeDays / $goalDays días", fontWeight = FontWeight.Bold, color = PrimaryGreen)
            }
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Dibujamos los cuadros basados en el historial real
                if (daysStatus.isEmpty()) {
                    Text("Cargando...", fontSize = 12.sp, color = Color.Gray)
                } else {
                    daysStatus.forEach { (dayName, isActive) ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    if (isActive) PrimaryGreen else Color(0xFFF3F4F6),
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                dayName,
                                color = if (isActive) Color.White else Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- DIÁLOGO DE EDICIÓN ---
@Composable
fun EditGoalsDialog(
    currentCals: Int,
    currentDays: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var cals by remember { mutableStateOf(currentCals.toString()) }
    var days by remember { mutableStateOf(currentDays.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configurar Metas") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = cals,
                    onValueChange = { cals = it },
                    label = { Text("Calorías Diarias") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = days,
                    onValueChange = { days = it },
                    label = { Text("Días de Ejercicio/Semana") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(cals.toIntOrNull() ?: 2000, days.toIntOrNull() ?: 5)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
