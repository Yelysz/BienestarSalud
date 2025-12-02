package com.example.bienestarsalud.domain.model.activity

data class ActivityLog(
    val id: String = "",
    val name: String = "",        // Ej: "Caminar", "Gym"
    val durationMinutes: Int = 0,
    val calories: Int = 0,
    val date: String = "",        // Ej: "2025-11-28"
    val iconName: String = "run"
)