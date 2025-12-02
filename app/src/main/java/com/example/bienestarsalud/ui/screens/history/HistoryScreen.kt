package com.example.bienestarsalud.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bienestarsalud.domain.model.activity.ActivityLog
import com.example.bienestarsalud.domain.model.wellness.WellnessRecord
import com.example.bienestarsalud.ui.theme.PrimaryBlue
import com.example.bienestarsalud.ui.theme.PrimaryGreen

@Composable
fun HistoryScreen(
    vm: HistoryViewModel = hiltViewModel()
) {
    val historyItems by vm.unifiedHistory.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val currentFilter by vm.currentFilter.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            // CORRECCIÓN: Fondo dinámico
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            "Tu Cronología",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground, // Texto dinámico
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = currentFilter == HistoryFilter.ALL,
                onClick = { vm.setFilter(HistoryFilter.ALL) },
                label = { Text("Todos") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PrimaryGreen, selectedLabelColor = Color.White)
            )
            FilterChip(
                selected = currentFilter == HistoryFilter.WEEK,
                onClick = { vm.setFilter(HistoryFilter.WEEK) },
                label = { Text("7 días") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PrimaryGreen, selectedLabelColor = Color.White)
            )
            FilterChip(
                selected = currentFilter == HistoryFilter.MONTH,
                onClick = { vm.setFilter(HistoryFilter.MONTH) },
                label = { Text("Mes") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PrimaryGreen, selectedLabelColor = Color.White)
            )
        }

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = PrimaryGreen) }
        } else if (historyItems.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay registros aún", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(historyItems) { item ->
                    when (item) {
                        is HistoryItem.DailySummary -> DailySummaryCard(item.record)
                        is HistoryItem.ActivitySession -> ActivityHistoryCard(item.activity)
                    }
                }
            }
        }
    }
}

@Composable
fun DailySummaryCard(record: WellnessRecord) {
    Card(
        // CORRECCIÓN: Tarjeta dinámica
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Resumen Diario • ${record.date}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                Icon(
                    imageVector = if (record.mood >= 3) Icons.Default.SentimentSatisfied else Icons.Default.SentimentDissatisfied,
                    contentDescription = null,
                    tint = if (record.mood >= 3) PrimaryGreen else Color(0xFFEF4444)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MetricSmall(Icons.Default.WaterDrop, "${record.waterGlasses} vasos", PrimaryBlue)
                MetricSmall(Icons.Default.Bedtime, String.format("%.1f h", record.sleepHours), Color(0xFF9333EA))
            }
            if (record.note.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("\"${record.note}\"", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            }
        }
    }
}

@Composable
fun ActivityHistoryCard(activity: ActivityLog) {
    Card(
        // Esta puede quedarse colorida o cambiar a surface si prefieres
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(Color(0xFFF97316).copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.FitnessCenter, null, tint = Color(0xFFF97316), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(activity.name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(activity.date, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Spacer(Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Text("${activity.calories} kcal", fontWeight = FontWeight.Bold, color = Color(0xFFF97316))
                Text("${activity.durationMinutes} min", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
fun MetricSmall(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(6.dp))
        Text(text, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = color)
    }
}