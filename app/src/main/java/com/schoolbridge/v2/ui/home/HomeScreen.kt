package com.schoolbridge.v2.ui.home


import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.schoolbridge.v2.components.CustomBottomNavBar
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.domain.academic.TodayCourse
import com.schoolbridge.v2.domain.messaging.Alert
import com.schoolbridge.v2.domain.user.CurrentUser
import com.schoolbridge.v2.domain.user.UserRole
import com.schoolbridge.v2.ui.components.AppSubHeader
import com.schoolbridge.v2.ui.components.SpacerL
import com.schoolbridge.v2.ui.components.SpacerM
import com.schoolbridge.v2.ui.home.alert.AlertDetailsBottomSheetContent
import com.schoolbridge.v2.ui.home.alert.AlertsSection
import com.schoolbridge.v2.ui.home.course.CourseListSection
import com.schoolbridge.v2.ui.home.event.EventsSection
import com.schoolbridge.v2.ui.home.grade.GradesSummarySection
import com.schoolbridge.v2.ui.home.schedule.TodayScheduleCard
import com.schoolbridge.v2.ui.home.schedule.TodayScheduleSection
import com.schoolbridge.v2.ui.home.student.StudentListSection
import com.schoolbridge.v2.ui.home.teacher.TeacherQuickActionsSection
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
                TeacherQuickActionsSection()
                SpacerL()
                TodayScheduleSection(onWeeklyViewClick = onWeeklyViewClick)
                SpacerL()
                AlertsSection(
                    onViewAllAlertsClick = onViewAllAlertsClick,
                    onAlertClick = onAlertClick
                )
                SpacerL()
                CourseListSection()
            }

            UserRole.SCHOOL_ADMIN -> {
                AdminQuickActionsSection()
                SpacerL()

                SpacerM()
                AdminTodayScheduleSection(onWeeklyViewClick = onWeeklyViewClick)
                PendingGradesSection()
                RecentSanctionsSection()
                ApprovalRequestsSection()
                InternalMemosSection()
                StudentExplorerSection()
                AcademicCalendarSection()
                TeacherActivitySummarySection()
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



@Composable
fun AdminQuickActionsSection(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            adminQuickActions.forEach { action ->
                TeacherActionCard(            // reuse existing card util
                    title  = action.title,
                    icon   = action.icon,
                    onClick = action.onClick
                )
            }
        }
    }
}

data class AdminQuickAction(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit = {}
)

/* 🔧 Add/adjust actions as needed */
val adminQuickActions = listOf(
    AdminQuickAction("sanction",    "Record Sanction", Icons.Default.Warning),
    AdminQuickAction("timetable",   "Edit Timetable",  Icons.Default.Schedule),
    AdminQuickAction("assign",      "Assign Teacher",  Icons.Default.PersonAdd),
    AdminQuickAction("students",    "Manage Students", Icons.Default.Group),
    AdminQuickAction("announcement","Send Notice",     Icons.Default.Campaign),
    AdminQuickAction("reports",     "Reports",         Icons.Default.Description),
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTodayScheduleSection(
    onWeeklyViewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    /* simple filter state – replace with ViewModel later */
    var selectedFilter by remember { mutableStateOf("All Levels") }
    val levels = listOf("All Levels", "S1", "S2", "S3", "S4 Science", "S4 Arts")

    val dummyCourses = listOf(
        TodayCourse("Mathematics", "08:00", "09:40", "Mr. Kamali", "Room A1"),
        TodayCourse("French",      "10:00", "11:40", "Mme. Mukamana", "Room B1"),
        TodayCourse("Physics",     "13:00", "14:40", "Mr. Nkurunziza", "Lab 1"),
    )

    Column(modifier = modifier.fillMaxWidth()) {

        /* header + weekly view */
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppSubHeader("📅 Today’s Schedule")
            TextButton(onClick = onWeeklyViewClick) {
                Text("Weekly View", style = MaterialTheme.typography.labelLarge)
            }
        }

        /* level / stream filter */
        Spacer(Modifier.height(4.dp))
        ExposedDropdownMenuBox(
            expanded = false,
            onExpandedChange = {}        // implement if full dropdown needed
        ) {
            Text(
                text = selectedFilter,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
            /* – implement real menu later – */
        }

        Spacer(Modifier.height(8.dp))

        /* schedule list */
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(dummyCourses.size){ index ->
                TodayScheduleCard(dummyCourses[index])
            }
        }
    }
}
// -----------------------------------------------------------------------------
//  💡  Dummy domain models (replace with real ones or ViewModel flows later)
// -----------------------------------------------------------------------------
data class Kpi(val title: String, val value: String, val icon: ImageVector)
data class TeacherActivity(val name: String, val pendingAttendance: Boolean, val overdueGrades: Boolean)
data class PendingGrade(val className: String, val subject: String, val teacher: String)
data class Sanction(val student: String, val reason: String, val date: String, val issuer: String)
data class ApprovalRequest(val title: String, val requester: String, val date: String)
data class Memo(val title: String, val author: String, val date: String)
data class ClassInfo(val level: String, val stream: String, val students: Int)
data class ImportantDate(val title: String, val date: String)

// -----------------------------------------------------------------------------
//  1️⃣  KPI SECTION
// -----------------------------------------------------------------------------
@Composable
fun AdminKpiSection(modifier: Modifier = Modifier) {
    val kpis = remember {
        listOf(
            Kpi("Students", "134", Icons.Default.Group),
            Kpi("Teachers", "18", Icons.Default.Person),
            Kpi("Sanctions Today", "3", Icons.Default.Warning),
            Kpi("Pending Grades", "5", Icons.Default.Grade)
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text("📊 Overview", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
            kpis.forEach { kpi ->
                Card(shape = RoundedCornerShape(14.dp), modifier = Modifier.width(140.dp)) {
                    Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(kpi.icon, null, Modifier.size(28.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(6.dp))
                        Text(kpi.value, style = MaterialTheme.typography.headlineSmall)
                        Text(kpi.title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
//  2️⃣  TEACHER ACTIVITY SUMMARY
// -----------------------------------------------------------------------------
@Composable
fun TeacherActivitySummarySection(modifier: Modifier = Modifier) {
    val teachers = remember {
        listOf(
            TeacherActivity("Mr. Kamali", pendingAttendance = true, overdueGrades = false),
            TeacherActivity("Ms. Uwase", pendingAttendance = false, overdueGrades = true),
            TeacherActivity("Mr. Habimana", pendingAttendance = true, overdueGrades = true)
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text("🧑‍🏫 Teacher Activity", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        teachers.forEach { t ->
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, null, Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(t.name, fontWeight = FontWeight.SemiBold)
                        Row {
                            if (t.pendingAttendance) TagChip("Attendance", MaterialTheme.colorScheme.error)
                            if (t.overdueGrades) TagChip("Grades", MaterialTheme.colorScheme.tertiary)
                        }
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                }
            }
        }
    }
}

@Composable
private fun TagChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .padding(end = 4.dp)
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
    ) {
        Text(text = text, color = color, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
    }
}

// -----------------------------------------------------------------------------
// 3️⃣  PENDING GRADES SECTION
// -----------------------------------------------------------------------------
@Composable
fun PendingGradesSection(modifier: Modifier = Modifier) {
    val pending = remember {
        listOf(
            PendingGrade("S4 Science", "Physics", "Mr. Nkurunziza"),
            PendingGrade("S2", "English", "Ms. Ingabire")
        )
    }
    Column(modifier = modifier.fillMaxWidth()) {
        Text("📑 Pending Grades", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        pending.forEach { p ->
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Row(Modifier.padding(12.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text("${p.className} - ${p.subject}", fontWeight = FontWeight.SemiBold)
                        Text("Teacher: ${p.teacher}", style = MaterialTheme.typography.labelSmall)
                    }
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 4️⃣  RECENT SANCTIONS SECTION
// -----------------------------------------------------------------------------
@Composable
fun RecentSanctionsSection(modifier: Modifier = Modifier) {
    val sanctions = remember {
        listOf(
            Sanction("NIYO Alpha", "Late to class", "Today", "Mr. Kamali"),
            Sanction("UMUHOZA Jane", "Disruptive", "Yesterday", "Ms. Uwase"),
        )
    }
    Column(modifier = modifier.fillMaxWidth()) {
        Text("⚖️ Recent Sanctions", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        sanctions.forEach { s ->
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Column(Modifier.padding(12.dp)) {
                    Text(s.student, fontWeight = FontWeight.SemiBold)
                    Text(s.reason, style = MaterialTheme.typography.labelSmall)
                    Text("${s.date} • by ${s.issuer}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 5️⃣  APPROVAL REQUESTS SECTION
// -----------------------------------------------------------------------------
@Composable
fun ApprovalRequestsSection(modifier: Modifier = Modifier) {
    val approvals = remember {
        listOf(
            ApprovalRequest("Leave Request - Mr. Niyomugabo", "Mr. Niyomugabo", "Today"),
            ApprovalRequest("Event: Debate Club", "Student Council", "Yesterday")
        )
    }
    Column(modifier = modifier.fillMaxWidth()) {
        Text("📥 Approval Requests", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        approvals.forEach { a ->
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(a.title, fontWeight = FontWeight.SemiBold)
                        Text(a.date, style = MaterialTheme.typography.labelSmall)
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 6️⃣  INTERNAL MEMOS SECTION
// -----------------------------------------------------------------------------
@Composable
fun InternalMemosSection(modifier: Modifier = Modifier) {
    val memos = remember {
        listOf(
            Memo("Staff Meeting Notes", "Headmaster", "Mon"),
            Memo("New Hygiene Guidelines", "School Nurse", "Fri")
        )
    }
    Column(modifier = modifier.fillMaxWidth()) {
        Text("📝 Internal Memos", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        memos.forEach { m ->
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Row(Modifier.padding(12.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text(m.title, fontWeight = FontWeight.SemiBold)
                        Text("${m.author} • ${m.date}", style = MaterialTheme.typography.labelSmall)
                    }
                    Icon(Icons.Default.ArrowForward, null)
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 7️⃣  STUDENT EXPLORER SECTION (compact)
// -----------------------------------------------------------------------------
@Composable
fun StudentExplorerSection(modifier: Modifier = Modifier) {
    val classes = remember {
        listOf(
            ClassInfo("S1", "A", 40),
            ClassInfo("S2", "B", 38),
            ClassInfo("S4", "Science", 30)
        )
    }
    Column(modifier = modifier.fillMaxWidth()) {
        Text("👥 Classes & Students", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(classes.size){ index ->
                Card(shape = RoundedCornerShape(14.dp), modifier = Modifier.width(120.dp)) {
                    Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${classes[index].level} ${classes[index].stream}", fontWeight = FontWeight.SemiBold)
                        Text("${classes[index].students} students", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 8️⃣  ACADEMIC CALENDAR SECTION
// -----------------------------------------------------------------------------
@Composable
fun AcademicCalendarSection(modifier: Modifier = Modifier) {
    val dates = remember {
        listOf(
            ImportantDate("Mid-term Exams", "27 Jun 2025"),
            ImportantDate("Parents' Meeting", "02 Jul 2025"),
            ImportantDate("Term Break", "15 Jul 2025")
        )
    }
    Column(modifier = modifier.fillMaxWidth()) {
        Text("📅 Important Dates", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        dates.forEach { d ->
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Row(Modifier.padding(12.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text(d.title, fontWeight = FontWeight.SemiBold)
                        Text(d.date, style = MaterialTheme.typography.labelSmall)
                    }
                    Icon(Icons.Default.ArrowForward, null)
                }
            }
        }
    }
}
