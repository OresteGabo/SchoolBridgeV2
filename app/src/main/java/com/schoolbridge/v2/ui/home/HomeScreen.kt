package com.schoolbridge.v2.ui.home // Adjust package as needed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.schoolbridge.v2.components.CustomBottomNavBar
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.domain.messaging.Alert
import com.schoolbridge.v2.domain.user.CurrentUser
import com.schoolbridge.v2.ui.common.components.SpacerL
import com.schoolbridge.v2.ui.home.alert.AlertDetailsBottomSheetContent
import com.schoolbridge.v2.ui.home.alert.AlertsSection
import com.schoolbridge.v2.ui.home.event.EventsSection
import com.schoolbridge.v2.ui.home.student.StudentListSection
import com.schoolbridge.v2.ui.navigation.MainAppScreen
import com.schoolbridge.v2.ui.onboarding.shared.MainNavScreen
import kotlinx.coroutines.launch


data class UserEventStatus(
    val eventId: String,
    val isConfirmed: Boolean? // Null means not responded, true for confirmed, false for declined
)


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
    currentScreen: MainAppScreen,
    onTabSelected: (MainAppScreen) -> Unit,
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
        bottomBar = { CustomBottomNavBar(
            currentScreen = currentScreen,
            onTabSelected = onTabSelected
        ) },
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
    onAlertClick: (Alert) -> Unit,
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

