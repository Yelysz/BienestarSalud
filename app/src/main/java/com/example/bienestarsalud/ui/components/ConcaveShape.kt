package com.example.bienestarsalud.ui.components

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

class ConcaveShape(val curveDepth: Dp = 60.dp) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {

        val curveDepthPx = with(density) { curveDepth.toPx() }

        val path = Path().apply {
            // Mueve al inicio (esquina superior izquierda, pero "bajando")
            moveTo(0f, curveDepthPx)

            // Dibuja la curva cóncava (el valle)
            quadraticBezierTo(
                x1 = size.width / 2, // Punto de control X (centro)
                y1 = 0f,             // Punto de control Y (arriba del todo)
                x2 = size.width,     // Punto final X (derecha)
                y2 = curveDepthPx    // Punto final Y (esquina superior derecha)
            )

            // Línea hacia la esquina inferior derecha
            lineTo(size.width, size.height)
            // Línea hacia la esquina inferior izquierda
            lineTo(0f, size.height)
            // Cierra el camino
            close()
        }
        return Outline.Generic(path)
    }
}