package com.example.bienestarsalud.domain.model.reminder

data class Reminder(
    val id: String = "",
    val title: String = "",       // Ej: "Beber Agua"
    val time: String = "",        // Ej: "08:00 AM"
    val isEnabled: Boolean = true,

    // Lista de días (1=Domingo, 2=Lunes ... 7=Sábado según Calendar de Java)
    val daysOfWeek: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7),
    // 0 = Una sola vez. >0 = Repetir cada X horas (Ej: 2 para agua)
    val repeatIntervalHours: Int = 0
)