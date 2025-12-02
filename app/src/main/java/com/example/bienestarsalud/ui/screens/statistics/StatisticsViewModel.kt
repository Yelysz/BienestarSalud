package com.example.bienestarsalud.ui.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bienestarsalud.domain.model.goals.UserGoals
import com.example.bienestarsalud.domain.repository.wellness.WellnessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

data class StatsState(
    // ... (campos anteriores) ...
    val totalRecords: Int = 0,
    val avgWater: Double = 0.0,
    val avgSleep: Double = 0.0,
    val avgMood: Double = 0.0,
    val totalCaloriesToday: Int = 0,
    val totalMinutesToday: Int = 0,
    val waterTrend: List<Float> = emptyList(),
    val daysLabels: List<String> = emptyList(),

    // --- NUEVOS CAMPOS PARA METAS ---
    val userGoals: UserGoals = UserGoals(),
    val activeDaysCount: Int = 0,     // Cuántos días has cumplido esta semana
    val last7DaysStatus: List<Pair<String, Boolean>> = emptyList() // (Día, ¿Activo?) ej: [("L", true), ("M", false)...]
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repo: WellnessRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsState())
    val uiState = _uiState.asStateFlow()

    init {
        calculateStats()
    }

    // Función pública para recargar cuando guardes una nueva meta
    fun refresh() {
        calculateStats()
    }

    fun updateUserGoals(calories: Int, days: Int) = viewModelScope.launch {
        val newGoals = UserGoals(dailyCaloriesGoal = calories, weeklyWorkoutDaysGoal = days)
        repo.saveUserGoals(newGoals)
        calculateStats() // Recalcular UI
    }

    private fun calculateStats() = viewModelScope.launch {
        // 1. Obtener datos
        val goals = repo.getUserGoals()
        val allActivities = repo.getAllActivities()
        val allRecords = repo.getAllRecords()

        val todayFormatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val todayString = todayFormatter.format(java.util.Date())

        val activitiesToday = repo.getActivitiesByDate(todayString)
        val totalCals = activitiesToday.sumOf { it.calories }
        val totalMins = activitiesToday.sumOf { it.durationMinutes }

        // --- LÓGICA DE DÍAS ACTIVOS (CORREGIDA PARA TODOS LOS ANDROID) ---
        val last7DaysStatus = mutableListOf<Pair<String, Boolean>>()
        var activeDaysCount = 0

        // Usamos Calendar en lugar de LocalDate para compatibilidad
        val calendar = java.util.Calendar.getInstance()
        val dayFormat = java.text.SimpleDateFormat("EEEEE", java.util.Locale("es", "ES")) // "L", "M"...

        // Retrocedemos 6 días para empezar desde el principio de la semana (hace 6 días)
        calendar.add(java.util.Calendar.DAY_OF_YEAR, -6)

        for (i in 0..6) {
            val dateStr = todayFormatter.format(calendar.time)
            val dayName = dayFormat.format(calendar.time).uppercase()

            val hasActivity = allActivities.any { it.date == dateStr }
            if (hasActivity) activeDaysCount++

            last7DaysStatus.add(Pair(dayName, hasActivity))

            // Avanzamos un día
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }

        // --- CÁLCULOS GRÁFICOS ---
        val last7DaysRecords = allRecords.take(7)
        val count = if (last7DaysRecords.isNotEmpty()) last7DaysRecords.size else 1

        val trendData = last7DaysRecords.map { (it.waterGlasses.toFloat() / 10f).coerceIn(0.1f, 1f) }.reversed()
        val trendLabels = last7DaysRecords.map { if(it.date.length >= 10) it.date.takeLast(2) else "??" }.reversed()

        _uiState.value = StatsState(
            totalRecords = last7DaysRecords.size,
            avgWater = last7DaysRecords.sumOf { it.waterGlasses }.toDouble() / count,
            avgSleep = last7DaysRecords.sumOf { it.sleepHours } / count,
            avgMood = last7DaysRecords.sumOf { it.mood }.toDouble() / count,
            totalCaloriesToday = totalCals,
            totalMinutesToday = totalMins,
            waterTrend = trendData,
            daysLabels = trendLabels,
            userGoals = goals,
            activeDaysCount = activeDaysCount,
            last7DaysStatus = last7DaysStatus
        )
    }
}