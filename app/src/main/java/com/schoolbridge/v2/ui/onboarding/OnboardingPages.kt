// ui/onboarding/OnboardingPages.kt
package com.schoolbridge.v2.ui.onboarding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.Chat
import androidx.compose.ui.graphics.vector.ImageVector

val onboardingPages = listOf(
    OnboardingPage(
        title = "Welcome to SchoolBridge",
        subtitle = "Your companion for Rwanda’s \ncompetence‑based curriculum.",
        icon = Icons.Default.School
    ),
    OnboardingPage(
        title = "Stats that matter",
        subtitle = "Track attendance, marks & CBC skills.\n86 % of Rwandan schools now report digitally.",
        icon = Icons.Default.DataUsage
    ),
    OnboardingPage(
        title = "Stay connected",
        subtitle = "Chat with teachers, receive alerts,\n& pay fees— all in one place.",
        icon = Icons.Default.Chat
    ),
)


/**
 * A single onboarding screen descriptor.
 */
data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
)