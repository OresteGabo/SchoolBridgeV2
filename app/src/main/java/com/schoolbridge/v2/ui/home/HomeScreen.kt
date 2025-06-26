package com.schoolbridge.v2.ui.home

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.schoolbridge.v2.components.CustomBottomNavBar
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.domain.messaging.Alert
import com.schoolbridge.v2.domain.user.CurrentUser
import com.schoolbridge.v2.domain.user.UserRole
import com.schoolbridge.v2.ui.components.SpacerL
import com.schoolbridge.v2.ui.home.alert.AlertDetailsBottomSheetContent
import com.schoolbridge.v2.ui.home.alert.AlertsSection
import com.schoolbridge.v2.ui.home.course.CourseListSection
import com.schoolbridge.v2.ui.home.event.EventsSection
import com.schoolbridge.v2.ui.home.grade.GradesSummarySection
import com.schoolbridge.v2.ui.home.schedule.TodayScheduleSection
import com.schoolbridge.v2.ui.home.student.StudentListSection
import com.schoolbridge.v2.ui.navigation.MainAppScreen
import kotlinx.coroutines.launch

/* ──────────────────────────────────────────────────────────────────────────────
 *  1️⃣  Top-bar with a Role “combo-box”
 * ────────────────────────────────────────────────────────────────────────────── */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    currentRole: UserRole?,
    availableRoles: Set<UserRole>,
    onRoleSelected: (UserRole) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuOpen by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
               Text("SchoolBridge")
                Spacer(Modifier.width(8.dp))

                // Show the switcher only if the user owns >1 role
                AnimatedVisibility(visible = availableRoles.size > 1 && currentRole != null) {
                    Box {
                        TextButton(onClick = { menuOpen = true }) {
                            Text(currentRole?.humanLabel ?: "")
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Change role"
                            )
                        }
                        DropdownMenu(
                            expanded = menuOpen,
                            onDismissRequest = { menuOpen = false }
                        ) {
                            // Optional: Title
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Switch Role",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                },
                                onClick = {},
                                enabled = false, // acts like a header
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            )

                            HorizontalDivider()

                            // List roles
                            availableRoles.forEach { role ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                role.humanLabel,
                                                style = MaterialTheme.typography.bodyMedium
                                                    .copy(fontWeight = FontWeight.SemiBold)
                                            )
                                            // Optional subtitle based on role
                                            Text(
                                                text = when (role) {
                                                    UserRole.PARENT -> "Monitor your children’s progress"
                                                    UserRole.STUDENT -> "View your courses and schedule"
                                                    UserRole.TEACHER -> "Manage your classes"
                                                    UserRole.SCHOOL_ADMIN -> "School administration tools"
                                                    else -> ""
                                                },
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    trailingIcon = {
                                        if (role == currentRole) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Current role",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    },
                                    onClick = {
                                        menuOpen = false
                                        if (role != currentRole) onRoleSelected(role)
                                    },
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }

                    }
                }
            }
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
        },
        modifier = modifier
    )
}

/* ──────────────────────────────────────────────────────────────────────────────
 *  2️⃣  Main screen “Route” that wires the role switcher
 * ────────────────────────────────────────────────────────────────────────────── */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    userSessionManager: UserSessionManager,
    currentScreen: MainAppScreen,
    onTabSelected: (MainAppScreen) -> Unit,
    onSettingsClick: () -> Unit,
    onWeeklyViewClick: () -> Unit,
    onViewAllAlertsClick: () -> Unit,
    onViewAllEventsClick: () -> Unit,
    onEventClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by userSessionManager.currentUser.collectAsStateWithLifecycle(initialValue = null)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var selectedAlert by remember { mutableStateOf<Alert?>(null) }

    Scaffold(
        topBar = {
            HomeTopBar(
                currentRole = currentUser?.currentRole,
                availableRoles = currentUser?.activeRoles ?: emptySet(),
                onRoleSelected = { selectedRole ->
                    scope.launch { userSessionManager.setCurrentRole(selectedRole) }
                },
                onSettingsClick = onSettingsClick
            )
        },
        bottomBar = {
            CustomBottomNavBar(
                currentScreen = currentScreen,
                onTabSelected = onTabSelected
            )
        },
        modifier = modifier
    ) { paddingValues ->

        /* Main content */
        HomeUI(
            currentUser = currentUser,
            onViewAllAlertsClick = onViewAllAlertsClick,
            onViewAllEventsClick = onViewAllEventsClick,
            onEventClick = onEventClick,
            onAlertClick = { alert ->
                selectedAlert = alert
                scope.launch { sheetState.show() }
            },
            onWeeklyViewClick = onWeeklyViewClick,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        )

        /* One-off bottom sheet for a tapped alert */
        if (selectedAlert != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    scope.launch {
                        sheetState.hide()
                        selectedAlert = null
                    }
                },
                sheetState = sheetState
            ) {
                selectedAlert?.let { alert ->
                    AlertDetailsBottomSheetContent(alertId = alert.id)
                }
            }
        }
    }
}

/* ──────────────────────────────────────────────────────────────────────────────
 *  3️⃣  HomeUI now keys off *currentRole* instead of only “isStudent()”
 * ────────────────────────────────────────────────────────────────────────────── */
@Composable
private fun HomeUI(
    currentUser: CurrentUser?,
    onViewAllAlertsClick: () -> Unit,
    onViewAllEventsClick: () -> Unit,
    onWeeklyViewClick: () -> Unit,
    onEventClick: (String) -> Unit,
    onAlertClick: (Alert) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeRole = currentUser?.currentRole
    Log.d("HomeUI", "Active role: $activeRole")
    if(activeRole == null) {
        if(currentUser != null) {
            if(currentUser.activeRoles.isNotEmpty()){
                Log.d("HomeUI", "Active roles: ${currentUser.activeRoles}")
                currentUser.currentRole=currentUser.activeRoles.first()
                activeRole = currentUser.currentRole

            }
        }
    }
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (activeRole) {
            UserRole.STUDENT -> {
                CourseListSection()
                SpacerL()
                TodayScheduleSection(onWeeklyViewClick = onWeeklyViewClick)
                SpacerL()
                AlertsSection(
                    onViewAllAlertsClick = onViewAllAlertsClick,
                    onAlertClick = onAlertClick
                )
                SpacerL()
                GradesSummarySection()
            }

            UserRole.PARENT -> {
                StudentListSection(students = currentUser?.linkedStudents)
                SpacerL()
                AlertsSection(
                    onViewAllAlertsClick = onViewAllAlertsClick,
                    onAlertClick = onAlertClick
                )
                SpacerL()
                EventsSection(
                    onViewAllEventsClick = onViewAllEventsClick,
                    onEventClick = onEventClick
                )
            }

            UserRole.TEACHER -> {
                CourseListSection() // Shows teacher's own courses
                SpacerL()
                TodayScheduleSection(onWeeklyViewClick = onWeeklyViewClick)
                SpacerL()
                AlertsSection(
                    onViewAllAlertsClick = onViewAllAlertsClick,
                    onAlertClick = onAlertClick
                )
                SpacerL()
                TeacherQuickActionsSection(currentUser = currentUser)
            }

            UserRole.SCHOOL_ADMIN -> {
                //AdminDashboardPlaceholder()   // implement your admin widgets
            }

            else -> { /* no role yet or still loading */ }
        }
    }
}

/* ──────────────────────────────────────────────────────────────────────────────
 *  4️⃣  Small helpers / previews
 * ────────────────────────────────────────────────────────────────────────────── */
val UserRole.humanLabel: String
    get() = when (this) {
        UserRole.STUDENT       -> "Student"
        UserRole.PARENT        -> "Parent"
        UserRole.TEACHER       -> "Teacher"
        UserRole.SCHOOL_ADMIN  -> "Admin"
        UserRole.GUEST         -> "Guest"
    }

@Preview(showBackground = true)
@Composable
private fun HomeTopBarPreview() {
    HomeTopBar(
        currentRole = UserRole.PARENT,
        availableRoles = setOf(UserRole.PARENT, UserRole.TEACHER),
        onRoleSelected = {},
        onSettingsClick = {}
    )
}



// Dummy data: Map teacherUserIds to names for display (in real app this would come from user repository)
val dummyTeacherNames = mapOf(
    "teacher1" to "Mr. Kamali",
    "teacher2" to "Ms. Uwase",
    "teacher3" to "Mrs. Mukeshimana",
    "teacher4" to "Mr. Habimana"
)

@Composable
fun TagChip(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
data class UserEventStatus(
    val eventId: String,
    val isConfirmed: Boolean? // Null means not responded, true for confirmed, false for declined
)

@Composable
fun TeacherQuickActionsSection(currentUser: CurrentUser?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            TeacherActionCard(
                title = "Mark Attendance",
                icon = Icons.Default.CheckCircle,
                onClick = { /* Navigate to attendance */ }
            )
            TeacherActionCard(
                title = "Mark Grades",
                icon = Icons.Default.Grade,
                onClick = { /* Navigate to grading */ }
            )
            TeacherActionCard(
                title = "My Students",
                icon = Icons.Default.Group,
                onClick = { /* View student list */ }
            )
        }
    }
}

@Composable
fun TeacherActionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .width(100.dp)
            .height(100.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
