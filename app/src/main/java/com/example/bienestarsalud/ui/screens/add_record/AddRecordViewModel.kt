package com.example.bienestarsalud.ui.screens.add_record

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bienestarsalud.domain.model.wellness.WellnessRecord
import com.example.bienestarsalud.domain.repository.wellness.WellnessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddRecordViewModel @Inject constructor(
    private val repo: WellnessRepository
) : ViewModel() {

    // Variables de estado que la pantalla está buscando
    var water by mutableIntStateOf(0)
    var sleep by mutableFloatStateOf(7f) // Usamos Float para el Slider
    var mood by mutableIntStateOf(3)
    var note by mutableStateOf("")       // Nota opcional
    var saved by mutableStateOf(false)   // <--- AQUÍ ESTÁ LA VARIABLE QUE FALTABA

    init {
        // Cargar datos si ya existen para hoy
        viewModelScope.launch {
            val record = repo.getTodayRecord()
            if (record != null) {
                water = record.waterGlasses
                sleep = record.sleepHours.toFloat()
                mood = record.mood
                note = record.note
            }
        }
    }

    fun save() = viewModelScope.launch {
        repo.saveRecord(
            WellnessRecord(
                date = LocalDate.now().toString(),
                waterGlasses = water,
                sleepHours = sleep.toDouble(),
                mood = mood,
                note = note
            )
        )
        saved = true // <--- Esto activará el cierre de la pantalla
    }
}