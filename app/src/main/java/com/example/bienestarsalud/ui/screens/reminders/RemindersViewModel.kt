package com.example.bienestarsalud.ui.screens.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bienestarsalud.domain.model.reminder.Reminder
import com.example.bienestarsalud.domain.repository.wellness.WellnessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemindersViewModel @Inject constructor(
    private val repo: WellnessRepository,
    private val alarmScheduler: AlarmScheduler // Inyectamos el Scheduler
) : ViewModel() {

    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders = _reminders.asStateFlow()

    init {
        loadReminders()
    }

    fun loadReminders() = viewModelScope.launch {
        _reminders.value = repo.getReminders()
    }

    fun addReminder(title: String, time: String, days: List<Int>, interval: Int) = viewModelScope.launch {
        val newReminder = Reminder(
            title = title,
            time = time,
            isEnabled = true,
            daysOfWeek = days,          // Nuevo
            repeatIntervalHours = interval // Nuevo
        )

        repo.saveReminder(newReminder)
        alarmScheduler.schedule(newReminder)

        loadReminders()
    }

    fun toggleReminder(reminder: Reminder) = viewModelScope.launch {
        val updated = reminder.copy(isEnabled = !reminder.isEnabled)
        repo.saveReminder(updated)

        if (updated.isEnabled) {
            alarmScheduler.schedule(updated)
        } else {
            alarmScheduler.cancel(updated)
        }
        loadReminders()
    }

    fun deleteReminder(id: String) = viewModelScope.launch {
        val reminder = _reminders.value.find { it.id == id }
        reminder?.let { alarmScheduler.cancel(it) } // Cancelar alarma antes de borrar

        repo.deleteReminder(id)
        loadReminders()
    }
}