package com.example.bienestarsalud.domain.repository.auth

import com.example.bienestarsalud.domain.model.auth.AuthUser

// Definimos los tipos de resultado aquí para que estén disponibles
sealed class AuthResult {
    data class Success(val user: AuthUser) : AuthResult()
    data class Error(val type: AuthErrorType, val message: String) : AuthResult()
}

enum class AuthErrorType {
    EMAIL, PASSWORD, GENERAL
}

interface AuthRepository {
    // Autenticación Principal
    suspend fun login(email: String, pass: String): AuthResult
    suspend fun register(email: String, pass: String): AuthResult
    suspend fun signInWithGoogle(idToken: String): AuthResult

    // Gestión de Sesión
    fun currentUser(): AuthUser?
    fun signOut()

    // Perfil y Recuperación
    suspend fun updateUserName(name: String): Result<Unit>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>

    suspend fun updateProfilePicture(uri: android.net.Uri): Result<String>
}