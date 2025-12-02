package com.example.bienestarsalud.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.bienestarsalud.ui.navigation.Screen
import com.example.bienestarsalud.ui.theme.*

@Composable
fun HomeScreen(
    vm: HomeViewModel = hiltViewModel(),
    onNavigateToAddRecord: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToScreen: (String) -> Unit
) {
    val state by vm.uiState.collectAsState()
    var showStreakInfo by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { vm.loadData() }

    Scaffold(
        topBar = {
            // El Header degradado se ve bien en ambos modos, lo dejamos igual
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(colors = listOf(PrimaryGreen, PrimaryBlue)))
                    .padding(24.dp, top = 24.dp, bottom = 24.dp, end = 24.dp)
            ) {
                Column {
                    Text(text = vm.greeting, fontSize = 14.sp, color = Color.White.copy(0.9f))
                    Text(text = vm.userName, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = vm.currentDate, fontSize = 14.sp, color = Color.White.copy(0.9f), fontWeight = FontWeight.Medium)
                }
                Row(modifier = Modifier.align(Alignment.TopEnd), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (state.streak > 0) {
                        Box(modifier = Modifier.background(Color.White.copy(0.2f), RoundedCornerShape(16.dp)).clickable { showStreakInfo = true }.padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocalFireDepartment, null, tint = Color(0xFFFFC107), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${state.streak}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                    Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.White.copy(0.3f)).clickable { onNavigateToSettings() }, contentAlignment = Alignment.Center) {
                        if (state.photoUrl != null) {
                            AsyncImage(model = state.photoUrl, contentDescription = "Perfil", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        } else {
                            Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                // CORRECCIÓN 1: Fondo dinámico
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // CONSEJO (Usamos Surface para que adapte el color)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // O un color suave específico
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lightbulb, null, tint = Color(0xFFF97316))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Consejo del día", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF97316))
                        // CORRECCIÓN 2: Texto dinámico
                        Text(text = state.dailyTip, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 18.sp)
                    }
                }
            }

            StreakCardDisplay(state.streak) { showStreakInfo = true }

            // TARJETA RESUMEN
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Resumen de Hoy", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        TextButton(onClick = { onNavigateToScreen(Screen.History.route) }) { Text("Ver Historial") }
                    }

                    // Métricas (Se mantienen con colores fijos porque son marcas visuales: Azul, Morado, Amarillo)
                    ProgressMetric("💧", "Hidratación", state.record.waterGlasses.toFloat(), state.goals.waterGoal.toFloat(), "vasos", PrimaryBlue, CardBlue)
                    ProgressMetric("😴", "Sueño", state.record.sleepHours.toFloat(), state.goals.sleepGoal.toFloat(), "h", Color(0xFF7E57C2), CardIndigo)
                    HealthMetricSimple("😊", "Estado de Ánimo", "${state.record.mood} / 5", CardYellow, PrimaryYellow)

                    if (state.record.note.isNotEmpty()) {
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background), shape = RoundedCornerShape(8.dp)) {
                            Text(
                                text = "\"${state.record.note}\"",
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                fontSize = 14.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Button(onClick = onNavigateToAddRecord, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen), shape = RoundedCornerShape(12.dp)) {
                        Text("Registrar Datos de Hoy")
                    }
                }
            }

            Text("Explorar", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

            // Accesos rápidos (Adaptados)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickAccessCard("Médico", Icons.Default.MedicalServices, Color(0xFFEF4444), { onNavigateToScreen(Screen.Medical.route) }, Modifier.weight(1f))
                QuickAccessCard("Actividad", Icons.Default.DirectionsRun, Color(0xFFF97316), { onNavigateToScreen(Screen.Activities.route) }, Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickAccessCard("Metas", Icons.Default.Flag, Color(0xFF22C55E), { onNavigateToScreen(Screen.Goals.route) }, Modifier.weight(1f))
                QuickAccessCard("Alertas", Icons.Default.NotificationsActive, Color(0xFF8B5CF6), { onNavigateToScreen(Screen.Reminders.route) }, Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // El diálogo de Info se adapta solo si no le pones color de fondo fijo
    if (showStreakInfo) {
        AlertDialog(
            onDismissRequest = { showStreakInfo = false },
            icon = { Icon(Icons.Default.LocalFireDepartment, null, tint = Color(0xFFFFC107)) },
            title = { Text("¿Cómo funciona la Racha?") },
            text = { Text("1. Tu racha aumenta en +1 cada día que registras.\n2. Si registras hoy, mantienes el fuego.\n3. Si olvidas un día, vuelve a 0.") },
            confirmButton = { TextButton(onClick = { showStreakInfo = false }) { Text("Entendido") } },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

// COMPONENTES ADAPTADOS
@Composable
fun StreakCardDisplay(streak: Int, onClickInfo: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(48.dp).background(Color(0xFFFFF7ED), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.LocalFireDepartment, null, tint = Color(0xFFF97316), modifier = Modifier.size(28.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("$streak Días en Racha", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text(if(streak > 0) "¡Sigue así!" else "¡Empieza hoy!", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
            IconButton(onClick = onClickInfo) { Icon(Icons.Default.Info, null, tint = PrimaryBlue) }
        }
    }
}

@Composable
fun QuickAccessCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit, modifier: Modifier) {
    Card(modifier = modifier.height(90.dp).clickable { onClick() }, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(modifier = Modifier.size(36.dp).background(color.copy(0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

// ProgressMetric y HealthMetricSimple se pueden quedar igual si usas colores fijos para las tarjetas de colores
@Composable
fun ProgressMetric(icon: String, title: String, current: Float, goal: Float, unit: String, color: Color, bgColor: Color) {
    val progress = if(goal > 0) (current / goal).coerceIn(0f, 1f) else 0f
    Row(modifier = Modifier.fillMaxWidth().background(bgColor, RoundedCornerShape(16.dp)).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = icon, fontSize = 24.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                // Forzamos texto oscuro dentro de tarjetas de color claro (como la azul claro)
                Text(title, fontSize = 12.sp, color = Color(0xFF1F2937).copy(alpha = 0.7f), fontWeight = FontWeight.SemiBold)
                Text(text = "${current.toInt()} / ${goal.toInt()} $unit", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)), color = color, trackColor = Color.White.copy(alpha = 0.5f))
        }
    }
}

@Composable
fun HealthMetricSimple(icon: String, title: String, value: String, backgroundColor: Color, iconColor: Color) {
    Row(modifier = Modifier.fillMaxWidth().background(backgroundColor, RoundedCornerShape(16.dp)).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 24.sp)
            Column {
                Text(text = title, fontSize = 12.sp, color = Color(0xFF1F2937).copy(alpha = 0.7f))
                Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
            }
        }
    }
}