package com.example.bienestarsalud.domain.model.goals

data class UserGoals(
    val waterGoal: Int = 8,
    val sleepGoal: Double = 8.0,
    val dailyCaloriesGoal: Int = 5000, // Meta de calorías
    val weeklyWorkoutDaysGoal: Int = 7 // Meta de días de ejercicio
)