package com.example.bienestarsalud.ui.screens.goals

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bienestarsalud.domain.model.goals.UserGoals
import com.example.bienestarsalud.domain.repository.wellness.WellnessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val repo: WellnessRepository
) : ViewModel() {

    // Estados de la UI (Valores por defecto)
    var waterGoal by mutableFloatStateOf(8f)
    var sleepGoal by mutableFloatStateOf(8f)
    var caloriesGoal by mutableStateOf("2000")
    var workoutDaysGoal by mutableFloatStateOf(3f)

    var isLoading by mutableStateOf(true)
    var saveMessage by mutableStateOf("")

    init {
        loadGoals()
    }

    private fun loadGoals() = viewModelScope.launch {
        isLoading = true
        val goals = repo.getUserGoals()
        // Cargar datos de Firebase
        waterGoal = goals.waterGoal.toFloat()
        sleepGoal = goals.sleepGoal.toFloat()
        caloriesGoal = goals.dailyCaloriesGoal.toString()
        workoutDaysGoal = goals.weeklyWorkoutDaysGoal.toFloat()
        isLoading = false
    }

    fun saveGoals() = viewModelScope.launch {
        val newGoals = UserGoals(
            waterGoal = waterGoal.toInt(),
            sleepGoal = sleepGoal.toDouble(),
            dailyCaloriesGoal = caloriesGoal.toIntOrNull() ?: 2000,
            weeklyWorkoutDaysGoal = workoutDaysGoal.toInt()
        )

        repo.saveUserGoals(newGoals)
        saveMessage = "¡Metas actualizadas correctamente!"
    }
}