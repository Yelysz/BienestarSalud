package com.example.bienestarsalud.presentation.screens.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bienestarsalud.domain.repository.AuthRepository
import com.example.bienestarsalud.domain.repository.AuthResult
import com.example.bienestarsalud.domain.repository.SimpleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val loggedIn: Boolean = false // Se vuelve true si el login es exitoso
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repo: AuthRepository // <-- Hilt inyecta tu repositorio
) : ViewModel() {

    private val _ui = MutableStateFlow(LoginUiState())
    val ui = _ui.asStateFlow()

    // 2. FUNCIONES: Lo que la UI puede "decirle" al ViewModel
    fun onEmailChange(email: String) {
        _ui.update { it.copy(email = email, error = null) }
    }

    fun onPasswordChange(pass: String) {
        _ui.update { it.copy(password = pass, error = null) }
    }

    // 3. LÓGICA DE NEGOCIO: Iniciar Sesión
    fun signIn() = viewModelScope.launch {
        val state = _ui.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _ui.update { it.copy(error = "Por favor, completa ambos campos.") }
            return@launch
        }

        // Empezar a cargar
        _ui.update { it.copy(loading = true, error = null) }

        // Llamar a Firebase (a través del repositorio)
        when (val res = repo.signIn(state.email, state.password)) {
            is AuthResult.Success -> {
                // ¡Éxito!
                _ui.update { it.copy(loading = false, loggedIn = true) }
            }
            is AuthResult.Error -> {
                // Error
                _ui.update { it.copy(loading = false, error = res.message) }
            }
        }
    }

    // 4. LÓGICA DE NEGOCIO: Enviar reseteo de contraseña
    fun sendReset() = viewModelScope.launch {
        val email = _ui.value.email
        if (email.isBlank()) {
            _ui.update { it.copy(error = "Escribe tu email para recuperar la contraseña.") }
            return@launch
        }

        _ui.update { it.copy(loading = true, error = null) }

        when (val res = repo.sendReset(email)) {
            is SimpleResult.Success -> {
                _ui.update { it.copy(loading = false, error = "Se ha enviado un correo a $email.") }
            }
            is SimpleResult.Error -> {
                _ui.update { it.copy(loading = false, error = res.message) }
            }
        }
    }
}