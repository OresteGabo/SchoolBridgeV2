package com.schoolbridge.v2.ui.home // Adjust package as needed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.schoolbridge.v2.R
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.domain.user.CurrentUser
import com.schoolbridge.v2.localization.t
import com.schoolbridge.v2.ui.common.components.AppSubHeader
import com.schoolbridge.v2.ui.common.components.SpacerL
import com.schoolbridge.v2.ui.common.components.SpacerS
import com.schoolbridge.v2.ui.event.Event
import com.schoolbridge.v2.ui.event.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime // Import LocalDateTime
import java.time.format.DateTimeFormatter // Import DateTimeFormatter

// --- Dummy Data Classes and Helper Functions (as provided in previous responses) ---

data class UserEventStatus(
    val eventId: String,
    val isConfirmed: Boolean? // Null means not responded, true for confirmed, false for declined
)

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
    var expanded by remember { mutableStateOf(false) }
    val maxInitialAlerts = 3 // Define how many alerts to show initially

    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300), label = "rotationAnimation"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(durationMillis = 300))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppSubHeader("💬 " + t( R.string.recent_alerts))
            TextButton(onClick = onViewAllAlertsClick) {
                Text(text = t( R.string.view_all), style = MaterialTheme.typography.labelLarge)
            }
        }

        SpacerS()

        // List of all alert resource IDs
        val alertIds = remember {
            listOf(
                R.string.alert_midterm_exams,
                R.string.alert_uniform_inspection,
                R.string.alert_fee_deadline,
                R.string.alert_health_check,
                R.string.alert_visitor_day,
                R.string.alert_sanitation_day,
                R.string.alert_lost_item,
                R.string.alert_results_released,
                R.string.alert_meal_schedule_update,
                R.string.alert_power_cut,
                R.string.alert_student_award,
                R.string.alert_holiday_transport,
                R.string.alert_missing_assignments,
                R.string.alert_emergency_drill,
                R.string.alert_library_books_due,
                R.string.alert_homework_reminder,
                R.string.alert_sports_tournament,
                R.string.alert_health_precautions,
                R.string.alert_weather_warning,
                R.string.alert_room_change,
                R.string.alert_disciplinary_meeting,
                R.string.alert_id_card_collection,
                R.string.alert_special_meal_day,
                R.string.alert_community_service,
                R.string.alert_club_signup
            )
        }

        // Determine which alerts to show based on the expanded state
        val alertsToShow = if (expanded) {
            alertIds
        } else {
            alertIds.take(maxInitialAlerts)
        }

        alertsToShow.forEachIndexed { index, id ->
            AlertCardCompact(
                message = t( id),
                index = index
            )
        }

        // Show "Show More" button only if there are more alerts to display
        if (alertIds.size > maxInitialAlerts) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (expanded) t(R.string.show_less) else t(R.string.show_more),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = Icons.Filled.ExpandMore,
                    contentDescription = if (expanded) "Show less alerts" else "Show more alerts",
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .rotate(rotationState)
                )
            }
        }
    }
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
// --- HomeRoute: Now accepts onEventClick callback ---

/**
 * Entry point for the Home screen.
 * Observes [UserSessionManager] and delegates UI rendering to [HomeUI].
 *
 * @param userSessionManager Manages session and provides user data.
 * @param onSettingsClick Called when the settings icon is tapped.
 * @param onEventClick Called when an event card is tapped, passes the eventId.
 * @param modifier Modifier applied to the screen layout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    userSessionManager: UserSessionManager,
    onSettingsClick: () -> Unit,
    onViewAllAlertsClick: () -> Unit,
    onViewAllEventsClick: () -> Unit,
    onEventClick: (String) -> Unit, // New callback for event clicks
    modifier: Modifier = Modifier
) {
    val currentUser by userSessionManager.currentUser.collectAsStateWithLifecycle(initialValue = null)

    Scaffold(
        topBar = { HomeTopBar(onSettingsClick = onSettingsClick) },
        modifier = modifier
    ) { paddingValues ->
        HomeUI(
            currentUser = currentUser,
            onViewAllAlertsClick =onViewAllAlertsClick,
            onViewAllEventsClick = onViewAllEventsClick,
            onEventClick = onEventClick, // Pass the new callback down
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        )
    }
}

// --- HomeUI: Now accepts onEventClick callback ---

/**
 * Stateless content for the Home screen.
 * Displays students, alerts, and events based on [currentUser].
 *
 * @param currentUser Contains user and linked student info, nullable while loading.
 * @param onViewAllAlertsClick Callback for viewing all alerts.
 * @param onViewAllEventsClick Callback for viewing all events.
 * @param onEventClick Callback when an individual event card is clicked.
 * @param modifier Modifier applied to the layout.
 */
@Composable
private fun HomeUI(
    currentUser: CurrentUser?,
    onViewAllAlertsClick: () -> Unit,
    onViewAllEventsClick: () -> Unit,
    onEventClick: (String) -> Unit, // New callback
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
        EventsSection(
            onViewAllEventsClick = onViewAllEventsClick,
            onEventClick = onEventClick
        )
    }
}

// --- EventsSection: Now receives onEventClick and passes Event objects ---

/**
 * Section for upcoming events.
 *
 * @param onViewAllEventsClick Callback for the "View All" button.
 * @param onEventClick Callback when an individual event card is clicked.
 * @param modifier Modifier applied to the section.
 */
@Composable
private fun EventsSection(
    onViewAllEventsClick: () -> Unit,
    onEventClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val maxInitialEvents = 3 // Define how many events to show initially

    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300), label = "rotationAnimation"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(durationMillis = 300))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppSubHeader("📅 " + t( R.string.upcoming_events))
            TextButton(onClick = onViewAllEventsClick) {
                Text(text = t(R.string.view_all), style = MaterialTheme.typography.labelLarge)
            }
        }

        SpacerS()

        // Use the actual EventRepository to get events
        val eventRepository = remember { EventRepository() } // Or inject via Hilt/DI
        val events = remember { eventRepository.getUpcomingEvents() } // Get the list of Event objects

        if (events.isEmpty()) {
            Text(t(R.string.no_upcoming_events), style = MaterialTheme.typography.bodyMedium)
        } else {
            // Determine which events to show based on the expanded state
            val eventsToShow = if (expanded) {
                events
            } else {
                events.take(maxInitialEvents)
            }

            eventsToShow.forEachIndexed { index, event ->
                EventCardCompact(
                    event = event,
                    index = index,
                    onEventClick = onEventClick
                )
            }

            // Show "Show More" button only if there are more events to display
            if (events.size > maxInitialEvents) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (expanded) t(R.string.show_less) else t(R.string.show_more),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = Icons.Filled.ExpandMore,
                        contentDescription = if (expanded) t(R.string.show_less) else t(R.string.show_more),
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .rotate(rotationState)
                    )
                }
            }
        }
    }
}

// --- EventCardCompact: Now takes an Event object and handles clicks ---

/**
 * Compact card for a single event.
 *
 * @param event The [Event] object to display.
 * @param index Used for staggered animation delay.
 * @param onEventClick Callback when the card is clicked, passes the eventId.
 * @param modifier Modifier applied to the card.
 */
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EventCardCompact(
    event: Event,
    index: Int,
    onEventClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    // Determine the accent color based on whether the event is mandatory
    val accentColor = if (event.isMandatory) {
        // A strong color for mandatory events, e.g., error color or a custom "important" color
        MaterialTheme.colorScheme.error
    } else {
        // Your primary color or a secondary color for optional events
        MaterialTheme.colorScheme.primary
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
            // You might change the container color slightly too if desired
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            onClick = { onEventClick(event.id) } // Trigger navigation on click
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Vertical accent bar - now changes color based on 'isMandatory'
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .fillMaxHeight()
                        .background(
                            accentColor, // Use the determined accentColor
                            RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                        )
                )
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Icon for mandatory events
                        if (event.isMandatory) {
                            Icon(
                                imageVector = Icons.Filled.Lock, // Or Icons.Filled.Warning
                                contentDescription = t(R.string.attendance_mandatory), // Localized text for accessibility
                                tint = accentColor, // Use the same accent color for consistency
                                modifier = Modifier.size(20.dp).padding(end = 4.dp)
                            )
                        }
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.startTime.format(DateTimeFormatter.ofPattern("MMM dd")),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}


/**
 * Compact card for a single alert.
 * Maintains card consistency with a vertical accent bar, using the theme's secondary colors
 * to differentiate from event cards. Includes an icon for clear alert semantics.
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
            // Use the secondary color container for the alert card background
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically // Vertically align contents
            ) {
                // Vertical accent bar - uses the secondary color for alerts
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .fillMaxHeight()
                        .background(
                            MaterialTheme.colorScheme.secondary, // Strong secondary color for accent
                            RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                        )
                )

                Row(
                    modifier = Modifier
                        .weight(1f) // Let the content row take up remaining space
                        .padding(12.dp), // Padding for the content inside the card
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // ICON: Provide clear visual cue for an alert
                    Icon(
                        imageVector = Icons.Filled.Info, // Or WarningAmber, NotificationImportant
                        contentDescription = "Alert", // Localize this for accessibility
                        tint = MaterialTheme.colorScheme.onSecondaryContainer, // Tint icon to match text for consistency
                        modifier = Modifier
                            .size(20.dp) // Maintain consistent icon size with mandatory events
                            .padding(end = 8.dp)
                    )

                    // TEXT: The alert message
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer // Text color contrasting with secondary container
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