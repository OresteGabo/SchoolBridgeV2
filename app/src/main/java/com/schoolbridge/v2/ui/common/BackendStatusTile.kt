package com.schoolbridge.v2.ui.common

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

enum class BackendConnectionState {
    DISCONNECTED,
    RECONNECTING,
    RECOVERED
}

@Composable
fun BackendStatusTile(
    title: String,
    message: String,
    helperText: String,
    state: BackendConnectionState,
    onRetry: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val accentContainer = when (state) {
        BackendConnectionState.DISCONNECTED -> colorScheme.errorContainer.copy(alpha = 0.7f)
        BackendConnectionState.RECONNECTING -> colorScheme.tertiaryContainer.copy(alpha = 0.78f)
        BackendConnectionState.RECOVERED -> colorScheme.secondaryContainer.copy(alpha = 0.82f)
    }
    val accentColor = when (state) {
        BackendConnectionState.DISCONNECTED -> colorScheme.error
        BackendConnectionState.RECONNECTING -> colorScheme.tertiary
        BackendConnectionState.RECOVERED -> colorScheme.secondary
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = colorScheme.surfaceContainerHigh,
        tonalElevation = 4.dp,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentContainer)
            ) {
                BackendStatusIndicator(
                    color = accentColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = helperText,
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor
                )
            }

            if (state != BackendConnectionState.RECOVERED && onRetry != null) {
                TextButton(onClick = onRetry) {
                    Text(if (state == BackendConnectionState.RECONNECTING) "Try now" else "Retry")
                }
            }
        }
    }
}

@Composable
private fun BackendStatusIndicator(
    color: Color,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "backendStatusIndicator")
    val phase = transition.animateFloat(
        initialValue = 0f,
        targetValue = (Math.PI * 2f).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )
    val pulse = transition.animateFloat(
        initialValue = 0.82f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(modifier = modifier) {
        drawBackendStatusIndicator(
            color = color,
            phase = phase.value,
            pulse = pulse.value
        )
    }
}

private fun DrawScope.drawBackendStatusIndicator(
    color: Color,
    phase: Float,
    pulse: Float
) {
    val radius = min(size.width, size.height) / 2f
    val center = Offset(size.width / 2f, size.height / 2f)
    val orbitRadius = radius * 0.62f * pulse
    val dotRadius = radius * 0.12f

    repeat(6) { index ->
        val angle = phase + (index * 0.9f)
        val dotCenter = Offset(
            x = center.x + cos(angle) * orbitRadius,
            y = center.y + sin(angle) * orbitRadius
        )
        val alpha = 0.3f + (((sin(angle) + 1f) / 2f) * 0.7f)
        drawCircle(
            color = color.copy(alpha = alpha),
            radius = dotRadius + (alpha * radius * 0.04f),
            center = dotCenter
        )
    }

    drawCircle(
        color = color.copy(alpha = 0.18f),
        radius = radius * 0.38f * pulse,
        center = center
    )
}
