package com.schoolbridge.v2.ui.home


import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.schoolbridge.v2.R
import com.schoolbridge.v2.components.CustomBottomNavBar
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.domain.academic.TodayCourse
import com.schoolbridge.v2.domain.academic.teacher.QuickActionViewModel
import com.schoolbridge.v2.domain.messaging.Alert
import com.schoolbridge.v2.domain.user.CurrentUser
import com.schoolbridge.v2.domain.user.UserRole
import com.schoolbridge.v2.ideatrials.CoursesScreen
import com.schoolbridge.v2.ideatrials.DistrictsScreen
import com.schoolbridge.v2.mqtt.SimpleMqttClient
import com.schoolbridge.v2.ui.components.AppSubHeader
import com.schoolbridge.v2.ui.components.SpacerL
import com.schoolbridge.v2.ui.components.SpacerM
import com.schoolbridge.v2.ui.components.SpacerS
import com.schoolbridge.v2.ui.home.alert.AlertDetailsBottomSheetContent
import com.schoolbridge.v2.ui.home.alert.AlertsSection
import com.schoolbridge.v2.ui.home.common.ActionCard
import com.schoolbridge.v2.ui.home.common.AddressCard
import com.schoolbridge.v2.ui.home.common.GenderTag
import com.schoolbridge.v2.ui.home.common.LinkedStudentRow
import com.schoolbridge.v2.ui.home.common.LocalTimetableRepo
import com.schoolbridge.v2.ui.home.common.TimetableBoard
import com.schoolbridge.v2.ui.home.course.CourseListSection
import com.schoolbridge.v2.ui.home.event.EventsSection
import com.schoolbridge.v2.ui.home.grade.GradesSummarySection
import com.schoolbridge.v2.ui.home.role.RoleSelectorBottomSheet
import com.schoolbridge.v2.ui.home.schedule.TodayScheduleCard
import com.schoolbridge.v2.ui.home.schedule.TodayScheduleSection
import com.schoolbridge.v2.ui.home.student.StudentListSection
import com.schoolbridge.v2.ui.home.teacher.TeacherQuickActionsSection
import com.schoolbridge.v2.ui.navigation.MainAppScreen
import kotlinx.coroutines.launch
import com.schoolbridge.v2.ui.home.decoration.GlowingTopBarBackground
import com.schoolbridge.v2.ui.home.decoration.GlowingGradientBackground
import com.schoolbridge.v2.util.dummyCourses
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.eclipse.paho.client.mqttv3.*


@Composable
fun AltHero(currentUser: CurrentUser?) {
    val name = currentUser?.lastName ?: ""
    val gender = currentUser?.gender
    val colors = listOf(
        colorScheme.primary,
        colorScheme.secondary
    )

    val textColor = colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(Color.Transparent)
    ) {
        // 1️⃣ Background Image
        Image(
            painter = painterResource(id = R.drawable.ic_bridge),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.Center)
                .size(500.dp)
                .offset(x = (-60).dp, y = (-40).dp)
                .alpha(0.08f)
        )

        // 2️⃣ Glowy Circles
        listOf(300.dp, 200.dp, 260.dp).forEachIndexed { i, size ->
            Box(
                modifier = Modifier
                    .size(size)
                    .blur(120.dp)
                    .graphicsLayer { alpha = 0.55f }
                    .offset(x = (-40 + i * 80).dp, y = (-20 + i * 60).dp)
                    .background(
                        Brush.radialGradient(colors + Color.Transparent),
                        shape = CircleShape
                    )
            )
        }

        // 3️⃣ 🟣 Gradient overlay for text readability
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(100.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            colorScheme.surface.copy(alpha = 0.8f),
                            colorScheme.surface.copy(alpha = 0.0f)
                        ),
                        startY = Float.POSITIVE_INFINITY,
                        endY = 0f
                    )
                )
        )

        // 4️⃣ Welcome Text
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            val genderPrefix = when (gender?.name) {
                "MALE" -> "Mr."
                "FEMALE" -> "Ms."
                else -> ""
            }
            val userP = if (name.isBlank()) "there" else "$genderPrefix $name"

            Text(
                text = "Hey $userP 👋",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = textColor,
                    fontWeight = FontWeight.SemiBold
                )
            )
            SpacerS()
            Text(
                text = "Welcome to SchoolBridge",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = textColor.copy(alpha = 0.85f)
                )
            )
        }
    }
}




/* ──────────────────────────────────────────────────────────────────────────────
 *  1️⃣  Top-bar with a Role “combo-box”
 * ────────────────────────────────────────────────────────────────────────────── */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    currentRole: UserRole?,
    availableRoles: Set<UserRole>,
    onRoleSelected: (UserRole) -> Unit,
    onSettingsClick: () -> Unit,
    onRequestNewRole: () -> Unit,
    modifier: Modifier = Modifier
) {
    val showRoleSheet = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    if (showRoleSheet.value) {
        RoleSelectorBottomSheet(
            currentRole = currentRole,
            availableRoles = availableRoles,
            onRoleSelected = {
                onRoleSelected(it)
                showRoleSheet.value = false
            },
            onRequestNewRole = {
                onRequestNewRole()
                showRoleSheet.value = false
            },
            onDismiss = { showRoleSheet.value = false }
        )
    }

    Box {
        GlowingTopBarBackground()
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AnimatedVisibility(
                        visible = availableRoles.size > 1 && currentRole != null
                    ) {
                        TextButton(onClick = { showRoleSheet.value = true }) {
                            Text(currentRole?.humanLabel ?: "")
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Change role"
                            )
                        }
                    }
                }
            },
            actions = {
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = colorScheme.onSurface,
                actionIconContentColor = colorScheme.onSurface
            ),
            modifier = modifier
        )
    }
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
    onRequestNewRole: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by userSessionManager.currentUser.collectAsStateWithLifecycle(initialValue = null)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var selectedAlert by remember { mutableStateOf<Alert?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        // ✨ Place the glow gradient behind everything

        GlowingGradientBackground(currentRole = currentUser?.currentRole)
        Scaffold(
            topBar = {
                HomeTopBar(
                    currentRole = currentUser?.currentRole,
                    availableRoles = currentUser?.activeRoles ?: emptySet(),
                    onRoleSelected = { selectedRole ->
                        scope.launch { userSessionManager.setCurrentRole(selectedRole) }
                    },
                    onSettingsClick = onSettingsClick,
                    onRequestNewRole = onRequestNewRole,
                    modifier = modifier
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
                    .fillMaxSize(),
                userSessionManager = userSessionManager
            )
        }

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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HomeUI(
    currentUser: CurrentUser?,
    onViewAllAlertsClick: () -> Unit,
    onViewAllEventsClick: () -> Unit,
    onWeeklyViewClick: () -> Unit,
    onEventClick: (String) -> Unit,
    onAlertClick: (Alert) -> Unit,
    userSessionManager: UserSessionManager,
    modifier: Modifier = Modifier
) {
    var activeRole = currentUser?.currentRole
    var authToken by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(userSessionManager) {
        authToken = userSessionManager.getAuthToken()
    }
    val mqtt = remember { SimpleMqttClient() }
    val context = LocalContext.current
    Log.d("HomeUI", "Active role: $activeRole")
    if (activeRole == null) {
        if (currentUser != null) {
            if (currentUser.activeRoles.isNotEmpty()) {
                Log.d("HomeUI", "Active roles: ${currentUser.activeRoles}")
                currentUser.currentRole = currentUser.activeRoles.first()
                activeRole = currentUser.currentRole
            }
        }
    }
    Column(
        modifier = modifier
            .background(Color.Transparent)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AltHero(currentUser = currentUser)

        OutlinedButton(onClick = {
            mqtt.connect(
                onConnected = {
                    //mqtt.subscribe("schoolbridge/test")
                    mqtt.subscribe("schoolbridge/#")
                    mqtt.publish("schoolbridge/test", "Hello from Android 🚀")
                    Toast.makeText(context, "Connected to MQTT", Toast.LENGTH_SHORT).show()
                    Toast.makeText(context, "Subscribing to schoolbridge/#", Toast.LENGTH_SHORT).show()
                    Toast.makeText(context, "Publishing to schoolbridge/test", Toast.LENGTH_SHORT).show()
                    Log.d("MQTT", "Connected to MQTT")
                },
                onMessage = { topic, message ->
                    Toast.makeText(context, "Got message from $topic: $message", Toast.LENGTH_SHORT).show()
                    Log.d("MQTT", "Got message from $topic: $message")
                }
            )
        }) {
            Text("Test MQTT")
        }

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
                if(authToken != null){
                    DistrictsScreen(
                        context = LocalContext.current,
                        token = authToken!!
                    )

                    CoursesScreen(
                        context = LocalContext.current,
                        token = authToken!!
                    )
                }

                AuthTokenDisplay(userSessionManager = userSessionManager)
            }


            /*UserRole.PARENT -> {
                val timetable by LocalTimetableRepo.cachedTimetable.collectAsState()

                LaunchedEffect(Unit) {
                    LocalTimetableRepo.loadMockDataForTesting() // Temporary for demo
                }

                if (timetable.isNotEmpty()) {
                    SpacerL()
                    Text("Today's Timetable", style = MaterialTheme.typography.titleMedium)
                    TimetableBoard(entries = timetable)
                }
               currentUser?.let {
                   // 1️⃣ Address and Gender
                   if (it.address != null) {
                       //AddressCard(address = it.address)
                       SpacerS()
                   }
                   if (it.gender != null) {
                       GenderTag(gender = it.gender)
                       SpacerS()
                   }

                   // 2️⃣ Linked Students
                   if (!it.linkedStudents.isNullOrEmpty()) {
                       Text(
                           text = "Linked Students",
                           style = MaterialTheme.typography.titleMedium,
                           modifier = Modifier.padding(vertical = 8.dp)
                       )
                       LinkedStudentRow(list = it.linkedStudents!!)
                       SpacerL()
                   }
               }

               // 3️⃣ Alerts and Events
               AlertsSection(
                   onViewAllAlertsClick = onViewAllAlertsClick,
                   onAlertClick = onAlertClick
               )
               SpacerL()

               EventsSection(
                   onViewAllEventsClick = onViewAllEventsClick,
                   onEventClick = onEventClick
               )

               // 4️⃣ Timetable for Parent's Children
               val timetable by LocalTimetableRepo.current.cachedTimetable.collectAsState()
               if (timetable.isNotEmpty()) {
                   SpacerL()
                   Text("Today's Timetable", style = MaterialTheme.typography.titleMedium)
                   TimetableBoard(entries = timetable)
               }
           }*/
            UserRole.PARENT -> {
                currentUser?.let {
                    // 1️⃣ Address and Gender
                    if (it.address != null) {
                        Text(
                            text = "Your Address",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                        AddressCard(address = it.address)
                        SpacerM()
                    }

                    if (it.gender != null) {
                        GenderTag(gender = it.gender)
                        SpacerM()
                    }

                    // 2️⃣ Linked Students
                    if (!it.linkedStudents.isNullOrEmpty()) {
                        Text(
                            text = "Linked Students",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                        LinkedStudentRow(list = it.linkedStudents)
                        SpacerL()
                    }
                }

                // 3️⃣ Alerts and Events
                AlertsSection(
                    onViewAllAlertsClick = onViewAllAlertsClick,
                    onAlertClick = onAlertClick
                )
                SpacerL()

                EventsSection(
                    onViewAllEventsClick = onViewAllEventsClick,
                    onEventClick = onEventClick
                )

                // 4️⃣ Timetable for Parent's Children
                val timetable by LocalTimetableRepo.cachedTimetable.collectAsState()

                LaunchedEffect(Unit) {
                    // In production, you'd load timetable for selected child
                    LocalTimetableRepo.loadMockDataForTesting()
                }

                if (timetable.isNotEmpty()) {
                    SpacerL()
                    Text(
                        text = "Today's Timetable",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    TimetableBoard(entries = timetable)
                }
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
                AdminTodayScheduleSection(onWeeklyViewClick = onWeeklyViewClick)
                SpacerL()
                PendingGradesSection()
                SpacerM()
                RecentSanctionsSection()
                SpacerM()
                ApprovalRequestsSection()
                SpacerM()
                InternalMemosSection()
                SpacerM()
                TeacherActivitySummarySection()
                SpacerM()
                AcademicOverviewSection()
                SpacerM()
                StudentExplorerSection()
                SpacerM()
                AcademicCalendarSection()
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
            else -> {}
        }
    }
}



@Composable
fun AdminQuickActionsSection() {
    val viewModel: QuickActionViewModel = viewModel()
    val chosenIds by viewModel.selected.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    val chosenActions = adminQuickActions.filter { it.id in chosenIds }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        if (chosenActions.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quick Admin Actions",
                    style = MaterialTheme.typography.titleMedium,
                    color = colorScheme.primary
                )
                TextButton(onClick = { showDialog = true }) {
                    Text("Customise")
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 4.dp)
            ) {
                chosenActions.forEach { action ->
                    ActionCard(
                        title = action.title,
                        icon = action.icon,
                        onClick = action.onClick
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.Warning, null, tint = colorScheme.outline.copy(alpha = 0.2f), modifier = Modifier.size(28.dp))
                        Icon(Icons.Default.Groups, null, tint = colorScheme.outline.copy(alpha = 0.2f), modifier = Modifier.size(28.dp))
                        Icon(Icons.AutoMirrored.Filled.EventNote, null, tint = colorScheme.outline.copy(alpha = 0.2f), modifier = Modifier.size(28.dp))
                    }

                    Spacer(Modifier.height(8.dp))
                    Icon(Icons.Default.Info, null, tint = colorScheme.outline, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.height(4.dp))
                    Text("No quick actions selected.", style = MaterialTheme.typography.bodyMedium, color = colorScheme.outline)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "You can bookmark tools like sanctions, permissions,\nor planning for quick access.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.outline,
                        textAlign = TextAlign.Center,
                        lineHeight = 17.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(onClick = { showDialog = true }) {
                        Text("Add Quick Actions")
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton({ showDialog = false }) { Text("Done") }
            },
            title = { Text("Choose Quick Actions") },
            text = {
                LazyColumn(Modifier.heightIn(max = 400.dp).padding(end = 8.dp)) {
                    items(adminQuickActions.size) { index ->
                        val action = adminQuickActions[index]
                        val checked = action.id in chosenIds
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.toggle(action.id) }
                                .padding(8.dp)
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { viewModel.toggle(action.id) }
                            )
                            Spacer(Modifier.width(8.dp))
                            Icon(action.icon, contentDescription = null)
                            Spacer(Modifier.width(12.dp))
                            Text(action.title, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun AuthTokenDisplay(userSessionManager: UserSessionManager) {
    // Collect the authentication token as a State.
    // We use LaunchedEffect to perform the suspend call once and update the state.
    // In a real app, you might have this token exposed directly from a ViewModel
    // as a StateFlow for more reactive updates.
    var authToken by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userSessionManager) {
        authToken = userSessionManager.getAuthToken()
    }

    Card(
        modifier = Modifier.padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Authentication Token:",
                style = MaterialTheme.typography.titleSmall,
                color = colorScheme.onSurfaceVariant
            )
            Text(
                text = authToken ?: "No token available",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )
            if (authToken == null) {
                Text(
                    text = "Log in to see the token.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp)
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
                        colorScheme.primary.copy(alpha = 0.12f),
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
        Text("🧑‍🏫 Teacher Activity", style = MaterialTheme.typography.titleMedium, color = colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        teachers.forEach { t ->
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, null, Modifier.size(24.dp), tint = colorScheme.primary)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(t.name, fontWeight = FontWeight.SemiBold)
                        Row {
                            if (t.pendingAttendance) TagChip("Attendance", colorScheme.error)
                            if (t.overdueGrades) TagChip("Grades", colorScheme.tertiary)
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
        Text("📑 Pending Grades", style = MaterialTheme.typography.titleMedium, color = colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        pending.forEach { p ->
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Row(Modifier.padding(12.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text("${p.className} - ${p.subject}", fontWeight = FontWeight.SemiBold)
                        Text("Teacher: ${p.teacher}", style = MaterialTheme.typography.labelSmall)
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
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
        Text("⚖️ Recent Sanctions", style = MaterialTheme.typography.titleMedium, color = colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        sanctions.forEach { s ->
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Column(Modifier.padding(12.dp)) {
                    Text(s.student, fontWeight = FontWeight.SemiBold)
                    Text(s.reason, style = MaterialTheme.typography.labelSmall)
                    Text("${s.date} • by ${s.issuer}", style = MaterialTheme.typography.labelSmall, color = colorScheme.onSurfaceVariant)
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
        Text("📥 Approval Requests", style = MaterialTheme.typography.titleMedium, color = colorScheme.primary)
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
        Text("📝 Internal Memos", style = MaterialTheme.typography.titleMedium, color = colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        memos.forEach { m ->
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Row(Modifier.padding(12.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text(m.title, fontWeight = FontWeight.SemiBold)
                        Text("${m.author} • ${m.date}", style = MaterialTheme.typography.labelSmall)
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
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
            ClassAttendanceStats("S1", "A", 40, 36, mapOf("Medical" to 1, "Permission" to 2, "Sanctioned" to 1)),
            ClassAttendanceStats("S2", "B", 38, 38, emptyMap()),
            ClassAttendanceStats("S4", "Science", 30, 27, mapOf("Medical" to 2, "Sanctioned" to 1))
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            "👥 Class Attendance",
            style = MaterialTheme.typography.titleMedium,
            color = colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            //modifier = Modifier.animateItemPlacement()
        ) {
            items(classes.size){index->
                AnimatedVisibility(visible = true) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .width(160.dp)
                            .heightIn(min = 120.dp),
                        //.animateItemPlacement(),
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.surface,
                            contentColor = colorScheme.onSurface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                "${classes[index].level} ${classes[index].stream}",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                "${classes[index].present}/${classes[index].total} present",
                                style = MaterialTheme.typography.labelSmall,
                                color = colorScheme.onSurfaceVariant
                            )

                            Spacer(Modifier.height(6.dp))

                            if (classes[index].absentReasons.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    classes[index].absentReasons.forEach { (reason, count) ->
                                        Text(
                                            text = "$reason: $count",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = colorScheme.secondary
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    "✅ Full attendance",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


data class ClassAttendanceStats(
    val level: String,
    val stream: String,
    val total: Int,
    val present: Int,
    val absentReasons: Map<String, Int>
)


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
        Text("📅 Important Dates", style = MaterialTheme.typography.titleMedium, color = colorScheme.primary)
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



// 📝 ACADEMIC OVERVIEW SECTION
@Composable
fun AcademicOverviewSection(modifier: Modifier = Modifier) {
    val classSummaries = listOf(
        ClassAcademicSummary("S4 Science", avg = 72.5, topScore = 91.3),
        ClassAcademicSummary("S2 B", avg = 64.7, topScore = 89.0),
        ClassAcademicSummary("S1 A", avg = 58.9, topScore = 78.4)
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Text("📊 Academic Summary", style = MaterialTheme.typography.titleMedium, color = colorScheme.primary)
        Spacer(Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(classSummaries.size){index->
                Card(shape = RoundedCornerShape(14.dp), modifier = Modifier.width(180.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(classSummaries[index].className, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text("Avg: ${classSummaries[index].avg}%", style = MaterialTheme.typography.labelSmall)
                        Text("Top: ${classSummaries[index].topScore}%", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

data class ClassAcademicSummary(
    val className: String,
    val avg: Double,
    val topScore: Double
)



