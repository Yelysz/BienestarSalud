package com.example.bienestarsalud.data.auth


import com.example.bienestarsalud.domain.model.AuthUser
import com.example.bienestarsalud.domain.repository.AuthRepository
import com.example.bienestarsalud.domain.repository.AuthResult
import com.example.bienestarsalud.domain.repository.SimpleResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository { // <-- Implementa la interfaz

    override suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user!! // No será null en un login exitoso
            // Mapeamos el FirebaseUser a tu AuthUser
            val authUser = AuthUser(uid = user.uid, email = user.email)
            AuthResult.Success(authUser)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error desconocido al iniciar sesión")
        }
    }

    override suspend fun signUp(email: String, password: String): AuthResult {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user!! // No será null en un registro exitoso
            // Mapeamos el FirebaseUser a tu AuthUser
            val authUser = AuthUser(uid = user.uid, email = user.email)
            AuthResult.Success(authUser)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error desconocido al registrarse")
        }
    }

    override suspend fun sendReset(email: String): SimpleResult {
        return try {
            auth.sendPasswordResetEmail(email).await()
            SimpleResult.Success
        } catch (e: Exception) {
            SimpleResult.Error(e.message ?: "Error desconocido")
        }
    }

    override fun currentUser(): AuthUser? {
        val firebaseUser = auth.currentUser
        // Si hay un usuario de Firebase, lo mapeamos a tu AuthUser
        return firebaseUser?.let {
            AuthUser(uid = it.uid, email = it.email)
        }
    }

    override fun signOut() {
        auth.signOut()
    }
}