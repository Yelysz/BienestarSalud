package com.example.bienestarsalud.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import com.example.bienestarsalud.ui.theme.BackgroundLight
import com.example.bienestarsalud.ui.theme.BienestarSaludTheme
import com.example.bienestarsalud.ui.theme.PrimaryBlue
import com.example.bienestarsalud.ui.theme.PrimaryGreen
import com.example.bienestarsalud.ui.theme.PrimaryYellow
import com.example.bienestarsalud.ui.theme.TextPrimary
import com.example.bienestarsalud.ui.theme.TextSecondary
import kotlinx.coroutines.launch

data class OnboardingPage(
    val icon: ImageVector,
    val iconEmoji: String,
    val title: String,
    val description: String,
    val color: Color
)

@Composable
fun OnboardingScreen(onNavigateToLogin: () -> Unit) {
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

    // Aumentamos el conteo de páginas en 1 para crear el espacio de "salida"
    val pagerState = rememberPagerState(pageCount = { pages.size + 1 })
    val scope = rememberCoroutineScope()

    // Detectamos si llegamos a la página "fantasma" para navegar automáticamente
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage == pages.size) {
            onNavigateToLogin()
        }
    }

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
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp, bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Carrusel deslizable
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) { pageIndex ->
                if (pageIndex < pages.size) {
                    OnboardingPageContent(pages[pageIndex])
                } else {
                    // Página vacía transparente (la que dispara la navegación)
                    Box(Modifier.fillMaxSize())
                }
            }

            // Indicadores (Puntos abajo)
            // Solo los mostramos si NO estamos en la página fantasma
            if (pagerState.currentPage < pages.size) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(pages.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .width(if (isSelected) 32.dp else 8.dp)
                                .height(8.dp)
                                .background(
                                    color = if (isSelected) PrimaryGreen else Color.LightGray,
                                    shape = CircleShape
                                )
                        )
                    }
                }

                // Botones
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Botón Saltar
                    if (pagerState.currentPage < pages.size - 1) {
                        TextButton(
                            onClick = onNavigateToLogin,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Saltar", color = TextSecondary)
                        }
                    } else {
                        // Espacio vacío para mantener alineación
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Botón Siguiente / Comenzar
                    Button(
                        onClick = {
                            if (pagerState.currentPage < pages.size - 1) {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
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
                            text = if (pagerState.currentPage < pages.size - 1) "Siguiente" else "Comenzar",
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
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

        Text(
            text = page.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

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
    BienestarSaludTheme {
        OnboardingScreen(
            onNavigateToLogin = {}
        )
    }
}