package com.example.bienestarsalud.presentation.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bienestarsalud.presentation.theme.BienestarSaludTheme
import com.example.bienestarsalud.presentation.theme.GradientCenter
import com.example.bienestarsalud.presentation.theme.GradientEnd
import com.example.bienestarsalud.presentation.theme.GradientStart
import com.example.bienestarsalud.presentation.theme.Poppins
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToOnboarding: () -> Unit) {
    // Animation states
    val infiniteTransition = rememberInfiniteTransition(label = "splash")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    // Auto navigate after 2.5 seconds
    LaunchedEffect(Unit) {
        delay(2500)
        onNavigateToOnboarding()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(GradientStart, GradientCenter)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Text(
                text = "💚",
                fontSize = 80.sp,
                modifier = Modifier
                    // 1. Añade un fondo
                    .background(
                        // Un color semi-transparente se ve muy bien
                        color = Color.White.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(30.dp)
                    )
                    // 2. Añade espacio interno (padding)
                    .padding(16.dp)
                    // 3. Tus animaciones (se quedan igual)
                    .scale(scale)
                    .alpha(alpha)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Soma",
                fontSize = 36.sp,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tu compañero de salud universitaria",
                fontFamily = Poppins,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Loading dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) { index ->
                    val dotAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, delayMillis = index * 200),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dot$index"
                    )

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .alpha(dotAlpha)
                            .background(
                                Color.White,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    BienestarSaludTheme {
        SplashScreen(
            onNavigateToOnboarding = {}
        )
    }
}
