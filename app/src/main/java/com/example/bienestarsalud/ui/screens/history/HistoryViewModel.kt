package com.example.bienestarsalud.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bienestarsalud.domain.model.activity.ActivityLog
import com.example.bienestarsalud.domain.model.wellness.WellnessRecord
import com.example.bienestarsalud.domain.repository.wellness.WellnessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

// 1. Enum para los tipos de filtro (Esto es lo que te faltaba)
enum class HistoryFilter { ALL, WEEK, MONTH }

// 2. Clase sellada para mezclar Actividades y Resúmenes
sealed class HistoryItem {
    data class DailySummary(val record: WellnessRecord) : HistoryItem()
    data class ActivitySession(val activity: ActivityLog) : HistoryItem()

    // Helper para obtener la fecha de cualquier item
    val date: String get() = when(this) {
        is DailySummary -> record.date
        is ActivitySession -> activity.date
    }
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repo: WellnessRepository
) : ViewModel() {

    // Lista completa sin filtros (Copia de seguridad)
    private var _rawHistory: List<HistoryItem> = emptyList()

    // Lista visible en la pantalla (Filtrada)
    private val _unifiedHistory = MutableStateFlow<List<HistoryItem>>(emptyList())
    val unifiedHistory = _unifiedHistory.asStateFlow()

    // Estado de carga
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    // Estado del filtro actual (Esto también te faltaba)
    private val _currentFilter = MutableStateFlow(HistoryFilter.ALL)
    val currentFilter = _currentFilter.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() = viewModelScope.launch {
        _isLoading.value = true

        // 1. Cargar todo
        val dailyRecords = repo.getAllRecords().map { HistoryItem.DailySummary(it) }
        val activities = repo.getAllActivities().map { HistoryItem.ActivitySession(it) }

        // 2. Unir y ordenar por fecha (más reciente primero)
        // Se asume formato "yyyy-MM-dd" para ordenación de texto correcta
        _rawHistory = (dailyRecords + activities).sortedByDescending { it.date }

        // 3. Aplicar filtro inicial
        applyFilter(_currentFilter.value)
        _isLoading.value = false
    }

    // Función que llama la UI al pulsar los chips
    fun setFilter(filter: HistoryFilter) {
        _currentFilter.value = filter
        applyFilter(filter)
    }

    private fun applyFilter(filter: HistoryFilter) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = Calendar.getInstance()

        // Calculamos la fecha límite según el filtro
        val limitDate = Calendar.getInstance().apply {
            when (filter) {
                HistoryFilter.WEEK -> add(Calendar.DAY_OF_YEAR, -7)
                HistoryFilter.MONTH -> add(Calendar.MONTH, -1)
                else -> time = Date(0) // Fecha muy antigua para "TODOS"
            }
        }.time

        if (filter == HistoryFilter.ALL) {
            _unifiedHistory.value = _rawHistory
        } else {
            _unifiedHistory.value = _rawHistory.filter { item ->
                try {
                    val itemDate = sdf.parse(item.date)
                    // Si la fecha del item es posterior a la fecha límite, lo mostramos
                    itemDate != null && itemDate.after(limitDate)
                } catch (e: Exception) {
                    true // Si falla el parseo, lo mostramos por si acaso
                }
            }
        }
    }
}