package com.schoolbridge.v2.ui.home // Adjust package as needed

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.schoolbridge.v2.R
import com.schoolbridge.v2.components.CustomBottomNavBar
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.domain.messaging.Alert
import com.schoolbridge.v2.domain.messaging.AlertSeverity
import com.schoolbridge.v2.domain.messaging.AlertSourceType
import com.schoolbridge.v2.domain.messaging.AlertsViewModel
import com.schoolbridge.v2.domain.user.CurrentUser
import com.schoolbridge.v2.localization.t
import com.schoolbridge.v2.ui.AlertRepository
import com.schoolbridge.v2.ui.common.components.AppSubHeader
import com.schoolbridge.v2.ui.common.components.SpacerL
import com.schoolbridge.v2.ui.common.components.SpacerS
import com.schoolbridge.v2.ui.event.Event
import com.schoolbridge.v2.ui.event.EventRepository
import kotlinx.coroutines.launch
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
fun AlertsSection(
    viewModel: AlertsViewModel = viewModel(),
    onViewAllAlertsClick: () -> Unit,
    onAlertClick: (Alert) -> Unit,  // New callback
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val maxInitialAlerts = 3
    val alerts by viewModel.alerts.collectAsState()
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "rotationAnimation"
    )
    val alertsToShow = if (expanded) alerts else alerts.take(maxInitialAlerts)

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
            AppSubHeader("💬 " + t(R.string.recent_alerts))
            TextButton(onClick = onViewAllAlertsClick) {
                Text(text = t(R.string.view_all), style = MaterialTheme.typography.labelLarge)
            }
        }

        SpacerS()

        alertsToShow.forEachIndexed { index, alert ->
            AlertCardCompact(
                alert = alert,
                index = index,
                /*onClick = {
                    viewModel.markAsRead(alert.id)
                    onAlertClick(alert)  // trigger the bottom sheet
                }*/
                onClick = {
                    viewModel.markAsRead(alert.id)
                    // Wait until recomposition happens
                    val updatedAlert = viewModel.alerts.value.find { it.id == alert.id } ?: alert
                    onAlertClick(updatedAlert)
                }
            )
        }

        if (alerts.size > maxInitialAlerts) {
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
    onEventClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by userSessionManager.currentUser.collectAsStateWithLifecycle(initialValue = null)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var selectedAlert by remember { mutableStateOf<Alert?>(null) }

    // Main Scaffold outside of ModalBottomSheet to avoid gesture blocking
    Scaffold(
        topBar = { HomeTopBar(onSettingsClick = onSettingsClick) },
        bottomBar = { CustomBottomNavBar() },
        modifier = modifier
    ) { paddingValues ->

        // Main content
        HomeUI(
            currentUser = currentUser,
            onViewAllAlertsClick = onViewAllAlertsClick,
            onViewAllEventsClick = onViewAllEventsClick,
            onEventClick = onEventClick,
            onAlertClick = { alert ->
                selectedAlert = alert
                scope.launch {
                    sheetState.show()
                }
            },
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        )

        // Render bottom sheet only when alert is selected
        if (selectedAlert != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    scope.launch {
                        sheetState.hide()
                        selectedAlert = null
                    }
                },
                sheetState = sheetState,
            ) {
                selectedAlert?.let { alert ->
                    AlertDetailsBottomSheetContent(alertId = alert.id)
                }
            }
        }
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
    onEventClick: (String) -> Unit,
    onAlertClick: (Alert) -> Unit,  // New callback for alert card click
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
        AlertsSection(
            onViewAllAlertsClick = onViewAllAlertsClick,
            onAlertClick = onAlertClick  // Pass the new callback
        )
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

    val accentColor = if (event.isMandatory) {
        MaterialTheme.colorScheme.error
    } else {
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
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            onClick = {
                Log.d("EventCardCompact", "Event clicked: ${event.title}")
                event.isRead = true
                onEventClick(event.id) }
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .fillMaxHeight()
                        .background(
                            accentColor,
                            RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                        )
                )
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (event.isMandatory) {
                                Icon(
                                    imageVector = Icons.Filled.Lock,
                                    contentDescription = t(R.string.attendance_mandatory),
                                    tint = accentColor,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .padding(end = 4.dp)
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

                    // "NEW" badge
                    if (!event.isRead) {
                        Text(
                            text = "NEW",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
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
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AlertCardCompact(
    alert: Alert,
    index: Int,
    onClick: (Alert) -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val severityColor = when (alert.severity) {
        AlertSeverity.HIGH -> MaterialTheme.colorScheme.error
        AlertSeverity.MEDIUM -> MaterialTheme.colorScheme.secondary
        AlertSeverity.LOW -> MaterialTheme.colorScheme.tertiary
    }

    val accentColor = if (alert.isRead)
        severityColor.copy(alpha = 0.35f)
    else
        severityColor

    val textColor = if (alert.isRead)
        MaterialTheme.colorScheme.onSurfaceVariant
    else
        MaterialTheme.colorScheme.onSecondaryContainer

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(500)) + slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(500, delayMillis = index * 70)
        )
    ) {
        Card(
            onClick = { onClick(alert) },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Vertical accent bar
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .fillMaxHeight()
                        .background(
                            accentColor,
                            shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                        )
                )

                Spacer(Modifier.width(10.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = textColor,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(end = 6.dp)
                        )

                        Text(
                            text = alert.title,
                            style = MaterialTheme.typography.labelLarge,
                            color = textColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        if (!alert.isRead) {
                            Text(
                                text = "NEW",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = alert.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor.copy(alpha = 0.9f),
                        maxLines = 1,  // changed from 2 to 1 line as per your need
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp) // spacing between source and timestamp
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = "Source",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = alert.source,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Fixed width container for timestamp so it stays stable
                        Box(
                            modifier = Modifier.width(110.dp), // adjust as needed
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = alert.timestamp.format(DateTimeFormatter.ofPattern("HH:mm, MMM d")),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}


/* ------------ MAIN BOTTOM‑SHEET CONTENT ------------ */
@Composable
fun AlertDetailsBottomSheetContent(
    alertId: String,
    viewModel: AlertsViewModel = viewModel(),
){
    val alerts by viewModel.alerts.collectAsState()
    val alert = alerts.find { it.id == alertId } ?: return
    val dateText = remember(alert.timestamp) {
        alert.timestamp.format(DateTimeFormatter.ofPattern("HH:mm, MMM d • yyyy"))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // -- Header with sender & severity
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 8.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alert.publisherName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            SeverityChip(alert.severity)
        }

        Spacer(Modifier.height(16.dp))

        // -- Title
        Row{
            Text(
                text = alert.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            // "NEW" badge
            if (!alert.isRead) {
                Text(
                    text = "NEW",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }


        Spacer(Modifier.height(10.dp))

        // -- Message body
        Text(
            text = alert.message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(24.dp))

        // -- Detail info card
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoRowWithIcon(
                    icon = Icons.Default.AccountBalance,
                    label = "Source",
                    value = alert.sourceOrganization ?: "—"
                )
                InfoRowWithIcon(
                    icon = Icons.Default.School,
                    label = "Student",
                    value = alert.studentName ?: "Not linked to a student"
                )
                InfoRowWithIcon(
                    icon = Icons.Default.Notifications,
                    label = "Type",
                    value = alert.type.name.replaceFirstChar { it.uppercase() }
                )
                InfoRowWithIcon(
                    icon = Icons.Default.People,
                    label = "Publisher Type",
                    value = alert.publisherType.name.lowercase().replaceFirstChar { it.uppercase() }
                )
            }
        }

        // Optional mark as unread
        if (alert.isRead) {
            Spacer(Modifier.height(20.dp))
            OutlinedButton(
                onClick = {
                    //onMarkAsUnread(alert)

                        viewModel.markAsUnread(alert.id)
                        // Wait until recomposition happens
                        val updatedAlert = viewModel.alerts.value.find { it.id == alert.id } ?: alert

                    //alert = updatedAlert

                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(Icons.Default.MarkEmailUnread, contentDescription = "Mark as Unread")
                Spacer(Modifier.width(8.dp))
                Text("Mark as Unread")
            }
        }
    }
}


/* ------------ SEVERITY CHIP ------------ */
@Composable
fun SeverityChip(severity: AlertSeverity) {
    val (bg, fg, text) = when (severity) {
        AlertSeverity.HIGH   -> Triple(MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            "High • requires immediate attention")
        AlertSeverity.MEDIUM -> Triple(MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer,
            "Medium • please review")
        AlertSeverity.LOW    -> Triple(MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer,
            "Low • informational")
    }

    AssistChip(
        onClick = { /* no‑op */ },
        label    = { Text(text, maxLines = 1) },
        colors   = AssistChipDefaults.assistChipColors(
            containerColor = bg,
            labelColor     = fg
        ),
        elevation = AssistChipDefaults.assistChipElevation()
    )
}

/* ------------ DETAIL ROW WITH ICON ------------ */
@Composable
fun InfoRowWithIcon(
    icon: ImageVector,
    label: String,
    value: String,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier
                .size(24.dp) // Increased from 18dp to 24dp
                .padding(end = 12.dp)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
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