package com.schoolbridge.v2.ui.home // Adjust package as needed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.schoolbridge.v2.R
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.domain.user.CurrentUser
import com.schoolbridge.v2.localization.t
import com.schoolbridge.v2.ui.common.components.AppSubHeader
import com.schoolbridge.v2.ui.common.components.SpacerL
import com.schoolbridge.v2.ui.common.components.SpacerS
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.*

/**
 * Entry point for the Home screen.
 * Observes [UserSessionManager] and delegates UI rendering to [HomeUI].
 *
 * @param userSessionManager Manages session and provides user data.
 * @param onSettingsClick Called when the settings icon is tapped.
 * @param modifier Modifier applied to the screen layout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    userSessionManager: UserSessionManager,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by userSessionManager.currentUser.collectAsStateWithLifecycle(initialValue = null)

    Scaffold(
        topBar = { HomeTopBar(onSettingsClick = onSettingsClick) },
        modifier = modifier
    ) { paddingValues ->
        HomeUI(
            currentUser = currentUser,
            onViewAllAlertsClick = { /* TODO: Navigate to all alerts */ },
            onViewAllEventsClick = { /* TODO: Navigate to all events */ },
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        )
    }
}

/**
 * Stateless content for the Home screen.
 * Displays students, alerts, and events based on [currentUser].
 *
 * @param currentUser Contains user and linked student info, nullable while loading.
 * @param onViewAllAlertsClick Callback for viewing all alerts.
 * @param onViewAllEventsClick Callback for viewing all events.
 * @param modifier Modifier applied to the layout.
 */
@Composable
private fun HomeUI(
    currentUser: CurrentUser?,
    onViewAllAlertsClick: () -> Unit,
    onViewAllEventsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StudentListSection(students = currentUser?.linkedStudents)
        SpacerL()
        AlertsSection(onViewAllAlertsClick = onViewAllAlertsClick)
        SpacerL()
        EventsSection(onViewAllEventsClick = onViewAllEventsClick)
    }
}

/**
 * Top App Bar for the Home screen.
 *
 * @param onSettingsClick Invoked when the settings icon is tapped.
 * @param modifier Modifier applied to the top bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text("SchoolBridge") },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings"
                )
            }
        },
        modifier = modifier
    )
}

/**
 * Displays horizontally scrollable student cards.
 *
 * @param students List of linked students, nullable.
 * @param modifier Modifier applied to the section.
 */
@Composable
private fun StudentListSection(
    students: List<CurrentUser.LinkedStudent>?,
    modifier: Modifier = Modifier
) {
    if (!students.isNullOrEmpty()) {
        Row(modifier = modifier.fillMaxWidth()) {
            AppSubHeader("📚 " + t(R.string.your_children))
        }
        SpacerS()
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            itemsIndexed(students) { _, student ->
                StudentCard(student = student)
            }
        }
    }
}

/**
 * Section for recent alerts.
 *
 * @param onViewAllAlertsClick Callback for the "View All" button.
 * @param modifier Modifier applied to the section.
 */
@Composable
private fun AlertsSection(
    onViewAllAlertsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppSubHeader("💬 " + t(R.string.recent_alerts))
        TextButton(onClick = onViewAllAlertsClick) {
            Text(text = "View All", style = MaterialTheme.typography.labelLarge)
        }
    }

    SpacerS()

    val alerts = listOf(t(R.string.alert_midterm_exams), t(R.string.alert_uniform_inspection))
    alerts.forEachIndexed { index, alert ->
        AlertCardCompact(message = alert, index = index)
    }
}

/**
 * Section for upcoming events.
 *
 * @param onViewAllEventsClick Callback for the "View All" button.
 * @param modifier Modifier applied to the section.
 */
@Composable
private fun EventsSection(
    onViewAllEventsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppSubHeader("📅 " + t(R.string.upcoming_events))
        TextButton(onClick = onViewAllEventsClick) {
            Text(text = "View All", style = MaterialTheme.typography.labelLarge)
        }
    }

    SpacerS()

    val events = listOf(
        Pair(t(R.string.event_meeting), "June 10"),
        Pair(t(R.string.event_sports_day), "June 20")
    )
    events.forEachIndexed { index, event ->
        EventCardCompact(title = event.first, date = event.second, index = index)
    }
}

/**
 * Compact card for a single alert.
 *
 * @param message Alert message to display.
 * @param index Used for staggered animation delay.
 * @param modifier Modifier applied to the card.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AlertCardCompact(message: String, index: Int, modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(1000)) + slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(1000, delayMillis = index * 100)
        )
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .fillMaxHeight()
                        .background(
                            MaterialTheme.colorScheme.error,
                            RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                        )
                )
                Text(
                    text = message,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

/**
 * Compact card for a single event.
 *
 * @param title Title of the event.
 * @param date Date of the event.
 * @param index Used for staggered animation delay.
 * @param modifier Modifier applied to the card.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EventCardCompact(title: String, date: String, index: Int, modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(1000)) + slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(1000, delayMillis = index * 100)
        )
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .fillMaxHeight()
                        .background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                        )
                )
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = date,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

/**
 * A card Composable for displaying a linked student's profile information in a compact style.
 *
 * @param student The [CurrentUser.LinkedStudent] object to display.
 * @param modifier The modifier to be applied to the card.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StudentCard(student: CurrentUser.LinkedStudent, modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(durationMillis = 1000)) +
                slideInHorizontally(animationSpec = tween(durationMillis = 1000))
    ) {
        Card(
            modifier = modifier
                .width(240.dp)
                .height(280.dp)
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // Vertical accent bar
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(12.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )

                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = student.firstName.firstOrNull()?.toString() ?: "",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = student.firstName + " " + student.lastName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Excella high school",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "S5 - MCB",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
