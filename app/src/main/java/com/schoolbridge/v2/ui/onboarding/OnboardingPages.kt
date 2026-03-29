package com.schoolbridge.v2.ui.onboarding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.ui.graphics.vector.ImageVector

val onboardingPages = listOf(
    OnboardingPage(
        title = "School life, in one calm place",
        subtitle = "SchoolBridge brings messages, schedules, alerts, and school follow-up into one trusted space so families and staff do not have to jump between tools.",
        icon = Icons.Filled.School,
        highlights = listOf(
            OnboardingHighlight("Messages", "Receive school conversations and decisions", Icons.AutoMirrored.Filled.Chat),
            OnboardingHighlight("Timetable", "See classes, meetings, and personal plans", Icons.Filled.CalendarMonth),
            OnboardingHighlight("Finance", "Track fee follow-up without confusion", Icons.Filled.Payments)
        ),
        supportingNote = "Designed for everyday school coordination, not open social chat."
    ),
    OnboardingPage(
        title = "Know what needs attention",
        subtitle = "The app helps you notice what matters first: unread school conversations, upcoming moments, account verification, and actions that still need your response.",
        icon = Icons.Filled.AutoGraph,
        highlights = listOf(
            OnboardingHighlight("Action flow", "Confirm, acknowledge, pay, or join directly", Icons.Filled.Badge),
            OnboardingHighlight("Trust", "Role-aware access keeps school data contextual", Icons.Filled.Security),
            OnboardingHighlight("Clarity", "Updates stay attached to the right conversation", Icons.Filled.AutoGraph)
        ),
        supportingNote = "Useful for parents, teachers, school admins, and learners with different needs."
    ),
    OnboardingPage(
        title = "Set up your account once",
        subtitle = "After this introduction, you can sign in, verify your number, choose the roles that fit you, and link children when needed so the right school information appears from the start.",
        icon = Icons.Filled.FamilyRestroom,
        highlights = listOf(
            OnboardingHighlight("Sign in securely", "Use your account and verify your identity", Icons.Filled.Security),
            OnboardingHighlight("Choose your role", "Parent, teacher, learner, or school admin", Icons.Filled.Badge),
            OnboardingHighlight("Link family access", "Attach the right child and school context", Icons.Filled.FamilyRestroom)
        ),
        supportingNote = "You can refine your profile later, but the first setup is enough to get started."
    )
)

data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val highlights: List<OnboardingHighlight>,
    val supportingNote: String
)

data class OnboardingHighlight(
    val label: String,
    val description: String,
    val icon: ImageVector
)
