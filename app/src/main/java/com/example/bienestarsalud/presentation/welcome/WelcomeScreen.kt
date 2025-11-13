package com.example.bienestarsalud.presentation.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bienestarsalud.presentation.components.WaveHeader
import com.example.bienestarsalud.presentation.theme.Coral
import com.example.bienestarsalud.presentation.theme.OnCoral
import com.example.bienestarsalud.presentation.theme.TextSecondary
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
// ▼▼▼ ¡ESTE ES EL NUEVO IMPORT! ▼▼▼
import com.example.bienestarsalud.presentation.components.ConcaveShape

@Composable
fun WelcomeScreen(onContinue: () -> Unit) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { p ->
        Box(Modifier.fillMaxSize().padding(p)) {
            Column(Modifier.fillMaxSize()) {
                // Header ondulado
                WaveHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp), color = Coral
                )

                // Tarjeta inferior blanca con la nueva forma
                Column(
                    Modifier
                        // El offset negativo hace que la tarjeta "suba" y se superponga
                        .offset(y = (-80).dp)
                        .fillMaxSize()
                        // ▼▼▼ ¡AQUÍ ESTÁ EL CAMBIO! ▼▼▼
                        // Usamos ConcaveShape con una profundidad que coincide con el offset
                        .clip(ConcaveShape(curveDepth = 80.dp))
                        // ▲▲▲ ¡AQUÍ ESTÁ EL CAMBIO! ▲▲▲
                        .background(Color.White)
                        .padding(24.dp)
                ) {
                    // El Spacer empuja el contenido hacia abajo para que no quede
                    // oculto "dentro" de la curva
                    Spacer(Modifier.height(16.dp))
                    Text("Welcome", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Track your hydration, sleep and mood with a simple daily check-in.",
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(24.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        ExtendedFloatingActionButton(
                            onClick = onContinue,
                            containerColor = Coral,
                            text = { Text("Continue", color = OnCoral) },
                            icon = { Icon(Icons.Default.ArrowForward, contentDescription = null, tint = OnCoral) },
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }
        }
    }
}