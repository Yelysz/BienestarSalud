package com.example.bienestarsalud.domain.model.gamification

data class UserStats(
    val currentStreak: Int = 0,      // Racha actual (días seguidos)
    val bestStreak: Int = 0,         // Mejor racha histórica
    val lastLogDate: String = ""     // Fecha del último registro ("2025-11-28")
)