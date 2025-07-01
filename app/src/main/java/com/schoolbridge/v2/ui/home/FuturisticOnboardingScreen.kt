package com.schoolbridge.v2.ui.home

// Futuristic, clean onboarding screen – fully standalone and preview‑ready
// Paste this into any Kotlin/Compose playground or your project and hit Preview.

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.schoolbridge.v2.ui.theme.SchoolBridgeV2Theme
import kotlinx.coroutines.launch

/*───────────────────────────────────────────────────────────────────────────────
  2.  Data model for each onboarding page
 ───────────────────────────────────────────────────────────────────────────────*/
private data class Page(val icon: ImageVector, val title: String, val subtitle: String)

private val pages = listOf(
    Page(Icons.Filled.School,   "Bridge Home & School", "Real‑time connection for parents, teachers and students."),
    Page(Icons.Filled.Chat,     "Instant Messaging",   "Stay informed with announcements and 1‑to‑1 chats."),
    Page(Icons.Filled.Security, "Secure & CBC‑Ready",  "Aligned with Rwanda's competence‑based curriculum.")
)

/*───────────────────────────────────────────────────────────────────────────────
  3.  Main composable
 ───────────────────────────────────────────────────────────────────────────────*/
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FuturisticOnboardingScreen(onFinished: () -> Unit = {}) {
    // Pager state
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    // Animated gradient offset for subtle movement
    val gradientShift by rememberInfiniteTransition().animateFloat(
        initialValue = 0f, targetValue = 400f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing))
    )

    SchoolBridgeV2Theme(isDarkTheme = false) {
        Box(Modifier.fillMaxSize())
        {
            /* Background moving radial/linear gradient */
            Box(
                Modifier
                    .fillMaxSize()
                    .rotate(30f)
                    .blur(200.dp)  // dreamy glow
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                                Color.Transparent
                            ),
                            start = Offset(gradientShift, 0f),
                            end   = Offset(0f, gradientShift)
                        )
                    )
            )

            // Pager with content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { index ->
                val page = pages[index]
                PageContent(page)
            }

            // Bottom controls (dots + action)
            Column(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                DotsIndicator(pages.size, pagerState.currentPage)
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (pagerState.currentPage == pages.lastIndex) onFinished()
                        else scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                ) {
                    Text(if (pagerState.currentPage == pages.lastIndex) "Get Started" else "Next")
                }
            }

            // skip tap zone
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(80.dp)
                    .pointerInput(Unit) { detectTapGestures { onFinished() } }
            )
        }
    }
    /*MaterialTheme(colorScheme = GlowColorScheme ) {

    }*/
}

/*───────────────────────────────────────────────────────────────────────────────
  4.  Page content composable
 ───────────────────────────────────────────────────────────────────────────────*/
@Composable
private fun PageContent(page: Page) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(page.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(92.dp))
        Spacer(Modifier.height(32.dp))
        Text(page.title, style = MaterialTheme.typography.headlineLarge, color = Color.White)
        Spacer(Modifier.height(12.dp))
        Text(page.subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground, lineHeight = 22.sp)
    }
}

/*───────────────────────────────────────────────────────────────────────────────
  5.  Dots indicator
 ───────────────────────────────────────────────────────────────────────────────*/
@Composable
private fun DotsIndicator(total: Int, current: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(total) { i ->
            val size = if (i == current) 12.dp else 8.dp
            val color = if (i == current) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            Box(Modifier.size(size).background(color, shape = MaterialTheme.shapes.small))
        }
    }
}

/*───────────────────────────────────────────────────────────────────────────────
  6. Preview
 ───────────────────────────────────────────────────────────────────────────────*/
@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun FuturisticOnboardingPreview() {
    FuturisticOnboardingScreen()
}
