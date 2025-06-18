package com.schoolbridge.v2.ui.event


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FolderOff
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.schoolbridge.v2.localization.t
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.schoolbridge.v2.R
import com.schoolbridge.v2.ui.common.components.AppSubHeader

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
    val targetAudience: String,
    var isRead: Boolean = false
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
    Surface(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 🎯 Title
            Text(
                text = event.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            // ❗️Mandatory / Optional Indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (event.isMandatory) Icons.Default.Warning else Icons.Default.Info,
                    contentDescription = null,
                    tint = if (event.isMandatory) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (event.isMandatory) t(R.string.attendance_mandatory) else t(R.string.optional_event),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (event.isMandatory) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }

            HorizontalDivider()

            // 🗓 Date & Time
            EventDetailRow(
                icon = Icons.Default.CalendarToday,
                label = t(R.string.date_time),
                value = "${event.startTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))} - ${event.endTime.format(DateTimeFormatter.ofPattern("HH:mm"))}"
            )

            // 📍 Location
            EventDetailRow(
                icon = Icons.Default.LocationOn,
                label = t(R.string.location),
                value = event.location
            )

            // 👤 Organizer & Contact
            EventDetailRow(Icons.Default.Person, t(R.string.organizer), event.organizer)
            EventDetailRow(Icons.Default.Email, t(R.string.contact), event.contactInfo)

            // 🎯 Audience
            EventDetailRow(Icons.Default.Group, t(R.string.target_audience), event.targetAudience)

            HorizontalDivider()

            // 📝 Description
            AppSubHeader("description")
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 📢 RSVP
            if (event.requiresRSVP) {
                HorizontalDivider()
                AppSubHeader("Your attendence")

                when (userEventStatus?.isConfirmed) {
                    true -> StatusCard(
                        icon = Icons.Default.CheckCircle,
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        text = t(R.string.your_presence_confirmed)
                    )

                    false -> StatusCard(
                        icon = Icons.Default.Cancel,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        text = "Decline presence"
                    )

                    null -> {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Button(
                                onClick = { onConfirmPresence(event.id) },
                                modifier = Modifier.weight(1f),
                                enabled = event.rsvpDeadline == null || LocalDateTime.now().isBefore(event.rsvpDeadline)
                            ) {
                                Text(t(R.string.confirm_presence))
                            }
                            OutlinedButton(
                                onClick = { onDeclinePresence(event.id) },
                                modifier = Modifier.weight(1f),
                                enabled = event.rsvpDeadline == null || LocalDateTime.now().isBefore(event.rsvpDeadline)
                            ) {
                                Text(t(R.string.decline_presence))
                            }
                        }

                        event.rsvpDeadline?.let {
                            Text(
                                text = "${t(R.string.rsvp_by)} ${it.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            // 📎 Attachments
            if (event.attachments.isNotEmpty()) {
                HorizontalDivider()
                AppSubHeader("attachments")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    event.attachments.forEach { file ->
                        OutlinedButton(
                            onClick = { /* TODO: Handle file */ },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachFile,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(text = file)
                        }
                    }
                }
            }else {
                HorizontalDivider()
                AppSubHeader("attachments")
                Spacer(Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOff,
                            contentDescription = "No attachments",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "No attachments available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

        }
    }
}
@Composable
fun StatusCard(icon: ImageVector, color: Color, text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(icon, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(text = text, style = MaterialTheme.typography.bodyMedium)
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