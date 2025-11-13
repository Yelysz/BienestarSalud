package com.example.bienestarsalud.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

@Composable
fun WaveHeader(
    modifier: Modifier,
    color: Color
) {
    Canvas(modifier = modifier.background(color)) {
        val w = size.width
        val h = size.height

        // Fondo coral
        drawRect(color, size = size)

        // Borde blanco ondulado (recorte de la parte inferior)
        val path = Path().apply {
            moveTo(0f, h * 0.40f)
            cubicTo(
                w * 0.30f, h * 0.55f,
                w * 0.65f, h * 0.25f,
                w,         h * 0.40f
            )
            lineTo(w, 0f)
            lineTo(0f, 0f)
            close()
        }
        drawPath(path, color = Color.White)
    }
}
