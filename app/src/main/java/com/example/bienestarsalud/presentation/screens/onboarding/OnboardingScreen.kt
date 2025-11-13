package com.example.bienestarsalud.presentation.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bienestarsalud.presentation.theme.*

data class OnboardingPage(
    val icon: ImageVector,
    val iconEmoji: String,
    val title: String,
    val description: String,
    val color: Color
)

@Composable
fun OnboardingScreen(onNavigateToLogin: () -> Unit) {
    var currentPage by remember { mutableIntStateOf(0) }

    val pages = listOf(
        OnboardingPage(
            icon = Icons.Default.WaterDrop,
            iconEmoji = "💧",
            title = "Mantente Hidratado",
            description = "Registra tu consumo de agua diario y recibe recordatorios personalizados",
            color = PrimaryBlue
        ),
        OnboardingPage(
            icon = Icons.Default.Bedtime,
            iconEmoji = "😴",
            title = "Mejora tu Sueño",
            description = "Rastrea tus horas de sueño y descubre patrones para descansar mejor",
            color = Color(0xFF7E57C2)
        ),
        OnboardingPage(
            icon = Icons.Default.SentimentSatisfied,
            iconEmoji = "😊",
            title = "Cuida tu Ánimo",
            description = "Registra cómo te sientes cada día y observa tu progreso emocional",
            color = PrimaryYellow
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundLight, Color.White)
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(top = 32.dp, bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            // Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                OnboardingPageContent(pages[currentPage])
            }

            // Indicators
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                pages.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .width(if (index == currentPage) 32.dp else 8.dp)
                            .height(8.dp)
                            .background(
                                color = if (index == currentPage) PrimaryGreen else Color.LightGray,
                                shape = CircleShape
                            )
                    )
                }
            }

            // Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Skip button
                if (currentPage < pages.size - 1) {
                    TextButton(
                        onClick = onNavigateToLogin,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Saltar", color = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Next/Start button
                Button(
                    onClick = {
                        if (currentPage < pages.size - 1) {
                            currentPage++
                        } else {
                            onNavigateToLogin()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreen
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(
                        text = if (currentPage < pages.size - 1) "Siguiente" else "Comenzar",
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = page.color.copy(alpha = 0.1f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = page.iconEmoji,
                fontSize = 64.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = page.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = page.description,
            fontSize = 16.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    // Usamos el tema de tu app para que el preview use tus fuentes y colores
    BienestarSaludTheme {
        // Llamamos a tu pantalla y le pasamos una función vacía {}
        // Esto soluciona el error del preview.
        OnboardingScreen(
            onNavigateToLogin = {}
        )
    }
}