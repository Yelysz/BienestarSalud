package com.example.bienestarsalud.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bienestarsalud.data.auth.FirebaseAuthRepositoryImpl
import com.example.bienestarsalud.domain.model.goals.UserGoals
import com.example.bienestarsalud.domain.model.wellness.WellnessRecord
import com.example.bienestarsalud.domain.repository.wellness.WellnessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

data class HomeState(
    val record: WellnessRecord = WellnessRecord(),
    val streak: Int = 0,
    val goals: UserGoals = UserGoals(), // <--- NUEVO: Para saber la meta (ej: 8 vasos)
    val photoUrl: String? = null ,       // <--- NUEVO: Foto de perfil
    val dailyTip: String = "Cargando consejo..."
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val wellnessRepo: WellnessRepository,
    private val authRepo: FirebaseAuthRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState = _uiState.asStateFlow()

    // Datos del usuario
    val userName = authRepo.currentUser()?.displayName ?: "Usuario"

    private val tipsList = listOf(
        "Beber un vaso de agua al despertar activa tu metabolismo.",
        "Dormir 7-8 horas mejora tu concentración y estado de ánimo.",
        "Caminar 30 minutos al día reduce el estrés significativamente.",
        "Comer frutas y verduras aporta las vitaminas que tu cuerpo necesita.",
        "Desconectar del celular 1 hora antes de dormir mejora el sueño.",
        "La constancia es la clave: pequeños pasos logran grandes cambios.",
        "Estirarte cada mañana ayuda a mejorar tu flexibilidad y energía.",
        "Recuerda respirar profundamente cuando sientas estrés."
    )

    // Saludo dinámico según la hora
    val greeting: String
        get() {
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            return when (hour) {
                in 5..11 -> "Buenos días,"
                in 12..19 -> "Buenas tardes,"
                else -> "Buenas noches,"
            }
        }

    val currentDate: String = try {
        val formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es", "ES"))
        LocalDate.now().format(formatter).replaceFirstChar { it.uppercase() }
    } catch (e: Exception) {
        "Hoy"
    }

    fun loadData() = viewModelScope.launch {
        val record = wellnessRepo.getTodayRecord() ?: WellnessRecord()
        val stats = wellnessRepo.getUserStats()
        val goals = wellnessRepo.getUserGoals()
        val user = authRepo.currentUser()

        // Elegir consejo aleatorio
        val randomTip = tipsList.random()

        _uiState.value = HomeState(
            record = record,
            streak = stats.currentStreak,
            goals = goals,
            photoUrl = user?.photoUrl,
            dailyTip = randomTip // <--- Guardamos el consejo elegido
        )
    }

    fun signOut() {
        authRepo.signOut()
    }
}