package com.schoolbridge.v2.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.ui.theme.AppPalette
import com.schoolbridge.v2.ui.theme.Contrast
import com.schoolbridge.v2.ui.theme.SchoolBridgeV2Theme
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit
) {
    val pages = onboardingPages
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val page = pages[pagerState.currentPage]
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        colorScheme.background,
                        colorScheme.surfaceContainerLowest,
                        colorScheme.surfaceContainerLow
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(330.dp)
                .clip(
                    RoundedCornerShape(
                        bottomStart = 36.dp,
                        bottomEnd = 36.dp
                    )
                )
                .background(
                    Brush.linearGradient(
                        listOf(
                            colorScheme.primaryContainer,
                            colorScheme.secondaryContainer,
                            colorScheme.tertiaryContainer
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            OnboardingTopBar(
                currentPage = pagerState.currentPage,
                totalPages = pages.size,
                onSkip = onFinished
            )

            Spacer(Modifier.height(18.dp))

            AnimatedContent(
                targetState = pagerState.currentPage,
                label = "OnboardingHero"
            ) { currentPage ->
                HeroHeader(
                    page = pages[currentPage],
                    index = currentPage + 1,
                    total = pages.size
                )
            }

            Spacer(Modifier.height(20.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { pageIndex ->
                InsightPanel(page = pages[pageIndex])
            }

            Spacer(Modifier.height(18.dp))

            StepRail(
                pages = pages,
                selectedIndex = pagerState.currentPage
            )

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(0.9f),
                    shape = RoundedCornerShape(22.dp),
                    color = colorScheme.surfaceContainerHigh
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (pagerState.currentPage == pages.lastIndex) {
                                "Ready to begin"
                            } else {
                                "Up next"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (pagerState.currentPage == pages.lastIndex) {
                                "Sign in and finish your school setup inside the app."
                            } else {
                                pages[pagerState.currentPage + 1].title
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }

                Button(
                    onClick = {
                        if (pagerState.currentPage == pages.lastIndex) {
                            onFinished()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier.weight(1.1f),
                    shape = RoundedCornerShape(22.dp),
                    contentPadding = PaddingValues(vertical = 18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = if (pagerState.currentPage == pages.lastIndex) "Enter App" else "Continue",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingTopBar(
    currentPage: Int,
    totalPages: Int,
    onSkip: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(999.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ) {
            Text(
                text = "Welcome ${currentPage + 1}/$totalPages",
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        TextButton(onClick = onSkip) {
            Text("Skip")
        }
    }
}

@Composable
private fun HeroHeader(
    page: OnboardingPage,
    index: Int,
    total: Int
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(84.dp),
            shape = RoundedCornerShape(28.dp),
            color = colorScheme.surface.copy(alpha = 0.22f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = page.icon,
                    contentDescription = null,
                    tint = colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "SchoolBridge",
                style = MaterialTheme.typography.labelLarge,
                color = colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineMedium,
                color = colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = page.subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = colorScheme.onPrimaryContainer.copy(alpha = 0.86f)
            )
            Text(
                text = "Step $index of $total",
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onPrimaryContainer.copy(alpha = 0.72f)
            )
        }
    }
}

@Composable
private fun InsightPanel(
    page: OnboardingPage
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ElevatedCard(
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = colorScheme.surface
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "What you can do here",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                page.highlights.forEachIndexed { index, highlight ->
                    OnboardingInsightRow(
                        order = index + 1,
                        highlight = highlight
                    )
                }
            }
        }

        Surface(
            shape = RoundedCornerShape(28.dp),
            color = colorScheme.secondaryContainer.copy(alpha = 0.62f)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Professional first setup",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSecondaryContainer
                )
                Text(
                    text = page.supportingNote,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSecondaryContainer.copy(alpha = 0.88f)
                )
            }
        }
    }
}

@Composable
private fun OnboardingInsightRow(
    order: Int,
    highlight: OnboardingHighlight
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(38.dp),
            shape = CircleShape,
            color = colorScheme.primary.copy(alpha = 0.12f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = order.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = highlight.icon,
                    contentDescription = null,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = highlight.label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = highlight.description,
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StepRail(
    pages: List<OnboardingPage>,
    selectedIndex: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        pages.forEachIndexed { index, page ->
            val isSelected = index == selectedIndex
            val containerColor by animateColorAsState(
                targetValue = if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHigh
                },
                animationSpec = tween(250),
                label = "stepRailColor"
            )
            val contentColor by animateColorAsState(
                targetValue = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                animationSpec = tween(250),
                label = "stepRailContentColor"
            )

            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                color = containerColor
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.labelLarge,
                        color = contentColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = page.title,
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun OnboardingScreenPreview() {
    SchoolBridgeV2Theme(
        isDarkTheme = false,
        palette = AppPalette.GOLDEN,
        contrast = Contrast.NORMAL,
        dynamicColor = false
    ) {
        OnboardingScreen(onFinished = {})
    }
}
