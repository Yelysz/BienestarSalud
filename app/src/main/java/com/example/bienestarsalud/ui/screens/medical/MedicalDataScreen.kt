package com.example.bienestarsalud.ui.screens.medical

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalDataScreen(
    vm: MedicalViewModel = hiltViewModel()
) {
    var showEditDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
    ) {
        // --- HEADER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(Color(0xFFEF4444), Color(0xFFEC4899))))
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.background(Color.White.copy(0.2f), RoundedCornerShape(8.dp)).padding(8.dp)) {
                            Icon(Icons.Default.MedicalServices, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Datos Médicos", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier.background(Color.White.copy(0.2f), RoundedCornerShape(8.dp)).size(40.dp)
                    ) {
                        Icon(Icons.Default.Edit, null, tint = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.background(Color.White.copy(0.2f), RoundedCornerShape(12.dp)).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Security, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tus datos están privados y seguros", color = Color.White, fontSize = 12.sp)
                }
            }
        }

        // --- CONTENIDO ---
        if (vm.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFEC4899))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 1. INFO PERSONAL
                MedicalCard("Información Personal", Icons.Default.Person) {
                    InfoRow("Nombre Completo", vm.fullName.ifEmpty { "Sin definir" })
                    InfoRow("Tipo de Sangre", vm.bloodType.ifEmpty { "--" }, true)
                }

                // 2. MEDIDAS
                MedicalCard("Medidas Físicas", Icons.Default.Straighten) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MeasurementBox(Icons.Default.Scale, vm.weight.ifEmpty { "0" }, "kg", Color(0xFF3B82F6), Color(0xFFEFF6FF), Modifier.weight(1f))
                        MeasurementBox(Icons.Default.Height, vm.height.ifEmpty { "0" }, "cm", Color(0xFF9333EA), Color(0xFFF3E8FF), Modifier.weight(1f))
                    }
                }

                // 3. HISTORIAL (LISTAS)
                MedicalCard("Historial Médico", Icons.Default.History) {
                    // Alergias
                    if (vm.allergiesList.isNotEmpty()) {
                        Text("Alergias", fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                        vm.allergiesList.forEach { allergy ->
                            AlertBox(title = allergy, color = Color(0xFFEAB308), bgColor = Color(0xFFFEF9C3))
                            Spacer(Modifier.height(8.dp))
                        }
                    } else {
                        Text("Sin alergias registradas", color = Color.Gray, fontSize = 12.sp)
                    }

                    Spacer(Modifier.height(16.dp))

                    // Condiciones
                    if (vm.conditionsList.isNotEmpty()) {
                        Text("Condiciones", fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                        vm.conditionsList.forEach { condition ->
                            AlertBox(title = condition, color = Color(0xFF9333EA), bgColor = Color(0xFFF3E8FF))
                            Spacer(Modifier.height(8.dp))
                        }
                    } else {
                        Text("Sin condiciones registradas", color = Color.Gray, fontSize = 12.sp)
                    }
                }

                Spacer(Modifier.height(30.dp))
            }
        }
    }

    // --- DIÁLOGO EDICIÓN ---
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Editar Perfil Médico") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Campos Simples
                    MedicalEditField("Nombre", vm.fullName) { vm.fullName = it }
                    MedicalEditField("Tipo de Sangre", vm.bloodType) { vm.bloodType = it }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(Modifier.weight(1f)) { MedicalEditField("Peso (kg)", vm.weight, true) { vm.weight = it } }
                        Box(Modifier.weight(1f)) { MedicalEditField("Altura (cm)", vm.height, true) { vm.height = it } }
                    }

                    Divider()

                    // LISTA DE ALERGIAS (Con input para agregar)
                    Text("Alergias", fontWeight = FontWeight.Bold)
                    MultiItemInput(
                        items = vm.allergiesList,
                        onAdd = { vm.addAllergy(it) },
                        onRemove = { vm.removeAllergy(it) },
                        placeholder = "Ej: Penicilina"
                    )

                    Divider()

                    // LISTA DE CONDICIONES (Con input para agregar)
                    Text("Condiciones", fontWeight = FontWeight.Bold)
                    MultiItemInput(
                        items = vm.conditionsList,
                        onAdd = { vm.addCondition(it) },
                        onRemove = { vm.removeCondition(it) },
                        placeholder = "Ej: Asma"
                    )
                }
            },
            confirmButton = {
                Button(onClick = { vm.save(); showEditDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

// --- COMPONENTES AUXILIARES ---

@Composable
fun MultiItemInput(
    items: List<String>,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit,
    placeholder: String
) {
    var newItem by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Lista actual con opción de borrar
        items.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth().background(Color(0xFFF3F4F6), RoundedCornerShape(8.dp)).padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(item, fontSize = 14.sp)
                IconButton(onClick = { onRemove(item) }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, null, tint = Color.Gray)
                }
            }
        }

        // Input para agregar nuevo
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newItem,
                onValueChange = { newItem = it },
                placeholder = { Text(placeholder) },
                modifier = Modifier.weight(1f).height(56.dp),
                singleLine = true
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    if (newItem.isNotEmpty()) {
                        onAdd(newItem)
                        newItem = ""
                    }
                },
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, null)
            }
        }
    }
}

@Composable
fun MedicalCard(title: String, icon: ImageVector, content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1F2937))
                Icon(icon, null, tint = Color.Gray)
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, isHighlighted: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).background(Color(0xFFF9FAFB), RoundedCornerShape(12.dp)).padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Text(value, fontWeight = FontWeight.Bold, color = if (isHighlighted) Color(0xFFDC2626) else Color(0xFF1F2937))
    }
}

@Composable
fun MeasurementBox(icon: ImageVector, value: String, unit: String, color: Color, bgColor: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier.background(bgColor, RoundedCornerShape(16.dp)).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
        Text(unit, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun AlertBox(title: String, color: Color, bgColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().background(bgColor, RoundedCornerShape(12.dp)).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(4.dp, 24.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(12.dp))
        Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937), fontSize = 14.sp)
    }
}

@Composable
fun MedicalEditField(label: String, value: String, isNumber: Boolean = false, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        keyboardOptions = if (isNumber) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
        modifier = Modifier.fillMaxWidth()
    )
}