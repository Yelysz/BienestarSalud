package com.example.bienestarsalud.ui.screens.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bienestarsalud.domain.model.activity.ActivityLog
import com.example.bienestarsalud.domain.repository.wellness.WellnessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivitiesViewModel @Inject constructor(
    private val repo: WellnessRepository
) : ViewModel() {

    private val _todayActivities = MutableStateFlow<List<ActivityLog>>(emptyList())
    val todayActivities = _todayActivities.asStateFlow()

    private val _totalCalories = MutableStateFlow(0)
    val totalCalories = _totalCalories.asStateFlow()

    private val _totalMinutes = MutableStateFlow(0)
    val totalMinutes = _totalMinutes.asStateFlow()

    init {
        loadTodayActivities()
    }

    fun loadTodayActivities() = viewModelScope.launch {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        val activities = repo.getActivitiesByDate(today)
        _todayActivities.value = activities

        // Calcular totales
        _totalCalories.value = activities.sumOf { it.calories }
        _totalMinutes.value = activities.sumOf { it.durationMinutes }
    }

    fun addActivity(name: String, minutes: Int, calories: Int, icon: String) = viewModelScope.launch {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())

        val newActivity = ActivityLog(
            name = name,
            durationMinutes = minutes,
            calories = calories,
            date = today,
            iconName = icon
        )
        repo.saveActivity(newActivity)
        loadTodayActivities() // Recargar lista
    }

    fun deleteActivity(activity: ActivityLog) = viewModelScope.launch {
        repo.deleteActivity(activity.id)
        loadTodayActivities() // Recargar lista tras borrar
    }
}