package com.example.bienestarsalud.ui.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.bienestarsalud.ui.navigation.MainScreen
import com.example.bienestarsalud.ui.theme.BienestarSaludTheme
import com.example.bienestarsalud.ui.workers.StreakReminderWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Inyectamos el MainViewModel para saber el tema antes de dibujar
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // WorkManager (Recordatorios de racha)
        val streakRequest = PeriodicWorkRequestBuilder<StreakReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(6, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "StreakCheck", ExistingPeriodicWorkPolicy.KEEP, streakRequest
        )

        setContent {
            // Leemos el estado del tema
            val isDarkTheme by mainViewModel.isDarkMode.collectAsState()

            // Aplicamos el tema a toda la app
            BienestarSaludTheme(darkTheme = isDarkTheme) {
                MainScreen()
            }
        }
    }
}