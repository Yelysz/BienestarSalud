package com.example.bienestarsalud.domain.model.wellness

data class WellnessRecord(
    val date: String = "",
    val waterGlasses: Int = 0,
    val sleepHours: Double = 0.0,
    val mood: Int = 3,
    val note: String = ""
)