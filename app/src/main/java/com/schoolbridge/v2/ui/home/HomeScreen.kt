package com.schoolbridge.v2.ui.home // Adjust package as needed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.schoolbridge.v2.R
import com.schoolbridge.v2.components.CustomBottomNavBar
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.domain.academic.Course
import com.schoolbridge.v2.domain.messaging.Alert
import com.schoolbridge.v2.domain.user.CurrentUser
import com.schoolbridge.v2.localization.t
import com.schoolbridge.v2.ui.common.components.AppSubHeader
import com.schoolbridge.v2.ui.common.components.SpacerL
import com.schoolbridge.v2.ui.common.components.SpacerS
import com.schoolbridge.v2.ui.home.alert.AlertDetailsBottomSheetContent
import com.schoolbridge.v2.ui.home.alert.AlertsSection
import com.schoolbridge.v2.ui.home.event.EventsSection
import com.schoolbridge.v2.ui.home.student.StudentCard
import com.schoolbridge.v2.ui.home.student.StudentListSection
import com.schoolbridge.v2.ui.navigation.MainAppScreen
import com.schoolbridge.v2.ui.onboarding.shared.MainNavScreen
import kotlinx.coroutines.launch
import java.time.LocalDate


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

@Preview
@Composable
private fun HomeRoutePreview() {
    val context = LocalContext.current
    HomeRoute(
        userSessionManager = UserSessionManager(
            context = context,
        ),
        currentScreen = MainAppScreen.Home,
        onTabSelected = {},
        onSettingsClick = {},
        onViewAllAlertsClick = {},
        onViewAllEventsClick = {},
        onEventClick = {},

    )
}



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
        if (currentUser?.isStudent() == true) {
            CourseListSection()

            SpacerL()

            TodayScheduleSection() // 🆕 Suggested: compact daily schedule preview

            SpacerL()

            AlertsSection(
                onViewAllAlertsClick = onViewAllAlertsClick,
                onAlertClick = onAlertClick
            )

            SpacerL()

            GradesSummarySection() // 🆕 Suggested: show recent marks released (see below)

        } else {
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



@Composable
fun CourseListSection(
    modifier: Modifier = Modifier
) {
    val dummyCourses = listOf(
        CourseWithStatus(
            course = Course(
                id = "c1",
                name = "Mathematics Grade 10",
                description = "Comprehensive study of algebra and geometry.",
                subjectId = "subj-math",
                academicYearId = "AY2024-2025",
                schoolLevelOfferingId = "slo-g10-sci",
                teacherUserIds = listOf("teacher1"),
                startDate = LocalDate.of(2024, 9, 1),
                endDate = LocalDate.of(2025, 5, 30),
                isActive = false
            ),
            status = CourseStatus.VALIDATED
        ),
        CourseWithStatus(
            course = Course(
                id = "c2",
                name = "Biology S3",
                description = "Detailed study of living organisms and life processes.",
                subjectId = "subj-bio",
                academicYearId = "AY2024-2025",
                schoolLevelOfferingId = "slo-s3-sci",
                teacherUserIds = listOf("teacher2"),
                startDate = LocalDate.of(2024, 9, 1),
                endDate = LocalDate.of(2025, 5, 30),
                isActive = true
            ),
            status = CourseStatus.IN_PROGRESS
        ),
        CourseWithStatus(
            course = Course(
                id = "c3",
                name = "English Literature",
                description = "Exploration of classic and modern literary works.",
                subjectId = "subj-eng",
                academicYearId = "AY2024-2025",
                schoolLevelOfferingId = "slo-g10-art",
                teacherUserIds = listOf("teacher3"),
                startDate = LocalDate.of(2024, 9, 1),
                endDate = LocalDate.of(2025, 5, 30),
                isActive = false
            ),
            status = CourseStatus.RETAKE_REQUIRED
        ),
        CourseWithStatus(
            course = Course(
                id = "c4",
                name = "History Grade 10",
                description = "A survey of world history with focus on Africa.",
                subjectId = "subj-hist",
                academicYearId = "AY2024-2025",
                schoolLevelOfferingId = "slo-g10-art",
                teacherUserIds = listOf("teacher4"),
                startDate = LocalDate.of(2024, 9, 1),
                endDate = LocalDate.of(2025, 5, 30),
                isActive = false
            ),
            status = CourseStatus.NOT_VALIDATED
        ),
        CourseWithStatus(
            course = Course(
                id = "c5",
                name = "Physics S5",
                description = "Advanced mechanics, electricity, and waves.",
                subjectId = "subj-phys",
                academicYearId = "AY2024-2025",
                schoolLevelOfferingId = "slo-s5-sci",
                teacherUserIds = listOf("teacher2"),
                startDate = LocalDate.of(2024, 9, 1),
                endDate = LocalDate.of(2025, 5, 30),
                isActive = false
            ),
            status = CourseStatus.AWAITING_RESULTS
        )
    )


    Row(modifier = modifier.fillMaxWidth()) {
        AppSubHeader("📚 " + "Courses")
    }
    SpacerS()
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        itemsIndexed(dummyCourses) { _, courseWithStatus ->
            CourseCard(courseWithStatus)
        }
    }


}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CourseCard(courseWithStatus: CourseWithStatus, modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(false) }
    val course = courseWithStatus.course

    LaunchedEffect(Unit) {
        visible = true
    }

    val teacherNames = course.teacherUserIds.joinToString(", ") { dummyTeacherNames[it] ?: "Unknown" }

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
                    // Circle with first letter of course name or an icon
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = course.name.firstOrNull()?.uppercase() ?: "?",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = course.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = course.description ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 3
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Teacher(s): $teacherNames",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    CourseStatusBadge(courseWithStatus.status)
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CourseCardPreview() {
    MaterialTheme {
        CourseCard(
            courseWithStatus = CourseWithStatus(
                course = Course(
                    id = "c1",
                    name = "Mathematics Grade 10",
                    description = "Comprehensive study of algebra and geometry.",
                    subjectId = "subj-math",
                    academicYearId = "AY2024-2025",
                    schoolLevelOfferingId = "slo-g10-sci",
                    teacherUserIds = listOf("teacher1"),
                    startDate = LocalDate.of(2024, 9, 1),
                    endDate = LocalDate.of(2025, 5, 30),
                    isActive = false
                ),
                status = CourseStatus.VALIDATED,
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}




// Dummy data: Map teacherUserIds to names for display (in real app this would come from user repository)
val dummyTeacherNames = mapOf(
    "teacher1" to "Mr. Kamali",
    "teacher2" to "Ms. Uwase",
    "teacher3" to "Mrs. Mukeshimana",
    "teacher4" to "Mr. Habimana"
)

// Extend your Course with a dummy flag for finished + validated (since not in your class)
data class CourseWithStatus(
    val course: Course,
    val status: CourseStatus
)

enum class CourseStatus {
    VALIDATED,           // Student completed and passed
    NOT_VALIDATED,       // Student failed
    IN_PROGRESS,         // Ongoing
    RETAKE_REQUIRED,     // Failed and required to retake
    AWAITING_RESULTS     // Finished but not yet validated
}

@Composable
fun CourseStatusBadge(status: CourseStatus) {
    val (label, color, icon) = when (status) {
        CourseStatus.VALIDATED -> Triple("VALIDATED", Color(0xFF4CAF50), Icons.Default.CheckCircle)
        CourseStatus.NOT_VALIDATED -> Triple("FAILED", Color(0xFFFF5722), Icons.Default.Warning)
        CourseStatus.RETAKE_REQUIRED -> Triple("RETAKE", Color(0xFFF44336), Icons.Default.Warning)
        CourseStatus.IN_PROGRESS -> Triple("IN PROGRESS", MaterialTheme.colorScheme.primary, Icons.Default.Notifications)
        CourseStatus.AWAITING_RESULTS -> Triple("PENDING", Color(0xFF03A9F4), Icons.Default.Info)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = color,
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(modifier = Modifier.width(6.dp))
        Icon(
            imageVector = icon,
            contentDescription = "Course Status",
            tint = color,
            modifier = Modifier.size(16.dp)
        )
    }
}


@Composable
fun TodayScheduleSection(modifier: Modifier = Modifier) {
    val dummySchedules = listOf(
        TodayCourse("Mathematics", "08:00", "09:40", "Mr. Kamali", "Room A1"),
        TodayCourse("Chemistry", "10:00", "11:40", "Ms. Uwase", "Lab 3"),
        TodayCourse("History", "13:00", "14:40", "Mr. Habimana", "Room B2")
    )

    Column(modifier = modifier.fillMaxWidth()) {
        AppSubHeader("📅 Today’s Schedule")
        SpacerS()
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            dummySchedules.forEach { dummySchedule ->
                item{
                    TodayScheduleCard(dummySchedule)
                }
            }
        }
    }
}

data class TodayCourse(
    val subject: String,
    val startTime: String,
    val endTime: String,
    val teacher: String,
    val location: String
)


@Composable
fun TodayScheduleCard(
    course: TodayCourse,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Timeline marker: dot + line
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                )
            }

            // Main Info Column
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    text = course.subject,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${course.startTime} - ${course.endTime}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = course.teacher,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = course.location,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
//
//@Preview(showBackground = true)
//@Composable
//fun TodayScheduleTimelineCardPreview() {
//    TodayScheduleCard(
//        course = TodayCourse(
//            subject = "Physics",
//            startTime = "10:00",
//            endTime = "11:30",
//            teacher = "Ms. Uwase",
//            location = "Lab 2"
//        )
//    )
//}



@Composable
fun GradesSummarySection(modifier: Modifier = Modifier) {
    val dummyGrades = listOf(
        GradeSummary("Mathematics", 87, "Mr. Kamali"),
        GradeSummary("Biology", 61, "Ms. Uwase"),
        GradeSummary("English", 45, "Mrs. Mukeshimana")
    )

    Column(modifier = modifier.fillMaxWidth()) {
        AppSubHeader("📊 Recent Grades")
        SpacerS()
        dummyGrades.forEachIndexed { index, grade ->
            GradeCardCompact(grade, index)
        }
    }
}

data class GradeSummary(
    val subject: String,
    val score: Int,
    val teacher: String
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GradeCardCompact(
    grade: GradeSummary,
    index: Int,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val (color, icon) = when {
        grade.score >= 75 -> MaterialTheme.colorScheme.primary to Icons.Default.CheckCircle
        grade.score >= 50 -> MaterialTheme.colorScheme.secondary to Icons.Default.Info
        else -> MaterialTheme.colorScheme.error to Icons.Default.Warning
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(400)) + slideInVertically(tween(400, delayMillis = index * 60))
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .fillMaxHeight()
                        .background(color, shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                )

                Spacer(Modifier.width(10.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(12.dp)
                ) {
                    Text(text = grade.subject, style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Score: ${grade.score}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Teacher: ${grade.teacher}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .align(Alignment.CenterVertically)
                        .size(24.dp)
                )
            }
        }
    }
}
