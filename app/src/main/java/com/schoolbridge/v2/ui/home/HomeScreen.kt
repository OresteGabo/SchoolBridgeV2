package com.schoolbridge.v2.ui.home // Adjust package as needed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
            // Show courses list instead of students
            CourseListSection()
        } else {
            StudentListSection(students = currentUser?.linkedStudents)
        }

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
            isFinishedAndValidated = true
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
            isFinishedAndValidated = false
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
            isFinishedAndValidated = true
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

                    if (courseWithStatus.isFinishedAndValidated) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Finished and validated",
                            tint = Color(0xFF4CAF50), // Green 500
                            modifier = Modifier.size(36.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.size(36.dp))
                    }
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
                isFinishedAndValidated = true
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
    val isFinishedAndValidated: Boolean
)