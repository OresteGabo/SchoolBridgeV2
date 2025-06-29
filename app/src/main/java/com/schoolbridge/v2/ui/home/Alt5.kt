package com.schoolbridge.v2.ui.home
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import kotlin.math.*
import androidx.compose.ui.platform.LocalDensity
// ADD THIS IMPORT
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize


// Re-using dummy data classes from previous examples
// data class _CurrentUser(val lastName: String = "Smith", val gender: _Gender = _Gender.MALE)
// enum class _Gender { MALE, FEMALE, OTHER }
// data class _Course(val name: String, val iconResId: Int, val progress: Float)
// data class _Alt3_CourseCategory(val name: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)


// Fictional Interface Colors - Ethereal, glowing, deep tones
val NeuralPrimaryGlow = Color(0xFF8B00FF) // Deep Violet
val NeuralSecondaryGlow = Color(0xFF00C0FF) // Bright Cyan
val NeuralTertiaryHighlight = Color(0xFFE0FF00) // Lime Yellow
val NeuralBackgroundDark = Color(0xFF0A001F) // Very Dark Purple-Blue
val NeuralBackgroundLight = Color(0xFF1F003F) // Slightly lighter Purple-Blue
val NeuralTextPrimary = Color.White
val NeuralTextSecondary = Color.LightGray

// Custom Theme for the Neural Interface
private val NeuralColorScheme = darkColorScheme(
    primary = NeuralPrimaryGlow,
    onPrimary = NeuralTextPrimary,
    secondary = NeuralSecondaryGlow,
    onSecondary = NeuralTextPrimary,
    tertiary = NeuralTertiaryHighlight,
    onTertiary = NeuralBackgroundDark,
    background = NeuralBackgroundDark,
    onBackground = NeuralTextSecondary,
    surface = NeuralBackgroundLight,
    onSurface = NeuralTextPrimary,
    error = Color(0xFFFF5252),
    onError = Color.White
)

val NeuralTypography = Typography(
    displayLarge = TextStyle( // For the central focus element
        fontWeight = FontWeight.Light,
        fontSize = 64.sp,
        color = NeuralTextPrimary,
        letterSpacing = 2.sp
    ),
    headlineMedium = TextStyle( // For primary node labels
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        color = NeuralTextPrimary
    ),
    bodyMedium = TextStyle( // For sub-labels or descriptions
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = NeuralTextSecondary
    ),
    labelSmall = TextStyle( // For very subtle information
        fontWeight = FontWeight.Thin,
        fontSize = 10.sp,
        color = NeuralTextSecondary.copy(alpha = 0.7f)
    )
)

// A simple custom shape, but we might mostly draw directly
val NeuralShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.CircleShape,
    small = androidx.compose.foundation.shape.CircleShape,
    medium = androidx.compose.foundation.shape.CircleShape,
    large = androidx.compose.foundation.shape.CircleShape
)

val FontWeightSemiBold = FontWeight(600) // Define if not already in your project


@Composable
fun NeuralTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NeuralColorScheme,
        typography = NeuralTypography,
        shapes = NeuralShapes,
        content = content
    )
}

// --- Main Neural Interface Composable ---
@OptIn(ExperimentalTextApi::class, ExperimentalMaterial3Api::class)
@Composable
fun _Neural_HarmonicLearningInterface(currentUser: _CurrentUser = _CurrentUser()) {
    val infiniteTransition = rememberInfiniteTransition(label = "backgroundTransition")
    val currentPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "currentPulseAlpha"
    )
    val currentPulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "currentPulseScale"
    )

    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    // State to hold the size of the main Box
    var boxSize by remember { mutableStateOf(IntSize.Zero) }
    val color1 = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f * currentPulseAlpha)
    val color2 = MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f * currentPulseAlpha)

    NeuralTheme {
        Scaffold(
            // Keeping Scaffold for basic structure, but it could conceptually disappear
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background)
                        // Capture the size of the Box after layout
                        .onSizeChanged { newSize ->
                            boxSize = newSize
                        }
                ) {
                    // Adaptive Background Glow - subtle pulsing
                    // Make sure boxSize is not Zero before drawing
                    if (boxSize != IntSize.Zero) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val center = Offset(boxSize.width / 2f, boxSize.height / 2f)
                            val maxRadius = boxSize.width / 2f

                            // Outer, subtle glow
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        color1,
                                        Color.Transparent
                                    ),
                                    center = center,
                                    radius = maxRadius * currentPulseScale
                                ),
                                radius = maxRadius * currentPulseScale,
                                center = center
                            )

                            // Inner, more focused glow
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        color2,
                                        Color.Transparent
                                    ),
                                    center = center,
                                    radius = maxRadius * 0.7f * currentPulseScale
                                ),
                                radius = maxRadius * 0.7f * currentPulseScale,
                                center = center
                            )
                        }
                    }


                    // Central "Consciousness Core"
                    _Neural_CentralCore(
                        currentUser = currentUser,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    // Orbital Nodes for Key Information
                    val nodes = remember {
                        listOf(
                            NeuralNode(id = "current_course", label = "Current Path", value = "Quantum Computing", type = NeuralNodeType.Course),
                            NeuralNode(id = "next_task", label = "Next Integration", value = "Neural Networks I", type = NeuralNodeType.Task),
                            NeuralNode(id = "progress_overview", label = "Cognitive Flow", value = "92%", type = NeuralNodeType.Progress),
                            NeuralNode(id = "new_insights", label = "New Insights", value = "3", type = NeuralNodeType.Alert),
                        )
                    }

                    // Only place nodes if boxSize is available
                    if (boxSize != IntSize.Zero) {
                        val radius1 = with(density) { 150.dp.toPx() } // First orbit radius
                        val radius2 = with(density) { 250.dp.toPx() } // Second orbit radius
                        val centerPx = Offset(boxSize.width / 2f, boxSize.height / 2f)

                        // Node size in pixels (once, at the top)
                        val nodeSizePx = with(density) { 50.dp.toPx() } // Assuming base node size is 50.dp

                        // Dynamic Node Placement (simple example, could be more complex with physics simulations)
                        nodes.forEachIndexed { index, node ->
                            val angle = (index * (360f / nodes.size)) + (currentPulseScale * 10) // Small rotation effect
                            val x = centerPx.x + (if (index % 2 == 0) radius1 else radius2) * cos(Math.toRadians(angle.toDouble())).toFloat()
                            val y = centerPx.y + (if (index % 2 == 0) radius1 else radius2) * sin(Math.toRadians(angle.toDouble())).toFloat()

                            _Neural_OrbitalNode(
                                node = node,
                                textMeasurer = textMeasurer,
                                modifier = Modifier
                                    .offset(
                                        x = with(density) { (x - (nodeSizePx / 2f)).toDp() },
                                        y = with(density) { (y - (nodeSizePx / 2f)).toDp() }
                                    )
                            )
                        }
                    }
                }
            }
        )
    }
}

// Data classes for Neural Interface Nodes
enum class NeuralNodeType {
    Course, Task, Progress, Alert, General
}

data class NeuralNode(
    val id: String,
    val label: String,
    val value: String,
    val type: NeuralNodeType,
    var isExpanded: Boolean = false
)

// --- Central Consciousness Core ---
@Composable
private fun _Neural_CentralCore(currentUser: _CurrentUser, modifier: Modifier = Modifier) {
    val pulseAnim by rememberInfiniteTransition(label = "corePulse").animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "corePulse"
    )

    Column(
        modifier = modifier
            .size(160.dp * pulseAnim) // Breathing effect
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        Color.Transparent
                    )
                ),
                shape = MaterialTheme.shapes.medium // CircleShape
            )
            .wrapContentSize(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Consciousness Core",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
        )
        Text(
            text = "${currentUser.lastName}'s",
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 32.sp),
            color = MaterialTheme.colorScheme.onPrimary,
            maxLines = 1
        )
        Text(
            text = "Harmonic Flow",
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 18.sp),
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
            maxLines = 1
        )
    }
}

// --- Orbital Node ---
@OptIn(ExperimentalTextApi::class)
@Composable
private fun _Neural_OrbitalNode(
    node: NeuralNode,
    textMeasurer: TextMeasurer,
    modifier: Modifier = Modifier
) {
    var isTapped by remember { mutableStateOf(false) }
    val tapScale by animateFloatAsState(
        targetValue = if (isTapped) 1.2f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "tapScale"
    )
    val tapAlpha by animateFloatAsState(
        targetValue = if (isTapped) 1.0f else 0.8f,
        animationSpec = tween(durationMillis = 300), label = "tapAlpha"
    )

    val nodeColor = when (node.type) {
        NeuralNodeType.Course -> MaterialTheme.colorScheme.primary.copy(alpha = tapAlpha)
        NeuralNodeType.Task -> MaterialTheme.colorScheme.secondary.copy(alpha = tapAlpha)
        NeuralNodeType.Progress -> MaterialTheme.colorScheme.tertiary.copy(alpha = tapAlpha)
        NeuralNodeType.Alert -> MaterialTheme.colorScheme.error.copy(alpha = tapAlpha)
        NeuralNodeType.General -> MaterialTheme.colorScheme.surface.copy(alpha = tapAlpha)
    }

    // This Composbale represents the node and its text.
    // Tapping it will toggle expansion (conceptually, in a real app, it would navigate)
    Column(
        modifier = modifier
            .size(if (isTapped) 80.dp else 50.dp) // Size changes on tap
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isTapped = true
                        this.awaitRelease()
                        isTapped = false
                        // In a real app, this would trigger navigation or data expansion
                        node.isExpanded = !node.isExpanded // Toggle state for conceptual expansion
                    }
                )
            }
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(nodeColor, nodeColor.copy(alpha = 0.3f), Color.Transparent)
                ),
                shape = MaterialTheme.shapes.small // CircleShape
            )
            .scale(tapScale), // Apply scale animation
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (node.isExpanded) {
            // Show more detail when expanded
            Text(
                text = node.label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                softWrap = false
            )
            Text(
                text = node.value,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                softWrap = false
            )
        } else {
            // Show only main value when collapsed
            Text(
                text = node.value,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}


// --- Preview ---
@Preview(showBackground = true, widthDp = 360, heightDp = 700)
@Composable
fun _Neural_HarmonicLearningInterfacePreview() {
    _Neural_HarmonicLearningInterface(currentUser = _CurrentUser(lastName = "Elara", gender = _Gender.FEMALE))
}
