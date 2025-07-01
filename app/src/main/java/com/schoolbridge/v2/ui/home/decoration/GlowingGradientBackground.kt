package com.schoolbridge.v2.ui.home.decoration

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.domain.user.UserRole


@Composable
fun GlowingGradientBackground(currentRole: UserRole?, scrollOffset: Float = 0f, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "Glow Animation")
    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "glowAlpha"
    )

    val colors = when (currentRole) {
        UserRole.TEACHER -> listOf(Color(0xFF00E5FF), Color(0xFF18FFFF), Color.Transparent)
        UserRole.STUDENT -> listOf(Color(0xFF8C9EFF), Color(0xFF536DFE), Color.Transparent)
        UserRole.PARENT -> listOf(Color(0xFFFF8A65), Color(0xFFFFAB91), Color.Transparent)
        UserRole.SCHOOL_ADMIN -> listOf(Color(0xFF69F0AE), Color(0xFFB9F6CA), Color.Transparent)
        else -> listOf(Color(0xFFD1C4E9), Color(0xFFB39DDB), Color.Transparent)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        listOf(
            Offset(200f, 400f),
            Offset(800f, 300f),
            Offset(500f, 1000f)
        ).forEachIndexed { index, offset ->
            Box(
                Modifier
                    .size((700 + index * 100).dp)
                    .graphicsLayer { alpha = animatedAlpha }
                    .blur(150.dp)
                    .offset { IntOffset((offset.x - scrollOffset).toInt(), offset.y.toInt()) }
                    .background(
                        Brush.radialGradient(
                            colors = colors,
                            center = offset,
                            radius = 1000f
                        ),
                        shape = CircleShape
                    )
            )
        }
    }
}
