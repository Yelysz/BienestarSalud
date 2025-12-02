package com.example.bienestarsalud.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Mantenemos el nombre "user_prefs"
private val Context.dataStore by preferencesDataStore("user_prefs")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // --- CLAVES DE LA BASE DE DATOS ---
    private val EMAIL_KEY = stringPreferencesKey("saved_email")
    private val THEME_KEY = booleanPreferencesKey("is_dark_mode")
    private val NOTIF_KEY = booleanPreferencesKey("notifications_enabled") // <--- NUEVA CLAVE

    // ==========================================
    // LÓGICA DE EMAIL (LOGIN)
    // ==========================================
    val savedEmail: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[EMAIL_KEY]
    }

    suspend fun saveEmail(email: String) {
        context.dataStore.edit { prefs ->
            prefs[EMAIL_KEY] = email
        }
    }

    suspend fun clearEmail() {
        context.dataStore.edit { prefs ->
            prefs.remove(EMAIL_KEY)
        }
    }

    // ==========================================
    // LÓGICA DE MODO OSCURO (AJUSTES)
    // ==========================================
    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[THEME_KEY] ?: false
    }

    suspend fun saveTheme(isDark: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[THEME_KEY] = isDark
        }
    }

    // ==========================================
    // LÓGICA DE NOTIFICACIONES (AJUSTES) - NUEVO
    // ==========================================

    // Leemos la preferencia. Si no existe, por defecto es TRUE (Activado)
    val areNotificationsEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[NOTIF_KEY] ?: true
    }

    // Guardamos la preferencia (ON/OFF)
    suspend fun saveNotifications(isEnabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[NOTIF_KEY] = isEnabled
        }
    }
}