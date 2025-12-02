package com.example.bienestarsalud.ui.screens.reminders

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bienestarsalud.domain.model.reminder.Reminder
import com.example.bienestarsalud.ui.theme.PrimaryBlue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    onBackClick: () -> Unit = {},
    vm: RemindersViewModel = hiltViewModel()
) {
    val reminders by vm.reminders.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recordatorios", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, "Atrás") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PrimaryBlue,
                contentColor = Color.White
            ) { Icon(Icons.Default.Add, null) }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF9FAFB))
                .padding(24.dp)
        ) {
            if (reminders.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Agrega alarmas para tus hábitos", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(reminders) { reminder ->
                        ReminderItem(
                            reminder = reminder,
                            onToggle = { vm.toggleReminder(reminder) },
                            onDelete = { vm.deleteReminder(reminder.id) }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddReminderDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { title, time, days, interval ->
                    vm.addReminder(title, time, days, interval)
                    showAddDialog = false
                }
            )
        }
    }
}

// --- ITEM DE LISTA MEJORADO (Muestra días e intervalo) ---
@Composable
fun ReminderItem(reminder: Reminder, onToggle: () -> Unit, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).background(if (reminder.isEnabled) Color(0xFFEFF6FF) else Color(0xFFF3F4F6), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Notifications, null, tint = if (reminder.isEnabled) PrimaryBlue else Color.Gray)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(reminder.title, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                    Text(reminder.time, fontSize = 14.sp, color = Color.Gray)

                    // Mostrar días seleccionados
                    val daysText = if (reminder.daysOfWeek.size == 7) "Todos los días" else "Días seleccionados"
                    Text(daysText, fontSize = 10.sp, color = PrimaryBlue)

                    // Mostrar intervalo si existe
                    if (reminder.repeatIntervalHours > 0) {
                        Text("Repite cada ${reminder.repeatIntervalHours}h", fontSize = 10.sp, color = Color(0xFFF97316))
                    }
                }
            }
            Row {
                Switch(
                    checked = reminder.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(checkedTrackColor = PrimaryBlue)
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, null, tint = Color(0xFFEF4444).copy(alpha = 0.7f))
                }
            }
        }
    }
}

// --- DIÁLOGO AVANZADO ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, List<Int>, Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedTimeStr by remember { mutableStateOf("08:00 AM") }

    // Días seleccionados (Por defecto todos: 1=Dom ... 7=Sab)
    val allDays = listOf(2, 3, 4, 5, 6, 7, 1) // Orden L, M, M, J, V, S, D
    var selectedDays by remember { mutableStateOf(allDays.toSet()) } // Set para fácil manejo

    // Intervalo
    var intervalHours by remember { mutableFloatStateOf(0f) }

    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(8, 0, false)

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val cal = Calendar.getInstance()
                    cal.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                    cal.set(Calendar.MINUTE, timePickerState.minute)
                    val sdf = SimpleDateFormat("h:mm a", Locale.ENGLISH)
                    selectedTimeStr = sdf.format(cal.time)
                    showTimePicker = false
                }) { Text("Aceptar") }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configurar Alarma", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Título
                OutlinedTextField(
                    value = title, onValueChange = { title = it },
                    label = { Text("Título (ej: Agua)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )

                // Hora
                OutlinedTextField(
                    value = selectedTimeStr, onValueChange = {},
                    label = { Text("Hora de inicio") },
                    modifier = Modifier.fillMaxWidth().clickable { showTimePicker = true },
                    enabled = false,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = Color.Black,
                        disabledBorderColor = Color.Gray,
                        disabledLabelColor = Color.Gray
                    ),
                    trailingIcon = { Icon(Icons.Default.AccessTime, null) }
                )

                Divider()

                // Selector de Días
                Text("Repetir los días:", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val daysLabels = listOf("L", "M", "M", "J", "V", "S", "D")
                    // Mapeo visual a Calendar IDs
                    daysLabels.forEachIndexed { index, label ->
                        val dayId = allDays[index]
                        val isSelected = selectedDays.contains(dayId)

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) PrimaryBlue else Color.LightGray.copy(0.3f))
                                .clickable {
                                    selectedDays = if (isSelected) selectedDays - dayId else selectedDays + dayId
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(label, color = if (isSelected) Color.White else Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Divider()

                // Frecuencia (Agua)
                Text("Frecuencia de repetición:", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                if (intervalHours == 0f) {
                    Text("Solo una vez", fontSize = 12.sp, color = Color.Gray)
                } else {
                    Text("Repetir cada ${intervalHours.toInt()} horas", fontSize = 12.sp, color = Color(0xFFF97316))
                }

                Slider(
                    value = intervalHours,
                    onValueChange = { intervalHours = it },
                    valueRange = 0f..6f,
                    steps = 5, // 0, 1, 2, 3, 4, 5, 6
                    colors = SliderDefaults.colors(thumbColor = Color(0xFFF97316), activeTrackColor = Color(0xFFF97316))
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotEmpty() && selectedDays.isNotEmpty()) {
                        onConfirm(title, selectedTimeStr, selectedDays.toList(), intervalHours.toInt())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(8.dp)
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = Color.Gray) }
        }
    )
}