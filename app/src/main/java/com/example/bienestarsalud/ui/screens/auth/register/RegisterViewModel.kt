package com.example.bienestarsalud.ui.screens.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bienestarsalud.domain.repository.auth.AuthErrorType
import com.example.bienestarsalud.domain.repository.auth.AuthRepository
import com.example.bienestarsalud.domain.repository.auth.AuthResult
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
    val registered: Boolean = false,

    val fullNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val termsError: String? = null,
    val generalError: String? = null,

    val hasMinLength: Boolean = false,
    val hasUppercase: Boolean = false,
    val hasNumber: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(RegisterUiState())
    val ui = _ui.asStateFlow()

    fun onFullNameChange(name: String) {
        _ui.update { it.copy(fullName = name, fullNameError = null) }
    }

    fun onEmailChange(email: String) {
        _ui.update { it.copy(email = email, emailError = null) }
    }

    fun onTermsChange(accepted: Boolean) {
        _ui.update { it.copy(acceptTerms = accepted, termsError = null) }
    }

    fun onPasswordChange(pass: String) {
        val hasMinLength = pass.length >= 8
        val hasUppercase = pass.any { it.isUpperCase() }
        val hasNumber = pass.any { it.isDigit() }
        _ui.update { it.copy(password = pass, hasMinLength = hasMinLength, hasUppercase = hasUppercase, hasNumber = hasNumber, passwordError = null) }
    }

    fun signUp() = viewModelScope.launch {
        val state = _ui.value
        var hasError = false

        if (state.fullName.isBlank()) {
            _ui.update { it.copy(fullNameError = "El nombre es obligatorio") }
            hasError = true
        }
        if (state.email.isBlank()) {
            _ui.update { it.copy(emailError = "El correo es obligatorio") }
            hasError = true
        }
        if (!state.hasMinLength || !state.hasUppercase || !state.hasNumber) {
            _ui.update { it.copy(passwordError = "La contraseña no es segura") }
            hasError = true
        }
        if (!state.acceptTerms) {
            _ui.update { it.copy(termsError = "Debes aceptar los términos") }
            hasError = true
        }

        if (hasError) return@launch

        _ui.update { it.copy(loading = true, generalError = null) }

        // CORRECCIÓN: Usamos 'register' en lugar de 'signUp'
        when (val res = repo.register(state.email, state.password)) {
            is AuthResult.Success -> {
                // Guardamos el nombre del usuario
                repo.updateUserName(state.fullName)
                _ui.update { it.copy(loading = false, registered = true) }
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

    fun signUpWithGoogle(idToken: String) = viewModelScope.launch {
        _ui.update { it.copy(loading = true, generalError = null) }
        // CORRECCIÓN: Usamos el método definido en la interfaz
        when (val res = repo.signInWithGoogle(idToken)) {
            is AuthResult.Success -> {
                // Si tienes el nombre en Google, podrías actualizarlo aquí también si quisieras
                _ui.update { it.copy(loading = false, registered = true) }
            }
            is AuthResult.Error -> _ui.update { it.copy(loading = false, generalError = res.message) }
        }
    }
}