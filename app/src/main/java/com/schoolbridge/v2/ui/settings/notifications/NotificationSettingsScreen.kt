package com.schoolbridge.v2.ui.settings.notifications

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.ui.settings.SettingOption
import kotlinx.coroutines.delay

@Preview
@Composable
private fun Notifications() {
    NotificationSettingsScreen(onBack = {})
}

// --- NotificationSettingsScreen Composable with Animations ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    // State for main notification toggles (if they still apply to non-push categories)
    var messageAlertsEnabled by remember { mutableStateOf(true) }
    var financeAlertsEnabled by remember { mutableStateOf(true) }
    var threadRepliesEnabled by remember { mutableStateOf(true) }
    var appAnnouncementsEnabled by remember { mutableStateOf(true) }
    var emailSummariesEnabled by remember { mutableStateOf(false) }
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(true) }

    // New states for categorized Push Notifications
    var criticalAppAlertsEnabled by remember { mutableStateOf(true) } // Renamed for clarity
    var securityAlertsEnabled by remember { mutableStateOf(true) }

    // New states for School-specific Important Push Notifications
    var studentStatusAlertsEnabled by remember { mutableStateOf(true) }
    var disciplinaryAlertsEnabled by remember { mutableStateOf(true) }
    var financialSchoolAlertsEnabled by remember { mutableStateOf(true) }
    var mandatoryMeetingRemindersEnabled by remember { mutableStateOf(true) }

    var marketingPromotionsEnabled by remember { mutableStateOf(false) }
    var personalizedRecommendationsEnabled by remember { mutableStateOf(true) }

    // State to control the visibility of each section for staggered animation
    val sectionVisibility = remember { mutableStateOf(MutableList(10) { false }) } // Number of animated sections

    LaunchedEffect(Unit) {
        val delayStep = 80L // Small delay for light staggering
        // Animate each logical section of the settings
        for (i in sectionVisibility.value.indices) {
            delay(delayStep)
            sectionVisibility.value = sectionVisibility.value.toMutableList().also {
                it[i] = true
            }
        }
    }

    BackHandler {
        // Handle back press, e.g., navigateUp() in a NavController
        println("Back pressed from Notification Settings")
        onBack() // Ensure onBack is called
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notification Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // Categorized Push Notifications Section
            AnimatedVisibility(
                visible = sectionVisibility.value[0],
                enter = fadeIn(animationSpec = tween(durationMillis = 200)) + slideInVertically(initialOffsetY = { it / 5 }, animationSpec = tween(durationMillis = 200))
            ) {
                Column { // Group elements for animation
                    SettingsSectionHeader("Push Notifications")
                    Text(
                        text = "Choose which types of push notifications you'd like to receive.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }


            // Crucial / Important App-wide Push Notifications
            AnimatedVisibility(
                visible = sectionVisibility.value[1],
                enter = fadeIn(animationSpec = tween(durationMillis = 200)) + slideInVertically(initialOffsetY = { it / 5 }, animationSpec = tween(durationMillis = 200))
            ) {
                Column { // Group elements for animation
                    SettingsSubSectionHeader("General Important Alerts")
                    SettingsToggleItem(
                        title = "Critical System Alerts",
                        description = "Urgent updates about your account or service availability.",
                        checked = criticalAppAlertsEnabled,
                        onCheckedChange = { criticalAppAlertsEnabled = it }
                    )
                    SettingsToggleItem(
                        title = "Security Notifications",
                        description = "Login attempts, password changes, or suspicious activity.",
                        checked = securityAlertsEnabled,
                        onCheckedChange = { securityAlertsEnabled = it }
                    )
                    HorizontalDivider()
                }
            }


            // School-Specific Important Push Notifications
            AnimatedVisibility(
                visible = sectionVisibility.value[2],
                enter = fadeIn(animationSpec = tween(durationMillis = 200)) + slideInVertically(initialOffsetY = { it / 5 }, animationSpec = tween(durationMillis = 200))
            ) {
                Column { // Group elements for animation
                    SettingsSubSectionHeader("Important School Alerts")
                    SettingsToggleItem(
                        title = "Student Status Updates",
                        description = "Absence, sickness, or immediate well-being concerns.",
                        checked = studentStatusAlertsEnabled,
                        onCheckedChange = { studentStatusAlertsEnabled = it }
                    )
                    SettingsToggleItem(
                        title = "Disciplinary Actions & Fines",
                        description = "Information regarding student conduct, suspensions, or financial penalties.",
                        checked = disciplinaryAlertsEnabled,
                        onCheckedChange = { disciplinaryAlertsEnabled = it }
                    )
                    SettingsToggleItem(
                        title = "School Financial Alerts",
                        description = "Payment reminders, overdue fees, or funding opportunities.",
                        checked = financialSchoolAlertsEnabled,
                        onCheckedChange = { financialSchoolAlertsEnabled = it }
                    )
                    SettingsToggleItem(
                        title = "Mandatory Meetings & Events",
                        description = "Crucial reminders for parent-teacher conferences or required school events.",
                        checked = mandatoryMeetingRemindersEnabled,
                        onCheckedChange = { mandatoryMeetingRemindersEnabled = it }
                    )
                    HorizontalDivider()
                }
            }


            // Less Important / Promotional Push Notifications
            AnimatedVisibility(
                visible = sectionVisibility.value[3],
                enter = fadeIn(animationSpec = tween(durationMillis = 200)) + slideInVertically(initialOffsetY = { it / 5 }, animationSpec = tween(durationMillis = 200))
            ) {
                Column { // Group elements for animation
                    SettingsSubSectionHeader("Promotional & Personalized")
                    SettingsToggleItem(
                        title = "Marketing & Promotions",
                        description = "Special offers, new features, or app news.",
                        checked = marketingPromotionsEnabled,
                        onCheckedChange = { marketingPromotionsEnabled = it }
                    )
                    SettingsToggleItem(
                        title = "Personalized Recommendations",
                        description = "Suggestions tailored to your activity.",
                        checked = personalizedRecommendationsEnabled,
                        onCheckedChange = { personalizedRecommendationsEnabled = it }
                    )
                    HorizontalDivider()
                }
            }


            // In-App Notifications Section
            AnimatedVisibility(
                visible = sectionVisibility.value[4],
                enter = fadeIn(animationSpec = tween(durationMillis = 200)) + slideInVertically(initialOffsetY = { it / 5 }, animationSpec = tween(durationMillis = 200))
            ) {
                Column { // Group elements for animation
                    SettingsSectionHeader("In-App Notifications")
                    SettingsToggleItem(
                        title = "Messages",
                        checked = messageAlertsEnabled,
                        onCheckedChange = { messageAlertsEnabled = it }
                    )
                    SettingsToggleItem(
                        title = "Finance Updates",
                        checked = financeAlertsEnabled,
                        onCheckedChange = { financeAlertsEnabled = it }
                    )
                    SettingsToggleItem(
                        title = "Replies in Threads",
                        checked = threadRepliesEnabled,
                        onCheckedChange = { threadRepliesEnabled = it }
                    )
                    SettingsToggleItem(
                        title = "School Announcements", // This could also be a push, but assuming it's in-app here
                        checked = appAnnouncementsEnabled,
                        onCheckedChange = { appAnnouncementsEnabled = it }
                    )
                    HorizontalDivider()
                }
            }


            // Email Notifications Section
            AnimatedVisibility(
                visible = sectionVisibility.value[5],
                enter = fadeIn(animationSpec = tween(durationMillis = 200)) + slideInVertically(initialOffsetY = { it / 5 }, animationSpec = tween(durationMillis = 200))
            ) {
                Column { // Group elements for animation
                    SettingsSectionHeader("Email Notifications")
                    SettingsToggleItem(
                        title = "Receive Weekly Summary Emails",
                        checked = emailSummariesEnabled,
                        onCheckedChange = { emailSummariesEnabled = it }
                    )
                    HorizontalDivider()
                }
            }


            // Sound & Vibration Section
            AnimatedVisibility(
                visible = sectionVisibility.value[6],
                enter = fadeIn(animationSpec = tween(durationMillis = 200)) + slideInVertically(initialOffsetY = { it / 5 }, animationSpec = tween(durationMillis = 200))
            ) {
                Column { // Group elements for animation
                    SettingsSectionHeader("Sound & Vibration")
                    SettingsToggleItem(
                        title = "Enable Notification Sounds",
                        checked = soundEnabled,
                        onCheckedChange = { soundEnabled = it }
                    )
                    SettingsToggleItem(
                        title = "Enable Vibration",
                        checked = vibrationEnabled,
                        onCheckedChange = { vibrationEnabled = it }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsSubSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant, // A slightly subdued color
        modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 4.dp)
    )
}

@Composable
fun SettingsToggleItem(
    title: String,
    description: String? = null, // Optional description
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = {
            if (description != null) {
                Text(description, style = MaterialTheme.typography.bodySmall)
            }
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
    )
}