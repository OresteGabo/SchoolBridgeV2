package com.schoolbridge.v2.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme

@Composable
fun SchoolBridgePatternBackground(
    modifier: Modifier = Modifier,
    dotAlpha: Float = 0.04f,
    gradientAlpha: Float = 0.08f
) {
    val scheme = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        scheme.primary.copy(alpha = gradientAlpha),
                        scheme.secondary.copy(alpha = gradientAlpha * 0.75f),
                        Color.Transparent
                    ),
                    center = Offset(220f, 120f),
                    radius = 1200f
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val dotColor = scheme.primary.copy(alpha = dotAlpha)
            val step = 60f
            for (x in 0..size.width.toInt() step step.toInt()) {
                for (y in 0..size.height.toInt() step step.toInt()) {
                    drawCircle(dotColor, 4f, Offset(x.toFloat(), y.toFloat()))
                }
            }
        }
    }
}
