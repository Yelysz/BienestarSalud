package com.example.bienestarsalud.data.auth

import android.net.Uri
import com.example.bienestarsalud.domain.model.auth.AuthUser
import com.example.bienestarsalud.domain.repository.auth.AuthErrorType
import com.example.bienestarsalud.domain.repository.auth.AuthRepository
import com.example.bienestarsalud.domain.repository.auth.AuthResult
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,// Única instancia, llamada 'firebaseAuth'
    private val storage: FirebaseStorage
) : AuthRepository {

    // --- LOGIN ---
    override suspend fun login(email: String, pass: String): AuthResult {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, pass).await()
            val user = result.user!!
            AuthResult.Success(AuthUser(uid = user.uid, email = user.email))
        } catch (e: Exception) {
            // Manejo de errores específico
            if (e is FirebaseAuthInvalidCredentialsException && e.message?.contains("formatted") == true) {
                return AuthResult.Error(AuthErrorType.EMAIL, "El formato del correo no es válido.")
            }
            if (e is FirebaseAuthInvalidUserException || e is FirebaseAuthInvalidCredentialsException) {
                return AuthResult.Error(AuthErrorType.GENERAL, "Correo o contraseña incorrectos.")
            }
            val (type, msg) = mapFirebaseError(e)
            AuthResult.Error(type, msg)
        }
    }

    // --- REGISTRO ---
    override suspend fun register(email: String, pass: String): AuthResult {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
            val user = result.user!!
            AuthResult.Success(AuthUser(uid = user.uid, email = user.email))
        } catch (e: Exception) {
            val (type, msg) = mapFirebaseError(e)
            AuthResult.Error(type, msg)
        }
    }

    // --- GOOGLE ---
    override suspend fun signInWithGoogle(idToken: String): AuthResult {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user!!
            AuthResult.Success(AuthUser(uid = user.uid, email = user.email))
        } catch (e: Exception) {
            val (type, msg) = mapFirebaseError(e)
            AuthResult.Error(type, msg)
        }
    }

    // --- DATOS DEL USUARIO ---
    override fun currentUser(): AuthUser? {
        val firebaseUser = firebaseAuth.currentUser
        return firebaseUser?.let {
            AuthUser(uid = it.uid, email = it.email, displayName = it.displayName ?: "", photoUrl = it.photoUrl?.toString())
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }

    // --- PERFIL Y CONTRASEÑA (Usados en Settings) ---

    override suspend fun updateUserName(name: String): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                val updates = userProfileChangeRequest { displayName = name }
                user.updateProfile(updates).await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Usuario no logueado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- UTILIDADES ---
    private fun mapFirebaseError(e: Exception): Pair<AuthErrorType, String> {
        return when (e) {
            is FirebaseAuthUserCollisionException -> AuthErrorType.EMAIL to "Este correo ya está registrado."
            is FirebaseAuthWeakPasswordException -> AuthErrorType.PASSWORD to "La contraseña es muy débil (mín. 6 caracteres)."
            is FirebaseAuthInvalidCredentialsException -> {
                if (e.message?.contains("formatted") == true)
                    AuthErrorType.EMAIL to "El formato del correo no es válido."
                else
                    AuthErrorType.GENERAL to "Credenciales incorrectas."
            }
            is FirebaseAuthInvalidUserException -> AuthErrorType.GENERAL to "Cuenta no encontrada."
            is FirebaseNetworkException -> AuthErrorType.GENERAL to "Sin conexión a internet."
            else -> AuthErrorType.GENERAL to (e.localizedMessage ?: "Ocurrió un error desconocido.")
        }
    }

    override suspend fun updateProfilePicture(uri: Uri): Result<String> {
        return try {
            val user = firebaseAuth.currentUser ?: return Result.failure(Exception("No usuario"))

            // 1. Referencia: users/UID/profile.jpg
            val storageRef = storage.reference.child("users/${user.uid}/profile.jpg")

            // 2. Subir archivo
            storageRef.putFile(uri).await()

            // 3. Obtener URL pública
            val downloadUrl = storageRef.downloadUrl.await()

            // 4. Actualizar perfil de Auth
            val updates = userProfileChangeRequest { photoUri = downloadUrl }
            user.updateProfile(updates).await()

            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}