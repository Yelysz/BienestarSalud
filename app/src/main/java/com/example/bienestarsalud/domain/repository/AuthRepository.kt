package com.example.bienestarsalud.domain.repository
import com.example.bienestarsalud.domain.model.AuthUser

sealed interface AuthResult {
    data class Success(val user: AuthUser): AuthResult
    data class Error(val message: String): AuthResult
}

sealed interface SimpleResult {
    object Success : SimpleResult
    data class Error(val message: String) : SimpleResult
}


interface AuthRepository {
    suspend fun signIn(email: String, password: String): AuthResult
    suspend fun signUp(email: String, password: String): AuthResult
    suspend fun sendReset(email: String): SimpleResult
    fun currentUser(): AuthUser?
    fun signOut()
}