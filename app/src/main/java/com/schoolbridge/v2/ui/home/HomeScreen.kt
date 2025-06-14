package com.schoolbridge.v2.ui.home // Adjust package as needed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.domain.user.CurrentUser
import com.schoolbridge.v2.ui.event.Event
import com.schoolbridge.v2.ui.event.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime // Import LocalDateTime
import java.time.format.DateTimeFormatter // Import DateTimeFormatter

// --- Dummy Data Classes and Helper Functions (as provided in previous responses) ---
/*
// Re-defining Event data class with an 'id' for navigation
data class Event(
    val id: String,
    val title: String,
    val description: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val location: String,
    val isMandatory: Boolean,
    val requiresRSVP: Boolean,
    val rsvpDeadline: LocalDateTime?,
    val organizer: String,
    val contactInfo: String,
    val attachments: List<String> = emptyList(),
    val targetAudience: String
)
*/
data class UserEventStatus(
    val eventId: String,
    val isConfirmed: Boolean? // Null means not responded, true for confirmed, false for declined
)


/*
data class CurrentUser(val linkedStudents: List<LinkedStudent>?) {
    data class LinkedStudent(val firstName: String, val lastName: String)
}*/

// Dummy t function for string resources (replace with your actual R.string in a real app)
fun t(resourceId: Int): String {
    return when (resourceId) {
        // These should ideally come from your actual R.string resources
        // For demonstration, we'll keep them simple.
        R.string.your_children -> "Your Children"
        R.string.recent_alerts -> "Recent Alerts"
        R.string.upcoming_events -> "Upcoming Events"
        R.string.alert_midterm_exams -> "Midterm Exams Alert"
        R.string.alert_uniform_inspection -> "Uniform Inspection Alert"
        R.string.event_meeting -> "Parent-Teacher Meeting"
        R.string.event_sports_day -> "Annual Sports Day"
        R.string.event_science_fair -> "School Science Fair"
        R.string.confirm_presence -> "Confirm Presence"
        R.string.decline_presence -> "Decline Presence"
        R.string.your_presence_confirmed -> "Your Presence Confirmed"
        R.string.attendance_mandatory -> "Attendance is Mandatory"
        R.string.optional_event -> "Optional Event"
        R.string.absence_sanctioned -> "Unexcused absence may lead to sanctions."
        R.string.rsvp_by -> "RSVP by:"
        R.string.event_details -> "Event Details"
        R.string.date_time -> "Date & Time"
        R.string.location -> "Location"
        R.string.organizer -> "Organizer"
        R.string.contact -> "Contact"
        R.string.target_audience -> "Target Audience"
        else -> "Localized String"
    }
}

// Dummy Spacer composables
@Composable fun SpacerS() { Spacer(Modifier.height(8.dp)) }
@Composable fun SpacerM() { Spacer(Modifier.height(16.dp)) }
@Composable fun SpacerL() { Spacer(Modifier.height(24.dp)) }

// Assuming AppSubHeader is defined elsewhere
@Composable
fun AppSubHeader(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
}

// Dummy R.string for the example to compile. In a real project, this would be auto-generated.
object R {
    object string {
        const val your_children = 0
        const val recent_alerts = 1
        const val upcoming_events = 2
        const val alert_midterm_exams = 3
        const val alert_uniform_inspection = 4
        const val event_meeting = 5
        const val event_sports_day = 6
        const val event_science_fair = 7
        const val confirm_presence = 8
        const val decline_presence = 9
        const val your_presence_confirmed = 10
        const val attendance_mandatory = 11
        const val optional_event = 12
        const val absence_sanctioned = 13
        const val rsvp_by = 14
        const val event_details = 15
        const val date_time = 16
        const val location = 17
        const val organizer = 18
        const val contact = 19
        const val target_audience = 20
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
            onViewAllAlertsClick = { /* TODO: Navigate to all alerts */ },
            onViewAllEventsClick = { /* TODO: Navigate to all events */ },
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
    onEventClick: (String) -> Unit, // New callback
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

    // Use the actual EventRepository to get events
    val eventRepository = remember { EventRepository() } // Or inject via Hilt/DI
    val events = remember { eventRepository.getUpcomingEvents() } // Get the list of Event objects

    if (events.isEmpty()) {
        Text("No upcoming events.", style = MaterialTheme.typography.bodyMedium)
    } else {
        events.forEachIndexed { index, event ->
            EventCardCompact(
                event = event, // Pass the whole Event object
                index = index,
                onEventClick = onEventClick // Pass the onEventClick callback
            )
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
    event: Event, // Changed from title and date to Event object
    index: Int,
    onEventClick: (String) -> Unit, // New callback for click
    modifier: Modifier = Modifier
) {
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
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            onClick = { onEventClick(event.id) } // Trigger navigation on click
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
                        text = event.title, // Use event.title
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.startTime.format(DateTimeFormatter.ofPattern("MMM dd")), // Use event.startTime
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