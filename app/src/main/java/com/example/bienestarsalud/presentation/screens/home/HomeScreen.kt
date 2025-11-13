package com.example.bienestarsalud.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bienestarsalud.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(PrimaryGreen, PrimaryBlue)
                        )
                    )
                    .padding(24.dp, top = 60.dp, bottom = 24.dp, end = 24.dp)
            ) {
                Column {
                    Text(
                        text = "Bienvenido de vuelta,",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Text(
                        text = "Usuario",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Lunes, 10 de Noviembre",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(48.dp)
                        .background(
                            Color.White.copy(alpha = 0.3f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = {
                        Icon(Icons.Default.Favorite, contentDescription = null)
                    },
                    label = { Text("Inicio") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryGreen,
                        selectedTextColor = PrimaryGreen,
                        indicatorColor = PrimaryGreen.copy(alpha = 0.1f)
                    )
                )
                NavigationBarItem(
                    icon = {
                        Icon(Icons.Default.BarChart, contentDescription = null)
                    },
                    label = { Text("Panel") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = {
                        Icon(Icons.Default.Settings, contentDescription = null)
                    },
                    label = { Text("Ajustes") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundLight)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Today's Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Resumen de Hoy",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    // Water
                    HealthMetricCard(
                        icon = "💧",
                        title = "Hidratación",
                        value = "4 / 8 vasos",
                        backgroundColor = CardBlue,
                        iconColor = PrimaryBlue
                    )

                    // Sleep
                    HealthMetricCard(
                        icon = "😴",
                        title = "Sueño",
                        value = "7.5 horas",
                        backgroundColor = CardIndigo,
                        iconColor = Color(0xFF7E57C2)
                    )

                    // Mood
                    HealthMetricCard(
                        icon = "😊",
                        title = "Estado de Ánimo",
                        value = "Feliz",
                        backgroundColor = CardYellow,
                        iconColor = PrimaryYellow
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { /* Update record */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryGreen
                        )
                    ) {
                        Text(
                            text = "Actualizar Registro",
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            // Weekly Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Resumen Semanal",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    WeeklyStat(
                        label = "Promedio de Agua",
                        value = "6.2 vasos",
                        color = PrimaryBlue
                    )

                    Divider()

                    WeeklyStat(
                        label = "Promedio de Sueño",
                        value = "7.1 horas",
                        color = Color(0xFF7E57C2)
                    )

                    Divider()

                    WeeklyStat(
                        label = "Ánimo General",
                        value = "3.8 / 5",
                        color = PrimaryYellow
                    )
                }
            }
        }
    }
}

@Composable
fun HealthMetricCard(
    icon: String,
    title: String,
    value: String,
    backgroundColor: Color,
    iconColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, MaterialTheme.shapes.large)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconColor, MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 24.sp
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        Text(
            text = icon,
            fontSize = 28.sp
        )
    }
}

@Composable
fun WeeklyStat(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = TextSecondary
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}