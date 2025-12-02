package com.example.bienestarsalud.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bienestarsalud.ui.theme.PrimaryGreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    // Obtenemos la ruta actual para saber si mostramos la barra o no
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    // Lista de pantallas donde SI queremos ver la barra de navegación
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.History.route,
        Screen.Statistics.route,
        Screen.Settings.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    // 1. INICIO
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, null) },
                        label = { Text("Inicio") },
                        selected = currentDestination?.hierarchy?.any { it.route == Screen.Home.route } == true,
                        onClick = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryGreen)
                    )

                    // 2. HISTORIAL
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.DateRange, null) }, // O Icons.Default.History
                        label = { Text("Historial") },
                        selected = currentDestination?.hierarchy?.any { it.route == Screen.History.route } == true,
                        onClick = {
                            navController.navigate(Screen.History.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryGreen)
                    )

                    // 3. GRÁFICOS (Estadísticas)
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.BarChart, null) },
                        label = { Text("Gráficos") },
                        selected = currentDestination?.hierarchy?.any { it.route == Screen.Statistics.route } == true,
                        onClick = {
                            navController.navigate(Screen.Statistics.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryGreen)
                    )

                    // 4. AJUSTES
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, null) },
                        label = { Text("Ajustes") },
                        selected = currentDestination?.hierarchy?.any { it.route == Screen.Settings.route } == true,
                        onClick = {
                            navController.navigate(Screen.Settings.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryGreen)
                    )
                }
            }
        }
    ) { paddingValues ->
        // Importante: Pasamos el padding al NavGraph para que el contenido no quede oculto tras la barra
        // Asegúrate de modificar tu NavGraph para aceptar el parámetro 'modifier' si aún no lo hace.
        NavGraph(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}