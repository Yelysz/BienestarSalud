package com.example.bienestarsalud.ui.screens.settings

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bienestarsalud.data.local.UserPreferences
import com.example.bienestarsalud.domain.repository.auth.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val firebaseAuth: FirebaseAuth,
    private val userPreferences: UserPreferences
) : ViewModel() {

    // Datos de usuario
    var profilePicUrl by mutableStateOf(firebaseAuth.currentUser?.photoUrl?.toString())
        private set

    var isUploading by mutableStateOf(false)
    var userName by mutableStateOf(firebaseAuth.currentUser?.displayName ?: "Usuario")
        private set
    val userEmail = firebaseAuth.currentUser?.email ?: "Sin Email"
    var message by mutableStateOf<String?>(null)

    // 1. ESTADO DEL TEMA (Leído desde DataStore)
    val isDarkMode = userPreferences.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // 2. ESTADO DE NOTIFICACIONES (Leído desde DataStore) <--- CAMBIO AQUÍ
    val notificationsEnabled = userPreferences.areNotificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    // --- FUNCIONES ---

    fun toggleDarkMode(currentValue: Boolean) = viewModelScope.launch {
        userPreferences.saveTheme(!currentValue)
    }

    // 3. FUNCIÓN PARA GUARDAR NOTIFICACIONES <--- CAMBIO AQUÍ
    fun toggleNotifications(currentValue: Boolean) = viewModelScope.launch {
        userPreferences.saveNotifications(!currentValue)
    }

    fun updateName(newName: String) = viewModelScope.launch {
        if (newName.isBlank()) return@launch
        val result = authRepo.updateUserName(newName)
        if (result.isSuccess) {
            userName = newName
            message = "Nombre actualizado"
        }
    }

    fun resetPassword() = viewModelScope.launch {
        authRepo.sendPasswordResetEmail(userEmail)
        message = "Correo enviado"
    }

    fun clearMessage() { message = null }

    fun signOut() { authRepo.signOut() }

    fun uploadImage(uri: Uri) = viewModelScope.launch {
        isUploading = true
        val result = authRepo.updateProfilePicture(uri)
        if (result.isSuccess) {
            profilePicUrl = result.getOrNull() // Actualizamos la UI
            message = "Foto de perfil actualizada"
        } else {
            message = "Error al subir la imagen"
        }
        isUploading = false
    }
}