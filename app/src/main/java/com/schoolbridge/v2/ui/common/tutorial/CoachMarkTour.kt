package com.schoolbridge.v2.ui.common.tutorial

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.math.max
import kotlin.math.min

data class CoachMarkStep(
    val targetId: String,
    val title: String,
    val body: String
)

@Stable
class CoachMarkTargetRegistry {
    private val targetBounds = mutableStateMapOf<String, Rect>()

    fun updateTarget(id: String, bounds: Rect) {
        targetBounds[id] = bounds
    }

    fun boundsOf(id: String): Rect? = targetBounds[id]
}

@Composable
fun rememberCoachMarkTargetRegistry(): CoachMarkTargetRegistry = remember { CoachMarkTargetRegistry() }

fun Modifier.coachMarkTarget(
    id: String,
    registry: CoachMarkTargetRegistry?
): Modifier {
    if (registry == null) return this
    return this.onGloballyPositioned { coordinates ->
        registry.updateTarget(id, coordinates.boundsInRoot())
    }
}

@Composable
fun CoachMarkOverlay(
    registry: CoachMarkTargetRegistry,
    steps: List<CoachMarkStep>,
    currentIndex: Int,
    onSkip: () -> Unit,
    onNext: () -> Unit,
    onDone: () -> Unit,
    onTargetUnavailable: () -> Unit,
    modifier: Modifier = Modifier
) {
    val step = steps.getOrNull(currentIndex) ?: return
    val bounds = registry.boundsOf(step.targetId)
    val highlightPadding = 10.dp

    LaunchedEffect(step.targetId) {
        repeat(8) {
            if (registry.boundsOf(step.targetId) != null) {
                return@LaunchedEffect
            }
            delay(120)
        }
        if (registry.boundsOf(step.targetId) == null) {
            onTargetUnavailable()
        }
    }

    if (bounds == null) return

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.56f))
    ) {
        HighlightTarget(
            bounds = bounds,
            padding = highlightPadding
        )

        CoachMarkCard(
            step = step,
            index = currentIndex,
            total = steps.size,
            bounds = bounds,
            onSkip = onSkip,
            onNext = onNext,
            onDone = onDone,
            modifier = Modifier.align(Alignment.TopStart)
        )
    }
}

@Composable
private fun HighlightTarget(
    bounds: Rect,
    padding: Dp
) {
    val density = LocalDensity.current
    val padPx = with(density) { padding.toPx() }
    val left = with(density) { max(0f, bounds.left - padPx).toDp() }
    val top = with(density) { max(0f, bounds.top - padPx).toDp() }
    val width = with(density) { (bounds.width + padPx * 2f).toDp() }
    val height = with(density) { (bounds.height + padPx * 2f).toDp() }

    Box(
        modifier = Modifier
            .padding(start = left, top = top)
            .size(width = width, height = height)
            .graphicsLayer {
                shadowElevation = 24.dp.toPx()
                shape = RoundedCornerShape(24.dp)
                clip = false
            }
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(24.dp)
            )
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                shape = RoundedCornerShape(24.dp)
            )
    )
}

@Composable
private fun CoachMarkCard(
    step: CoachMarkStep,
    index: Int,
    total: Int,
    bounds: Rect,
    onSkip: () -> Unit,
    onNext: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    val cardWidth = 292.dp
    val cardHeight = 220.dp
    val horizontalMargin = 20.dp
    val minXPx = with(density) { horizontalMargin.toPx() }
    val cardWidthPx = with(density) { cardWidth.toPx() }
    val cardHeightPx = with(density) { cardHeight.toPx() }
    val maxXPx = max(
        minXPx,
        screenWidthPx - cardWidthPx - with(density) { horizontalMargin.toPx() }
    )
    val offsetXPx = (bounds.center.x - cardWidthPx / 2f).coerceIn(minXPx, maxXPx)
    val verticalGapPx = with(density) { 18.dp.toPx() }
    val spaceBelowPx = screenHeightPx - bounds.bottom
    val spaceAbovePx = bounds.top
    val prefersAbove = bounds.center.y > screenHeightPx * 0.62f
    val showAbove = (prefersAbove && spaceAbovePx >= cardHeightPx + verticalGapPx) ||
        (spaceBelowPx < cardHeightPx + verticalGapPx && spaceAbovePx > spaceBelowPx)
    val preferredTopPx = if (showAbove) {
        bounds.top - cardHeightPx - verticalGapPx
    } else {
        bounds.bottom + verticalGapPx
    }
    val maxYPx = max(
        0f,
        screenHeightPx - cardHeightPx
    )
    val offsetYPx = preferredTopPx.coerceIn(0f, maxYPx)
    val pointerSize = 16.dp
    val pointerSizePx = with(density) { pointerSize.toPx() }
    val pointerCenterXPx = (bounds.center.x - offsetXPx).coerceIn(
        pointerSizePx * 1.5f,
        cardWidthPx - pointerSizePx * 1.5f
    )

    Box(
        modifier = modifier
            .padding(start = with(density) { offsetXPx.toDp() }, top = with(density) { offsetYPx.toDp() })
            .width(cardWidth)
    ) {
        if (!showAbove) {
            CoachMarkPointer(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset {
                        IntOffset(
                            x = (pointerCenterXPx - pointerSizePx / 2f).roundToInt(),
                            y = 0
                        )
                    },
                size = pointerSize
            )
        }

        Surface(
            modifier = Modifier.padding(
                top = if (showAbove) 0.dp else pointerSize / 2,
                bottom = if (showAbove) pointerSize / 2 else 0.dp
            ),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            shadowElevation = 14.dp
        ) {
            Column(
                modifier = Modifier
                    .width(cardWidth)
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "${index + 1}/$total",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    TextButton(onClick = onSkip) {
                        Text("Skip")
                    }
                }

                Text(
                    text = step.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = step.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = if (index == total - 1) onDone else onNext,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(if (index == total - 1) "Got it" else "Next")
                    }
                }
            }
        }

        if (showAbove) {
            CoachMarkPointer(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset {
                        IntOffset(
                            x = (pointerCenterXPx - pointerSizePx / 2f).roundToInt(),
                            y = 0
                        )
                    },
                size = pointerSize
            )
        }
    }
}

@Composable
private fun CoachMarkPointer(
    modifier: Modifier = Modifier,
    size: Dp
) {
    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                rotationZ = 45f
                shadowElevation = 10.dp.toPx()
                shape = RoundedCornerShape(5.dp)
                clip = false
            }
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(5.dp)
            )
    )
}

object HomeFeatureTourStore {
    private const val PREFS_NAME = "feature_tour_prefs"
    private const val KEY_HOME_TOUR_SEEN = "home_tour_seen"
    private const val KEY_HOME_TOUR_STARTED = "home_tour_started"

    fun shouldShow(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).let { prefs ->
            !prefs.getBoolean(KEY_HOME_TOUR_SEEN, false) &&
                !prefs.getBoolean(KEY_HOME_TOUR_STARTED, false)
        }

    fun hasStarted(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_HOME_TOUR_STARTED, false)

    fun markStarted(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_HOME_TOUR_STARTED, true)
            .apply()
    }

    fun markSeen(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_HOME_TOUR_SEEN, true)
            .apply()
    }
}
