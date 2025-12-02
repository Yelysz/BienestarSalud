package com.example.bienestarsalud.ui.screens.auth.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bienestarsalud.data.local.UserPreferences
import com.example.bienestarsalud.domain.repository.auth.AuthErrorType
import com.example.bienestarsalud.domain.repository.auth.AuthRepository
import com.example.bienestarsalud.domain.repository.auth.AuthResult
// Eliminado: import com.example.bienestarsalud.domain.repository.auth.SimpleResult (Ya no existe)
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val loggedIn: Boolean = false,
    val rememberMe: Boolean = false,

    val emailError: String? = null,
    val passwordError: String? = null,
    val generalError: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repo: AuthRepository,
    private val prefs: UserPreferences
) : ViewModel() {

    private val _ui = MutableStateFlow(LoginUiState())
    val ui = _ui.asStateFlow()

    init {
        viewModelScope.launch {
            val savedEmail = prefs.savedEmail.first()
            if (!savedEmail.isNullOrBlank()) {
                _ui.update { it.copy(email = savedEmail, rememberMe = true) }
            }
        }
    }

    fun onEmailChange(email: String) {
        _ui.update { it.copy(email = email, emailError = null, generalError = null) }
    }

    fun onPasswordChange(pass: String) {
        _ui.update { it.copy(password = pass, passwordError = null, generalError = null) }
    }

    fun onRememberMeChange(checked: Boolean) {
        _ui.update { it.copy(rememberMe = checked) }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun signIn() = viewModelScope.launch {
        val state = _ui.value
        var hasError = false

        if (state.email.isBlank()) {
            _ui.update { it.copy(emailError = "El correo es obligatorio") }
            hasError = true
        } else if (!isValidEmail(state.email)) {
            _ui.update { it.copy(emailError = "El formato del correo no es válido") }
            hasError = true
        }

        if (state.password.isBlank()) {
            _ui.update { it.copy(passwordError = "La contraseña es obligatoria") }
            hasError = true
        }

        if (hasError) return@launch

        _ui.update { it.copy(loading = true, generalError = null, emailError = null, passwordError = null) }

        // CORRECCIÓN: Usamos 'login'
        when (val res = repo.login(state.email, state.password)) {
            is AuthResult.Success -> {
                if (state.rememberMe) prefs.saveEmail(state.email) else prefs.clearEmail()
                _ui.update { it.copy(loading = false, loggedIn = true) }
            }
            is AuthResult.Error -> {
                when (res.type) {
                    AuthErrorType.EMAIL -> _ui.update { it.copy(loading = false, emailError = res.message) }
                    AuthErrorType.PASSWORD -> _ui.update { it.copy(loading = false, passwordError = res.message) }
                    else -> _ui.update { it.copy(loading = false, generalError = res.message) }
                }
            }
        }
    }

    fun sendReset() = viewModelScope.launch {
        val email = _ui.value.email
        if (email.isBlank() || !isValidEmail(email)) {
            _ui.update { it.copy(emailError = "Ingresa un correo válido para recuperar.") }
            return@launch
        }
        _ui.update { it.copy(loading = true, generalError = null) }

        // CORRECCIÓN: Usamos 'sendPasswordResetEmail' y manejamos Result estándar
        val result = repo.sendPasswordResetEmail(email)

        if (result.isSuccess) {
            _ui.update { it.copy(loading = false, generalError = "Correo de recuperación enviado.") }
        } else {
            val errorMsg = result.exceptionOrNull()?.localizedMessage ?: "Error al enviar correo"
            _ui.update { it.copy(loading = false, generalError = errorMsg) }
        }
    }

    fun signInWithGoogle(idToken: String) = viewModelScope.launch {
        _ui.update { it.copy(loading = true, generalError = null) }
        when (val res = repo.signInWithGoogle(idToken)) {
            is AuthResult.Success -> _ui.update { it.copy(loading = false, loggedIn = true) }
            is AuthResult.Error -> _ui.update { it.copy(loading = false, generalError = res.message) }
        }
    }
}