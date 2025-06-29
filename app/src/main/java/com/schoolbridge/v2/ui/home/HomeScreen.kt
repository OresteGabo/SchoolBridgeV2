package com.schoolbridge.v2.ui.home


import android.util.Log
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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
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
import com.schoolbridge.v2.domain.academic.Course
import com.schoolbridge.v2.domain.academic.TodayCourse
import com.schoolbridge.v2.domain.academic.teacher.QuickActionViewModel
import com.schoolbridge.v2.domain.messaging.Alert
import com.schoolbridge.v2.domain.school.SchoolLevelOffering
import com.schoolbridge.v2.domain.user.CurrentUser
import com.schoolbridge.v2.domain.user.UserRole
import com.schoolbridge.v2.ui.components.AppSubHeader
import com.schoolbridge.v2.ui.components.SpacerL
import com.schoolbridge.v2.ui.components.SpacerM
import com.schoolbridge.v2.ui.components.SpacerS
import com.schoolbridge.v2.ui.home.alert.AlertDetailsBottomSheetContent
import com.schoolbridge.v2.ui.home.alert.AlertsSection
import com.schoolbridge.v2.ui.home.common.ActionCard
import com.schoolbridge.v2.ui.home.course.CourseListSection
import com.schoolbridge.v2.ui.home.event.EventsSection
import com.schoolbridge.v2.ui.home.grade.GradesSummarySection
import com.schoolbridge.v2.ui.home.schedule.TodayScheduleCard
import com.schoolbridge.v2.ui.home.schedule.TodayScheduleSection
import com.schoolbridge.v2.ui.home.student.StudentListSection
import com.schoolbridge.v2.ui.home.teacher.TeacherQuickActionsSection
import com.schoolbridge.v2.ui.navigation.MainAppScreen
import com.schoolbridge.v2.util.sampleOfferings
import kotlinx.coroutines.launch

@Composable
fun M_TopBanner(role: UserRole) {
    val g = when (role) {
        UserRole.STUDENT -> listOf(Color(0xFF5C6BC0), Color(0xFF3949AB))
        UserRole.PARENT -> listOf(Color(0xFFFFA270), Color(0xFFFF7043))
        UserRole.TEACHER -> listOf(Color(0xFF4DD0E1), Color(0xFF0097A7))
        UserRole.SCHOOL_ADMIN -> listOf(Color(0xFF66BB6A), Color(0xFF2E7D32))
        UserRole.GUEST ->listOf(Color(0xFF66BB6A), Color(0xFF2E7D32))
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(MaterialTheme.shapes.large)
            .background(Brush.horizontalGradient(g))
            .padding(20.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.ic_bridge), // ensure ic_bridge is in res/drawable
                contentDescription = "Bridge icon",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Bridge to Learning", style = MaterialTheme.typography.labelMedium.copy(color = Color.White))
                Text(
                    "Dashboard · ${role.name}",
                    style = MaterialTheme.typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

/*@Composable
fun AltHero() {
    val colors = listOf(colorScheme.primary, colorScheme.secondary,)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(Color.Transparent)
    )
    {
        // blobby gradient circles
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

        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text("Hey there 👋", style = MaterialTheme.typography.headlineSmall.copy(color = Color.White, fontWeight = FontWeight.SemiBold))
            SpacerS()
            Text("Welcome SchoolBridge", style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.9f)))
        }
    }
}
*/
@Composable
fun AltHero() {
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(Color.Transparent)
    ) {
        // 1️⃣ Bridge Background Image (low opacity, aesthetic only)
        Image(
            painter = painterResource(id = R.drawable.ic_bridge),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.Center)
                .size(500.dp) // intentionally large to overflow and feel abstract
                .offset(x = (-60).dp, y = (-40).dp)
                .alpha(0.08f) // barely visible
        )

        // 2️⃣ Glowy Gradient Circles
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

        // 3️⃣ Welcome Text
        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                "Hey there 👋",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            )
            SpacerS()
            Text(
                "Welcome to SchoolBridge",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.9f)
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
private fun HomeTopBar(
    currentRole: UserRole?,
    availableRoles: Set<UserRole>,
    onRoleSelected: (UserRole) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuOpen by remember { mutableStateOf(false) }
    Box {
        GlowingTopBarBackground()
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    //Text("SchoolBridge")
                    //Spacer(Modifier.width(8.dp))

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
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "Switch Role",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = colorScheme.primary
                                        )
                                    },
                                    onClick = {},
                                    enabled = false,
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                                )

                                HorizontalDivider()

                                availableRoles.forEach { role ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(
                                                    role.humanLabel,
                                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                                )
                                                Text(
                                                    text = when (role) {
                                                        UserRole.PARENT -> "Monitor your children’s progress"
                                                        UserRole.STUDENT -> "View your courses and schedule"
                                                        UserRole.TEACHER -> "Manage your classes"
                                                        UserRole.SCHOOL_ADMIN -> "School administration tools"
                                                        else -> ""
                                                    },
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = colorScheme.onSurfaceVariant
                                                )
                                            }
                                        },
                                        trailingIcon = {
                                            if (role == currentRole) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Current role",
                                                    tint = colorScheme.primary
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
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = colorScheme.onSurface,
                actionIconContentColor = colorScheme.onSurface
            ),
            modifier = modifier
        )
    }
}

@Composable
fun GlowingGradientBackground(currentRole: UserRole?, scrollOffset: Float = 0f, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "Glow Animation")
    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "glowAlpha"
    )

    val colors = when (currentRole) {
        UserRole.TEACHER -> listOf(Color(0xFF00E5FF), Color(0xFF18FFFF), Color.Transparent)
        UserRole.STUDENT -> listOf(Color(0xFF8C9EFF), Color(0xFF536DFE), Color.Transparent)
        UserRole.PARENT -> listOf(Color(0xFFFF8A65), Color(0xFFFFAB91), Color.Transparent)
        UserRole.SCHOOL_ADMIN -> listOf(Color(0xFF69F0AE), Color(0xFFB9F6CA), Color.Transparent)
        else -> listOf(Color(0xFFD1C4E9), Color(0xFFB39DDB), Color.Transparent)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        listOf(
            Offset(200f, 400f),
            Offset(800f, 300f),
            Offset(500f, 1000f)
        ).forEachIndexed { index, offset ->
            Box(
                Modifier
                    .size((700 + index * 100).dp)
                    .graphicsLayer { alpha = animatedAlpha }
                    .blur(150.dp)
                    .offset { IntOffset((offset.x - scrollOffset).toInt(), offset.y.toInt()) }
                    .background(
                        Brush.radialGradient(
                            colors = colors,
                            center = offset,
                            radius = 1000f
                        ),
                        shape = CircleShape
                    )
            )
        }
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
        AltHero()
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
private fun GlowingTopBarBackground(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colorScheme.primary.copy(alpha = 0.15f),
                        colorScheme.surface.copy(alpha = 0.05f)
                    )
                ),
                shape = RectangleShape
            )
            .blur(radius = 30.dp) // Creates the frosted effect
    )
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
/*
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




*/

@OptIn(ExperimentalMaterial3Api::class)
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

/*
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
*/
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

// 🧑🏽‍🏫 TEACHER OVERVIEW SECTION
@Composable
fun TeacherOverviewSection(modifier: Modifier = Modifier) {
    val teacherStats = listOf(
        TeacherStat("Mr. Kamali", courses = 4, pendingGrades = 2),
        TeacherStat("Ms. Uwase", courses = 3, pendingGrades = 0),
        TeacherStat("Mr. Habimana", courses = 5, pendingGrades = 1)
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Text("🧑🏽‍🏫 Teachers Overview", style = MaterialTheme.typography.titleMedium, color = colorScheme.primary)
        Spacer(Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(teacherStats.size){index->
                Card(shape = RoundedCornerShape(14.dp), modifier = Modifier.width(180.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(teacherStats[index].name, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text("Courses: ${teacherStats[index].courses}", style = MaterialTheme.typography.labelSmall)
                        Text("Pending grades: ${teacherStats[index].pendingGrades}", style = MaterialTheme.typography.labelSmall, color = if (teacherStats[index].pendingGrades > 0) colorScheme.error else colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

data class TeacherStat(
    val name: String,
    val courses: Int,
    val pendingGrades: Int
)

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

fun generateClassAcademicSummaries(
    offerings: List<SchoolLevelOffering>,
    gradeFetcher: (Course) -> List<Double> // fetches grades per course
): List<ClassAcademicSummary> {
    return offerings.mapNotNull { offering ->
        val allGrades = offering.courses.flatMap { course ->
            gradeFetcher(course)
        }

        if (allGrades.isNotEmpty()) {
            val avg = allGrades.average()
            val top = allGrades.maxOrNull() ?: 0.0
            ClassAcademicSummary(
                className = "${offering.schoolLevel.name} ${offering.stream.orEmpty()}".trim(),
                avg = avg,
                topScore = top
            )
        } else null
    }
}

val mockGradeFetcher: (Course) -> List<Double> = { course ->
    // Replace with real fetch from courseId or subjectId
    listOf(58.0, 73.5, 66.0, 92.3, 81.0)
}

val summaries = generateClassAcademicSummaries(sampleOfferings, mockGradeFetcher)
