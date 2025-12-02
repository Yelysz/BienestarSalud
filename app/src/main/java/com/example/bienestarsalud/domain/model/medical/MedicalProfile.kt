package com.example.bienestarsalud.domain.model.medical

data class MedicalProfile(
    val fullName: String = "",
    val bloodType: String = "",
    val height: String = "",
    val weight: String = "",
    val allergies: List<String> = emptyList(),
    val conditions: List<String> = emptyList()
)