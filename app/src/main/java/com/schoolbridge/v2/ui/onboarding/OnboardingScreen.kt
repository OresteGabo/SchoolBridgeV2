// ui/onboarding/OnboardingScreen.kt
package com.schoolbridge.v2.ui.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

//@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinished: @Composable () -> Unit,
) {
    val pagerState = rememberPagerState(
        pageCount = { onboardingPages.size }
    )
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        /* ── Pulsing glow reused from NeuralTheme ── */
        val pulse by rememberInfiniteTransition(label = "glow")
            .animateFloat(
                0.8f, 1.1f,
                animationSpec = infiniteRepeatable(
                    tween(3500, easing = LinearEasing), RepeatMode.Reverse
                ),
                label = "pulse"
            )
        Box(
            Modifier
                .fillMaxSize()
                .blur(180.dp)
                .background(
                    Brush.radialGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
                .graphicsLayer { scaleX = pulse; scaleY = pulse }
        )

        /* ── Pager content ── */
        HorizontalPager(
            //pageCount = onboardingPages.size,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val item = onboardingPages[page]
            PageContent(item = item)
        }

        /* ── Dots + action button ── */
        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DotsIndicator(
                totalDots = onboardingPages.size,
                selectedIndex = pagerState.currentPage
            )
            Spacer(Modifier.height(24.dp))

            ElevatedButton(
                onClick = {
                    if (pagerState.currentPage == onboardingPages.lastIndex) {
                        //onFinished()
                    } else {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                }
            ) {
                Text(if (pagerState.currentPage == onboardingPages.lastIndex) "Get started" else "Next")
            }
        }

        /* ── Skip zone (tap top‑right) ── */
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(80.dp)                // Large invisible hit box
                .pointerInput(Unit) {
                    detectTapGestures {
                        //onFinished()
                    }
                }
        )
    }
}

@Composable
private fun PageContent(item: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            item.icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(96.dp)
        )
        Spacer(Modifier.height(32.dp))
        Text(
            item.title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(16.dp))
        Text(
            item.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            lineHeight = 20.sp,
        )
    }
}

@Composable
private fun DotsIndicator(totalDots: Int, selectedIndex: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(totalDots) { index ->
            val color =
                if (index == selectedIndex) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            Box(
                Modifier
                    .size(if (index == selectedIndex) 12.dp else 8.dp)
                    .background(color, shape = MaterialTheme.shapes.small)
            )
        }
    }
}


@Preview(showBackground = true, widthDp = 360, heightDp = 700)
@Composable
fun OnboardingScreenPreview() {
    OnboardingScreen(onFinished = {})
}