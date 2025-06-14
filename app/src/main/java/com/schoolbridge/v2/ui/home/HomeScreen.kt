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


/**
 * The main route Composable for the Home screen.
 * This Composable is responsible for observing data from the [UserSessionManager]
 * and delegating the UI rendering to [HomeScreenContent].
 *
 * It acts as the state holder for the Home screen.
 *
 * @param userSessionManager The manager for user session data.
 * @param onSettingsClick Callback invoked when the settings icon is clicked.
 * @param modifier The modifier to be applied to the layout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    userSessionManager: UserSessionManager,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Collect the currentUser StateFlow directly, providing an initial value of null.
    // This allows the UI to react to null (loading/no data) state.
    val currentUser by userSessionManager.currentUser.collectAsStateWithLifecycle(initialValue = null)

    Scaffold(
        topBar = {
            HomeTopBar(onSettingsClick = onSettingsClick)
        },
        modifier = modifier
    ) { paddingValues ->
        // Pass necessary data and callbacks to the content Composable.
        HomeUI(
            currentUser = currentUser,
            onViewAllAlertsClick = { /* TODO: Implement navigation to all alerts */ },
            onViewAllEventsClick = { /* TODO: Implement navigation to all events */ },
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        )
    }
}
/**
 * Displays the main content of the Home screen.
 * This Composable is responsible for rendering the UI based on the provided data.
 * It's designed to be stateless and highly testable.
 *
 * @param currentUser The [CurrentUser] object containing user and linked student data.
 * Can be null if data is still loading or no user is logged in.
 * @param onViewAllAlertsClick Callback invoked when "View All" for alerts is clicked.
 * @param onViewAllEventsClick Callback invoked when "View All" for events is clicked.
 * @param modifier The modifier to be applied to the layout.
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
        // Display children section if there are linked students
        val students = currentUser?.linkedStudents
        StudentListSection(students = students)

        SpacerL()

        // Display recent alerts section
        AlertsSection(onViewAllAlertsClick = onViewAllAlertsClick)

        SpacerL()

        // Display upcoming events section
        EventsSection(onViewAllEventsClick = onViewAllEventsClick)
    }
}
/**
 * Composable for the Top App Bar of the Home screen.
 *
 * @param onSettingsClick Callback invoked when the settings icon is clicked.
 * @param modifier The modifier to be applied to the Top App Bar.
 */
@OptIn(ExperimentalMaterial3Api::class) // Required for TopAppBar
@Composable
private fun HomeTopBar(
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text("SchoolBridge") }, // A more general app title
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings" // Use localized string if available
                )
            }
        },
        modifier = modifier
    )
}
/**
 * Displays a horizontal list of linked student profile cards.
 *
 * @param students The list of [CurrentUser.LinkedStudent] objects to display. Can be null or empty.
 * @param modifier The modifier to be applied to the section.
 */
@Composable
private fun StudentListSection(
    students: List<CurrentUser.LinkedStudent>?,
    modifier: Modifier = Modifier
) {
    if (!students.isNullOrEmpty()) {
        Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
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
 * Displays a section for recent alerts, including a header and compact alert cards.
 *
 * @param onViewAllAlertsClick Callback invoked when "View All" for alerts is clicked.
 * @param modifier The modifier to be applied to the section.
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
            Text(
                text = "View All", // t(R.string.view_all), // use "All" or "View All"
                style = MaterialTheme.typography.labelLarge
            )
        }
    }

    SpacerS()

    // TODO: Fetch alerts dynamically based on user subscriptions (e.g., via a ViewModel)
    AlertCardCompact(t(R.string.alert_midterm_exams))
    AlertCardCompact(t(R.string.alert_uniform_inspection))
}
/**
 * Displays a section for upcoming events, including a header and compact event cards.
 *
 * @param onViewAllEventsClick Callback invoked when "View All" for events is clicked.
 * @param modifier The modifier to be applied to the section.
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
            Text(
                text = "View All", // t(R.string.view_all), // use "All" or "View All"
                style = MaterialTheme.typography.labelLarge
            )
        }
    }

    SpacerS()

    // TODO: Fetch events dynamically via a ViewModel
    EventCardCompact(t(R.string.event_meeting), "June 10")
    EventCardCompact(t(R.string.event_sports_day), "June 20")
}
/**
 * A compact card Composable for displaying a single alert message.
 *
 * @param message The alert message to display.
 * @param modifier The modifier to be applied to the card.
 */
@Composable
fun AlertCardCompact(message: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Accent bar
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(
                        MaterialTheme.colorScheme.error,
                        RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                    )
            )
            // Content
            Text(
                text = message,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}
/**
 * A compact card Composable for displaying a single event with its title and date.
 *
 * @param title The title of the event.
 * @param date The date of the event.
 * @param modifier The modifier to be applied to the card.
 */
@Composable
fun EventCardCompact(title: String, date: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Accent bar
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                    )
            )

            // Content
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
/**
 * A card Composable for displaying a linked student's profile information in a compact style.
 *
 * @param student The [CurrentUser.LinkedStudent] object to display.
 * @param modifier The modifier to be applied to the card.
 */
@Composable
fun StudentCard(student: CurrentUser.LinkedStudent, modifier: Modifier = Modifier) {
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
            // Side Accent Bar
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
                        text = student.firstName.firstOrNull()?.uppercase() ?: "?",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${student.firstName} ${student.lastName}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Reg No: ${student.id}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "DOB: 25/06/1995", // TODO: Replace with actual student.dateOfBirth
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}