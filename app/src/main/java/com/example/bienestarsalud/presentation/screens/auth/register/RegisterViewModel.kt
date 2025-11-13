package com.example.bienestarsalud.presentation.screens.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bienestarsalud.domain.repository.AuthRepository
import com.example.bienestarsalud.domain.repository.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val acceptTerms: Boolean = false,
    val loading: Boolean = false,
    val error: String? = null,
    val registered: Boolean = false,

    // Validación de contraseña
    val hasMinLength: Boolean = false,
    val hasUppercase: Boolean = false,
    val hasNumber: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repo: AuthRepository // <-- Hilt inyecta tu interfaz
) : ViewModel() {

    private val _ui = MutableStateFlow(RegisterUiState())
    val ui = _ui.asStateFlow()

    // 2. FUNCIONES: Lo que la UI puede "decirle" al ViewModel
    fun onFullNameChange(name: String) {
        _ui.update { it.copy(fullName = name, error = null) }
    }

    fun onEmailChange(email: String) {
        _ui.update { it.copy(email = email, error = null) }
    }

    fun onTermsChange(accepted: Boolean) {
        _ui.update { it.copy(acceptTerms = accepted, error = null) }
    }

    fun onPasswordChange(pass: String) {
        // Hacemos la validación aquí, en el ViewModel
        val hasMinLength = pass.length >= 8
        val hasUppercase = pass.any { it.isUpperCase() }
        val hasNumber = pass.any { it.isDigit() }
        _ui.update {
            it.copy(
                password = pass,
                hasMinLength = hasMinLength,
                hasUppercase = hasUppercase,
                hasNumber = hasNumber,
                error = null
            )
        }
    }

    // 3. LÓGICA DE NEGOCIO: El trabajo pesado
    fun signUp() = viewModelScope.launch {
        val state = _ui.value // Estado actual

        // Validaciones
        if (!state.hasMinLength || !state.hasUppercase || !state.hasNumber || !state.acceptTerms) {
            _ui.update { it.copy(error = "Completa todos los requisitos.") }
            return@launch
        }
        if (state.email.isBlank() || state.fullName.isBlank()) {
            _ui.update { it.copy(error = "Email y Nombre no pueden estar vacíos.") }
            return@launch
        }

        // Empezar a cargar
        _ui.update { it.copy(loading = true, error = null) }

        // Llamar a Firebase (a través del repositorio)
        when (val res = repo.signUp(state.email, state.password)) {
            is AuthResult.Success -> {
                // ¡Éxito!
                // Aquí podrías guardar el nombre del usuario
                // repo.updateProfile(res.user, state.fullName)
                _ui.update { it.copy(loading = false, registered = true) }
            }
            is AuthResult.Error -> {
                // Error
                _ui.update { it.copy(loading = false, error = res.message) }
            }
        }
    }
}