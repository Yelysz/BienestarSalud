package com.example.bienestarsalud.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bienestarsalud.ui.screens.add_record.AddRecordScreen
import com.example.bienestarsalud.ui.screens.auth.login.LoginScreen
import com.example.bienestarsalud.ui.screens.auth.register.RegisterScreen
import com.example.bienestarsalud.ui.screens.home.HomeScreen
import com.example.bienestarsalud.ui.screens.onboarding.OnboardingScreen
import com.example.bienestarsalud.ui.screens.settings.SettingsScreen
import com.example.bienestarsalud.ui.screens.splash.SplashScreen
import com.example.bienestarsalud.ui.screens.statistics.StatisticsScreen
import com.example.bienestarsalud.ui.screens.history.HistoryScreen
import com.example.bienestarsalud.ui.screens.activities.ActivitiesScreen
import com.example.bienestarsalud.ui.screens.medical.MedicalDataScreen
import com.example.bienestarsalud.ui.screens.goals.GoalsScreen
import com.example.bienestarsalud.ui.screens.reminders.RemindersScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object AddRecord : Screen("add_record")
    object Settings : Screen("settings")
    object Statistics : Screen("statistics")
    object History : Screen("history")
    object Activities : Screen("activities")
    object Medical : Screen("medical")
    object Goals : Screen("goals")
    object Reminders : Screen("reminders")
}

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) { popUpTo(Screen.Splash.route) { inclusive = true } }
                }
            )
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) { popUpTo(Screen.Onboarding.route) { inclusive = true } }
                }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) { popUpTo(Screen.Register.route) { inclusive = true } }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToAddRecord = { navController.navigate(Screen.AddRecord.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToStatistics = { navController.navigate(Screen.Statistics.route) },
                onNavigateToScreen = { route -> navController.navigate(route) }
            )
        }

        composable(Screen.AddRecord.route) {
            AddRecordScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) { popUpTo(0) }
                }
            )
        }

        // --- NUEVAS PANTALLAS ---
        composable(Screen.Statistics.route) { StatisticsScreen() }
        composable(Screen.History.route) { HistoryScreen() }
        composable(Screen.Activities.route) { ActivitiesScreen() }
        composable(Screen.Medical.route) { MedicalDataScreen() }
        composable(Screen.Goals.route) { GoalsScreen() }
        composable(Screen.Reminders.route) {
            RemindersScreen(onBackClick = { navController.popBackStack() })
        }
    }
}