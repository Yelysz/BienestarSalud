package com.example.bienestarsalud.ui.screens.medical

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bienestarsalud.domain.model.medical.MedicalProfile
import com.example.bienestarsalud.domain.repository.wellness.WellnessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicalViewModel @Inject constructor(
    private val repo: WellnessRepository
) : ViewModel() {

    // Datos simples
    var fullName by mutableStateOf("")
    var bloodType by mutableStateOf("")
    var height by mutableStateOf("")
    var weight by mutableStateOf("")

    // LISTAS DINÁMICAS (Para múltiples items)
    var allergiesList = mutableStateListOf<String>()
    var conditionsList = mutableStateListOf<String>()

    var isLoading by mutableStateOf(true)
    var saveMessage by mutableStateOf("")

    init {
        loadData()
    }

    private fun loadData() = viewModelScope.launch {
        isLoading = true
        val profile = repo.getMedicalProfile()
        if (profile != null) {
            fullName = profile.fullName
            bloodType = profile.bloodType
            height = profile.height
            weight = profile.weight

            // Limpiamos y cargamos las listas
            allergiesList.clear()
            allergiesList.addAll(profile.allergies)

            conditionsList.clear()
            conditionsList.addAll(profile.conditions)
        }
        isLoading = false
    }

    // Funciones para modificar las listas desde la UI
    fun addAllergy(item: String) {
        if (item.isNotBlank()) allergiesList.add(item)
    }

    fun removeAllergy(item: String) {
        allergiesList.remove(item)
    }

    fun addCondition(item: String) {
        if (item.isNotBlank()) conditionsList.add(item)
    }

    fun removeCondition(item: String) {
        conditionsList.remove(item)
    }

    fun save() = viewModelScope.launch {
        val profile = MedicalProfile(
            fullName = fullName,
            bloodType = bloodType,
            height = height,
            weight = weight,
            allergies = allergiesList.toList(), // Convertimos a lista inmutable para guardar
            conditions = conditionsList.toList()
        )
        repo.saveMedicalProfile(profile)
        saveMessage = "Datos actualizados correctamente"
    }
}