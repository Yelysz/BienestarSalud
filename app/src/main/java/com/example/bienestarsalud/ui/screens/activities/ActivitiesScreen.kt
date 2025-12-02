package com.example.bienestarsalud.ui.screens.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.bienestarsalud.domain.model.activity.ActivityLog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesScreen(
    vm: ActivitiesViewModel = hiltViewModel()
) {
    val activities by vm.todayActivities.collectAsState()
    val totalCalories by vm.totalCalories.collectAsState()
    val totalMinutes by vm.totalMinutes.collectAsState()

    var showCustomDialog by remember { mutableStateOf(false) }
    var activityToDelete by remember { mutableStateOf<ActivityLog?>(null) } // Estado para borrar

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCustomDialog = true },
                containerColor = Color(0xFFF97316),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF9FAFB))
        ) {
            // HEADER DEGRADADO
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFF97316), Color(0xFFEC4899))
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Actividades", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(0.2f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Icon(Icons.Default.FitnessCenter, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Stats del Header
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        HeaderStatCard(totalCalories.toString(), "kcal", Icons.Default.LocalFireDepartment, Modifier.weight(1f))
                        HeaderStatCard(totalMinutes.toString(), "min", Icons.Default.Timer, Modifier.weight(1f))
                        HeaderStatCard(activities.size.toString(), "act.", Icons.Default.DirectionsRun, Modifier.weight(1f))
                    }
                }
            }

            // LISTA DE ACTIVIDADES
            LazyColumn(
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Sección Rápida
                item {
                    Text("Acciones Rápidas", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1F2937))
                    Spacer(modifier = Modifier.height(12.dp))
                    // Fila 1
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        QuickActionCard("Caminar", "30 min", Icons.Default.DirectionsWalk, Color(0xFF10B981), Color(0xFF34D399), Modifier.weight(1f)) {
                            vm.addActivity("Caminar", 30, 120, "run")
                        }
                        QuickActionCard("Gym", "1 h", Icons.Default.FitnessCenter, Color(0xFFF97316), Color(0xFFFB923C), Modifier.weight(1f)) {
                            vm.addActivity("Gimnasio", 60, 300, "gym")
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    // Fila 2
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        QuickActionCard("Correr", "30 min", Icons.Default.DirectionsRun, Color(0xFF3B82F6), Color(0xFF60A5FA), Modifier.weight(1f)) {
                            vm.addActivity("Correr", 30, 300, "run")
                        }
                        QuickActionCard("Bicicleta", "45 min", Icons.Default.DirectionsBike, Color(0xFF8B5CF6), Color(0xFFA78BFA), Modifier.weight(1f)) {
                            vm.addActivity("Bicicleta", 45, 250, "bike")
                        }
                    }
                }

                // Lista de Hoy
                item {
                    Text("Historial de Hoy", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1F2937))
                }

                if (activities.isEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                            Text("No hay actividades hoy.", color = Color.Gray)
                        }
                    }
                } else {
                    items(activities) { activity ->
                        ActivityRow(
                            activity = activity,
                            onDelete = { activityToDelete = activity } // Activar diálogo de borrado
                        )
                    }
                }

                item { Spacer(Modifier.height(60.dp)) }
            }
        }
    }

    // --- DIÁLOGOS ---

    // 1. Agregar
    if (showCustomDialog) {
        CustomActivityDialog(
            onDismiss = { showCustomDialog = false },
            onConfirm = { name, mins, cals, icon -> // Se añade el parámetro 'icon'
                vm.addActivity(name, mins, cals, icon) // Se pasa el icono al ViewModel
                showCustomDialog = false
            }
        )
    }

    // 2. Borrar
    if (activityToDelete != null) {
        AlertDialog(
            onDismissRequest = { activityToDelete = null },
            title = { Text("¿Borrar actividad?") },
            text = { Text("Se eliminará '${activityToDelete?.name}' de tu historial.") },
            confirmButton = {
                Button(
                    onClick = {
                        activityToDelete?.let { vm.deleteActivity(it) }
                        activityToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { activityToDelete = null }) { Text("Cancelar") }
            }
        )
    }
}

// --- COMPONENTES ---

@Composable
fun HeaderStatCard(value: String, label: String, icon: ImageVector, modifier: Modifier) {
    Column(
        modifier = modifier
            .background(Color.White.copy(0.2f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, null, tint = Color.White, modifier = Modifier.size(24.dp))
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(label, fontSize = 12.sp, color = Color.White.copy(0.9f))
    }
}

@Composable
fun QuickActionCard(title: String, subtitle: String, icon: ImageVector, c1: Color, c2: Color, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(100.dp).clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(c1, c2))).padding(16.dp)) {
            Column {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(28.dp))
                Spacer(Modifier.weight(1f))
                Text(title, fontWeight = FontWeight.Bold, color = Color.White)
                Text(subtitle, fontSize = 12.sp, color = Color.White.copy(0.9f))
            }
        }
    }
}

@Composable
fun ActivityRow(activity: ActivityLog, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).background(Color(0xFFFFF7ED), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when(activity.iconName) {
                        "run" -> Icons.Default.DirectionsRun
                        "gym" -> Icons.Default.FitnessCenter
                        "bike" -> Icons.Default.DirectionsBike
                        else -> Icons.Default.Bolt
                    },
                    contentDescription = null, tint = Color(0xFFF97316)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(activity.name, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                Text("${activity.calories} kcal • ${activity.durationMinutes} min", fontSize = 12.sp, color = Color.Gray)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, null, tint = Color(0xFFEF4444))
            }
        }
    }
}

@Composable
fun CustomActivityDialog(onDismiss: () -> Unit, onConfirm: (String, Int, Int, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var mins by remember { mutableStateOf("") }
    var cals by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("run") }

    // Lista de iconos disponibles para elegir
    val iconOptions = listOf(
        "run" to Icons.Default.DirectionsRun,
        "gym" to Icons.Default.FitnessCenter,
        "bike" to Icons.Default.DirectionsBike,
        "walk" to Icons.Default.DirectionsWalk,
        "swim" to Icons.Default.Pool,
        "yoga" to Icons.Default.SelfImprovement
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Actividad") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre (ej: Yoga)") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = mins, onValueChange = { mins = it }, label = { Text("Min") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = cals, onValueChange = { cals = it }, label = { Text("Kcal") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }

                // Selección de Icono
                Text("Elige un icono:", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
                    iconOptions.forEach { (iconName, iconVector) ->
                        Icon(
                            imageVector = iconVector,
                            contentDescription = null,
                            tint = if (selectedIcon == iconName) Color(0xFFF97316) else Color.Gray,
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { selectedIcon = iconName }
                                .background(
                                    if (selectedIcon == iconName) Color(0xFFF97316).copy(alpha = 0.2f) else Color.Transparent,
                                    CircleShape
                                )
                                .padding(4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if(name.isNotEmpty()) onConfirm(name, mins.toIntOrNull()?:0, cals.toIntOrNull()?:0, selectedIcon) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316))
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}