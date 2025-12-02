package com.example.bienestarsalud.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// --- PALETA CLARA (La que ya tenías) ---
private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    secondary = PrimaryBlue,
    tertiary = PrimaryYellow,
    background = BackgroundLight, // 0xFFF9FAFB
    surface = SurfaceLight,       // White
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextPrimary,   // 0xFF1F2937 (Gris oscuro)
    onSurface = TextPrimary,
    error = Color(0xFFEF4444)
)

// --- PALETA OSCURA (Nueva) ---
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,       // Mantenemos la identidad
    secondary = PrimaryBlue,
    tertiary = PrimaryYellow,

    // Fondos oscuros (Tonos Slate/Blue Gray)
    background = Color(0xFF111827), // Gray 900
    surface = Color(0xFF1F2937),    // Gray 800 (Tarjetas)

    // Textos claros
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFF9FAFB), // Gray 50 (Texto principal)
    onSurface = Color(0xFFF3F4F6),    // Gray 100 (Texto secundario)

    error = Color(0xFFEF4444)
)

@Composable
fun BienestarSaludTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme // <--- Aquí selecciona el tema oscuro
        else -> LightColorScheme
    }

    // Configuración de la barra de estado (Status Bar)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Pinta la barra de estado del color del fondo (o primary si prefieres)
            window.statusBarColor = colorScheme.background.toArgb()

            // Si es tema claro -> Iconos negros. Si es oscuro -> Iconos blancos.
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Asegúrate de tener Typography.kt
        content = content
    )
}