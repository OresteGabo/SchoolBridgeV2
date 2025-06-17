package com.schoolbridge.v2.ui.event

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource // Assuming you have string resources
import androidx.compose.ui.tooling.preview.Preview
import com.schoolbridge.v2.localization.t
import com.schoolbridge.v2.ui.common.components.SpacerM
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.schoolbridge.v2.R
import com.schoolbridge.v2.ui.common.components.AppSubHeader
import com.schoolbridge.v2.ui.common.components.SpacerS
import kotlinx.coroutines.flow.filter

// Dummy data classes for demonstration
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


data class UserEventStatus(
    val eventId: String,
    val isConfirmed: Boolean? // Null means not responded, true for confirmed, false for declined
)

// Event Details Route - Entry point for fetching data and managing state
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsRoute(
    eventId: String, // Pass event ID to fetch specific event details
    onBackClick: () -> Unit,
    eventRepository: EventRepository, // A repository to fetch event data
    modifier: Modifier = Modifier
) {
    val event by eventRepository.getEventDetails(eventId).collectAsState(initial = null)
    val userEventStatus by eventRepository.getUserEventStatus(eventId).collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Event details")//Text(t(R.string.event_details))
                        },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        if (event == null) {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator() // Show loading indicator
            }
        } else {
            EventDetailsUI(
                event = event!!,
                userEventStatus = userEventStatus,
                onConfirmPresence = { eventRepository.confirmPresence(eventId) },
                onDeclinePresence = { eventRepository.declinePresence(eventId) },
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}



// Stateless Event Details UI
@Composable
fun EventDetailsUI(
    event: Event,
    userEventStatus: UserEventStatus?,
    onConfirmPresence: (String) -> Unit,
    onDeclinePresence: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Event Title
        Text(
            text = event.title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )
        SpacerM()

        // Obligation Status
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (event.isMandatory) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Mandatory Event",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = t(R.string.attendance_mandatory),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Optional Event",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = t(R.string.optional_event),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        if (event.isMandatory && !event.attachments.contains("sanction_policy.pdf")) { // Example for sanction info
            Text(
                text = t(R.string.absence_sanctioned),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 32.dp, top = 4.dp)
            )
        }
        SpacerM()

        // Date & Time
        EventDetailRow(
            icon = Icons.Default.CalendarToday,
            label = t(R.string.date_time),
            value = "${event.startTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))} - ${event.endTime.format(DateTimeFormatter.ofPattern("HH:mm"))}"
        )
        SpacerS()

        // Location
        EventDetailRow(
            icon = Icons.Default.LocationOn,
            label = t(R.string.location),
            value = event.location
        )
        SpacerS()

        // Organizer and Contact
        EventDetailRow(
            icon = Icons.Default.Person,
            label = t(R.string.organizer),
            value = event.organizer
        )
        SpacerS()
        EventDetailRow(
            icon = Icons.Default.Person, // Reusing icon for simplicity
            label = t(R.string.contact),
            value = event.contactInfo
        )
        SpacerS()

        // Target Audience
        EventDetailRow(
            icon = Icons.Default.Person, // Reusing icon
            label = t(R.string.target_audience),
            value = event.targetAudience
        )
        SpacerM()

        // Description
        AppSubHeader("Description")
        SpacerS()
        Text(
            text = event.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        SpacerM()

        // RSVP Section
        if (event.requiresRSVP) {
            AppSubHeader("Your Attendance")
            SpacerS()

            when (userEventStatus?.isConfirmed) {
                true -> {
                    // Confirmed label
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Confirmed",
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = t(R.string.your_presence_confirmed),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
                false -> {
                    // Declined state (optional to show, or just allow to re-confirm)
                    Text(
                        text = "You have declined presence.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    SpacerS()
                    Button(onClick = { onConfirmPresence(event.id) }) {
                        Text(t(R.string.confirm_presence))
                    }
                }
                null -> {
                    // Not yet responded
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(
                            onClick = { onConfirmPresence(event.id) },
                            enabled = event.rsvpDeadline == null || LocalDateTime.now().isBefore(event.rsvpDeadline)
                        ) {
                            Text(t(R.string.confirm_presence))
                        }
                        OutlinedButton(
                            onClick = { onDeclinePresence(event.id) },
                            enabled = event.rsvpDeadline == null || LocalDateTime.now().isBefore(event.rsvpDeadline)
                        ) {
                            Text(t(R.string.decline_presence))
                        }
                    }
                    event.rsvpDeadline?.let {
                        SpacerS()
                        Text(
                            text = "${t(R.string.rsvp_by)} ${it.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally),
                            //textAlign = Alignment.CenterHorizontally
                        )
                    }

                }
            }
        }

        // Attachments (if any)
        if (event.attachments.isNotEmpty()) {
            SpacerM()
            AppSubHeader("Attachments")
            SpacerS()
            event.attachments.forEach { attachment ->
                TextButton(onClick = { /* TODO: Handle attachment download/view */ }) {
                    Text(text = attachment)
                }
            }
        }
    }
}

@Composable
fun EventDetailRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// Dummy EventRepository for demonstration
open class EventRepository {
    private val _events = MutableStateFlow(
                listOf(
                    Event(
                        id = "1",
                        title = "Parent-Teacher Meeting",
                        description = "An important meeting to discuss student progress and upcoming academic plans. Your presence is crucial.",
                        startTime = LocalDateTime.of(2025, 7, 10, 9, 0), // Future date
                        endTime = LocalDateTime.of(2025, 7, 10, 12, 0),
                        location = "School Auditorium",
                        isMandatory = true,
                        requiresRSVP = true,
                        rsvpDeadline = LocalDateTime.of(2025, 7, 5, 23, 59),
                        organizer = "School Administration",
                        contactInfo = "admin@school.com",
                        targetAudience = "All Parents"
                    ),
                    Event(
                        id = "2",
                        title = "Annual Sports Day",
                        description = "Come and support our students as they compete in various sports events! A fun day for the whole family.",
                        startTime = LocalDateTime.of(2025, 8, 20, 8, 30), // Future date
                        endTime = LocalDateTime.of(2025, 8, 20, 16, 0),
                        location = "School Sports Ground",
                        isMandatory = false,
                        requiresRSVP = false,
                        rsvpDeadline = null,
                        organizer = "Sports Department",
                        contactInfo = "sports@school.com",
                        attachments = listOf("SportsDaySchedule.pdf", "ParticipationRules.docx"),
                        targetAudience = "All Students and Parents"
                    ),
                    Event(
                        id = "3",
                        title = "School Science Fair",
                        description = "Showcasing innovative science projects by our talented students. Visitors are welcome to explore and learn.",
                        startTime = LocalDateTime.of(2025, 9, 15, 10, 0), // Future date
                        endTime = LocalDateTime.of(2025, 9, 15, 15, 0),
                        location = "School Gymnasium",
                        isMandatory = false,
                        requiresRSVP = true,
                        rsvpDeadline = LocalDateTime.of(2025, 9, 10, 23, 59),
                        organizer = "Science Department",
                        contactInfo = "science@school.com",
                        targetAudience = "Students, Parents, and Public"
                    ),
                    Event(
                        id = "4",
                        title = "Autumn Festival & Craft Market",
                        description = "Celebrate the season with fun activities, local crafts, and delicious food. Open to the entire community!",
                        startTime = LocalDateTime.of(2025, 10, 5, 10, 0),
                        endTime = LocalDateTime.of(2025, 10, 5, 17, 0),
                        location = "School Courtyard",
                        isMandatory = false,
                        requiresRSVP = false,
                        rsvpDeadline = null,
                        organizer = "Community Outreach",
                        contactInfo = "community@school.com",
                        attachments = listOf("VendorApplication.pdf"),
                        targetAudience = "All Community Members"
                    ),
                    Event(
                        id = "5",
                        title = "Winter Concert Rehearsal",
                        description = "Mandatory rehearsal for all choir and band members performing in the annual Winter Concert.",
                        startTime = LocalDateTime.of(2025, 11, 20, 16, 0),
                        endTime = LocalDateTime.of(2025, 11, 20, 18, 0),
                        location = "Music Room",
                        isMandatory = true,
                        requiresRSVP = false,
                        rsvpDeadline = null,
                        organizer = "Music Department",
                        contactInfo = "music@school.com",
                        targetAudience = "Choir and Band Members"
                    ),
                    Event(
                        id = "6",
                        title = "Career Day Expo",
                        description = "Explore various career paths and meet professionals from diverse industries. A great opportunity for high school students.",
                        startTime = LocalDateTime.of(2025, 12, 1, 9, 0),
                        endTime = LocalDateTime.of(2025, 12, 1, 14, 0),
                        location = "School Gymnasium",
                        isMandatory = true,
                        requiresRSVP = true,
                        rsvpDeadline = LocalDateTime.of(2025, 11, 25, 23, 59),
                        organizer = "Guidance Counselor's Office",
                        contactInfo = "guidance@school.com",
                        attachments = listOf("ParticipatingCompanies.pdf"),
                        targetAudience = "High School Students"
                    ),
                    Event(
                        id = "7",
                        title = "Alumni Homecoming Gala",
                        description = "An evening to reconnect with old friends, faculty, and celebrate the school's legacy. Dinner and awards ceremony.",
                        startTime = LocalDateTime.of(2026, 1, 15, 18, 0),
                        endTime = LocalDateTime.of(2026, 1, 15, 22, 0),
                        location = "Grand Ballroom, City Hotel",
                        isMandatory = false,
                        requiresRSVP = true,
                        rsvpDeadline = LocalDateTime.of(2025, 12, 31, 23, 59),
                        organizer = "Alumni Association",
                        contactInfo = "alumni@school.com",
                        attachments = listOf("GalaMenu.pdf", "DressCode.txt"),
                        targetAudience = "Alumni, Former Faculty"
                    ),
                    Event(
                        id = "8",
                        title = "First Aid Training Workshop",
                        description = "Learn essential first aid skills. Certification provided upon completion. Limited spots available.",
                        startTime = LocalDateTime.of(2026, 2, 10, 13, 0),
                        endTime = LocalDateTime.of(2026, 2, 10, 17, 0),
                        location = "School Nurse's Office",
                        isMandatory = false,
                        requiresRSVP = true,
                        rsvpDeadline = LocalDateTime.of(2026, 2, 5, 23, 59),
                        organizer = "Health and Safety Committee",
                        contactInfo = "nurse@school.com",
                        targetAudience = "Staff, Senior Students"
                    )
                )
    )

    private val _userEventStatuses = MutableStateFlow(
        mapOf(
            "1" to UserEventStatus("1", null), // Parent-Teacher Meeting: Not responded
            "2" to UserEventStatus("2", null), // Sports Day: No RSVP required
            "3" to UserEventStatus("3", false) // Science Fair: Declined
        )
    )

    open fun getEventDetails(eventId: String): StateFlow<Event?> {
        return MutableStateFlow(_events.value.find { it.id == eventId })
    }

    open fun getUserEventStatus(eventId: String): StateFlow<UserEventStatus?> {
        return MutableStateFlow(_userEventStatuses.value[eventId])
    }

    open fun confirmPresence(eventId: String) {
        _userEventStatuses.value = _userEventStatuses.value.toMutableMap().apply {
            this[eventId] = UserEventStatus(eventId, true)
        }
    }

    open fun declinePresence(eventId: String) {
        _userEventStatuses.value = _userEventStatuses.value.toMutableMap().apply {
            this[eventId] = UserEventStatus(eventId, false)
        }
    }

    // Exposed for HomeUI to get the list of upcoming events
    fun getUpcomingEvents(): List<Event> {
        val now = LocalDateTime.now()
        return _events.value.filter { it.startTime.isAfter(now) }.sortedBy { it.startTime }
    }
}




// --- Preview Composables ---

@Preview(showBackground = true, widthDp = 360)
@Composable
fun PreviewMandatoryEventNeedsRSVP() {
    // Replace YourAppTheme with your actual theme
    // YourAppTheme {
    val eventId = "1_preview"
    val mockEvent = Event(
        id = eventId,
        title = "Mandatory Parent-Teacher Meeting",
        description = "This is a critical meeting to discuss your child's academic progress and upcoming school initiatives. Your attendance is required.",
        startTime = LocalDateTime.of(2025, 7, 10, 9, 0),
        endTime = LocalDateTime.of(2025, 7, 10, 12, 0),
        location = "School Auditorium, Block C",
        isMandatory = true,
        requiresRSVP = true,
        rsvpDeadline = LocalDateTime.of(2025, 7, 5, 23, 59),
        organizer = "School Administration",
        contactInfo = "admin@excella.edu",
        targetAudience = "All Parents"
    )
    val mockUserStatus = UserEventStatus(eventId, null) // User has not responded yet

    val mockEventRepository = object : EventRepository() {
        override fun getEventDetails(eventId: String) = MutableStateFlow(mockEvent)
        override fun getUserEventStatus(eventId: String) = MutableStateFlow(mockUserStatus)
        override fun confirmPresence(eventId: String) { /* No-op for preview */ }
        override fun declinePresence(eventId: String) { /* No-op for preview */ }
    }

    EventDetailsRoute(
        eventId = eventId,
        onBackClick = { /* Do nothing in preview */ },
        eventRepository = mockEventRepository,
        modifier = Modifier.padding(16.dp)
    )
    // }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun PreviewOptionalEventNoRSVP() {
    // Replace YourAppTheme with your actual theme
    // YourAppTheme {
    val eventId = "2_preview"
    val mockEvent = Event(
        id = eventId,
        title = "Annual School Sports Day",
        description = "Join us for a fun-filled day of athletic competitions and team spirit! Spectators are welcome.",
        startTime = LocalDateTime.of(2025, 6, 20, 8, 30),
        endTime = LocalDateTime.of(2025, 6, 20, 16, 0),
        location = "School Sports Ground",
        isMandatory = false,
        requiresRSVP = false,
        rsvpDeadline = null,
        organizer = "Sports Department",
        contactInfo = "sports@excella.edu",
        attachments = listOf("SportsDaySchedule.pdf"),
        targetAudience = "All Students and Parents"
    )
    val mockUserStatus = UserEventStatus(eventId, null) // RSVP not required

    val mockEventRepository = object : EventRepository() {
        override fun getEventDetails(eventId: String) = MutableStateFlow(mockEvent)
        override fun getUserEventStatus(eventId: String) = MutableStateFlow(mockUserStatus)
        override fun confirmPresence(eventId: String) { /* No-op for preview */ }
        override fun declinePresence(eventId: String) { /* No-op for preview */ }
    }

    EventDetailsRoute(
        eventId = eventId,
        onBackClick = { /* Do nothing in preview */ },
        eventRepository = mockEventRepository,
        modifier = Modifier.padding(16.dp)
    )
    // }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun PreviewOptionalEventConfirmedRSVP() {
    // Replace YourAppTheme with your actual theme
    // YourAppTheme {
    val eventId = "3_preview"
    val mockEvent = Event(
        id = eventId,
        title = "School Science Fair",
        description = "Discover innovative projects from our talented students. Interactive exhibits and demonstrations will be available.",
        startTime = LocalDateTime.of(2025, 8, 15, 10, 0),
        endTime = LocalDateTime.of(2025, 8, 15, 15, 0),
        location = "School Gymnasium",
        isMandatory = false,
        requiresRSVP = true,
        rsvpDeadline = LocalDateTime.of(2025, 8, 10, 23, 59),
        organizer = "Science Department",
        contactInfo = "science@excella.edu",
        targetAudience = "Students, Parents, and Public"
    )
    val mockUserStatus = UserEventStatus(eventId, true) // User has confirmed presence

    val mockEventRepository = object : EventRepository() {
        override fun getEventDetails(eventId: String) = MutableStateFlow(mockEvent)
        override fun getUserEventStatus(eventId: String) = MutableStateFlow(mockUserStatus)
        override fun confirmPresence(eventId: String) { /* No-op for preview */ }
        override fun declinePresence(eventId: String) { /* No-op for preview */ }
    }

    EventDetailsRoute(
        eventId = eventId,
        onBackClick = { /* Do nothing in preview */ },
        eventRepository = mockEventRepository,
        modifier = Modifier.padding(16.dp)
    )
    // }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun PreviewOptionalEventDeclinedRSVP() {
    // Replace YourAppTheme with your actual theme
    // YourAppTheme {
    val eventId = "4_preview"
    val mockEvent = Event(
        id = eventId,
        title = "Art Exhibition Gala",
        description = "An evening celebrating the artistic talents of our students. Light refreshments will be served.",
        startTime = LocalDateTime.of(2025, 9, 5, 18, 0),
        endTime = LocalDateTime.of(2025, 9, 5, 20, 0),
        location = "School Art Gallery",
        isMandatory = false,
        requiresRSVP = true,
        rsvpDeadline = LocalDateTime.of(2025, 9, 1, 23, 59),
        organizer = "Art Department",
        contactInfo = "art@excella.edu",
        targetAudience = "Parents and Art Enthusiasts"
    )
    val mockUserStatus = UserEventStatus(eventId, false) // User has declined presence

    val mockEventRepository = object : EventRepository() {
        override fun getEventDetails(eventId: String) = MutableStateFlow(mockEvent)
        override fun getUserEventStatus(eventId: String) = MutableStateFlow(mockUserStatus)
        override fun confirmPresence(eventId: String) { /* No-op for preview */ }
        override fun declinePresence(eventId: String) { /* No-op for preview */ }
    }

    EventDetailsRoute(
        eventId = eventId,
        onBackClick = { /* Do nothing in preview */ },
        eventRepository = mockEventRepository,
        modifier = Modifier.padding(16.dp)
    )
    // }
}