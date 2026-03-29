package com.schoolbridge.v2.ui.home
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.platform.LocalContext
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
import com.schoolbridge.v2.components.CustomSideNavBar
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.domain.academic.TodayCourse
import com.schoolbridge.v2.domain.academic.teacher.QuickActionViewModel
import com.schoolbridge.v2.domain.messaging.Alert
import com.schoolbridge.v2.domain.user.CurrentUser
import com.schoolbridge.v2.domain.user.UserRole
import com.schoolbridge.v2.ui.components.AppSubHeader
import com.schoolbridge.v2.ui.components.SpacerL
import com.schoolbridge.v2.ui.components.SpacerM
import com.schoolbridge.v2.ui.components.SpacerS
import com.schoolbridge.v2.ui.home.alert.AlertDetailsBottomSheetContent
import com.schoolbridge.v2.ui.home.common.ActionCard
import com.schoolbridge.v2.ui.home.common.AddressCard
import com.schoolbridge.v2.ui.home.common.GenderTag
import com.schoolbridge.v2.ui.home.common.LinkedStudentRow
import com.schoolbridge.v2.ui.common.AdaptivePageFrame
import com.schoolbridge.v2.ui.common.SchoolBridgePatternBackground
import com.schoolbridge.v2.ui.common.isExpandedLayout
import com.schoolbridge.v2.ui.common.isWideLandscapeLayout
import com.schoolbridge.v2.ui.common.tutorial.CoachMarkOverlay
import com.schoolbridge.v2.ui.common.tutorial.CoachMarkStep
import com.schoolbridge.v2.ui.common.tutorial.HomeFeatureTourStore
import com.schoolbridge.v2.ui.common.tutorial.coachMarkTarget
import com.schoolbridge.v2.ui.common.tutorial.rememberCoachMarkTargetRegistry
import com.schoolbridge.v2.ui.home.course.CourseListSection
import com.schoolbridge.v2.ui.home.grade.GradesSummarySection
import com.schoolbridge.v2.ui.home.role.RoleSelectorBottomSheet
import com.schoolbridge.v2.ui.home.schedule.TodayScheduleSection
import com.schoolbridge.v2.ui.home.student.StudentListSection
import com.schoolbridge.v2.ui.home.teacher.TeacherQuickActionsSection
import com.schoolbridge.v2.ui.navigation.MainAppScreen
import kotlinx.coroutines.launch
import com.schoolbridge.v2.ui.home.decoration.GlowingTopBarBackground
import com.schoolbridge.v2.ui.home.decoration.GlowingGradientBackground


@Composable
fun AltHero(
    currentUser: CurrentUser?,
    cardHeight: androidx.compose.ui.unit.Dp = 210.dp,
    modifier: Modifier = Modifier
) {
    val name = buildString {
        append(currentUser?.lastName?.takeIf { it.isNotBlank() } ?: "there")
    }
    val role = currentUser?.currentRole ?: currentUser?.activeRoles?.firstOrNull()
    val scheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .then(modifier)
            .fillMaxWidth()
            .height(cardHeight),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            scheme.surface,
                            scheme.surfaceContainerHigh,
                            scheme.primary.copy(alpha = 0.12f)
                        ),
                        start = Offset.Zero,
                        end = Offset(900f, 700f)
                    )
                )
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(132.dp)
                    .blur(28.dp)
                    .background(
                        scheme.primary.copy(alpha = 0.16f),
                        CircleShape
                    )
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(116.dp)
                    .background(
                        scheme.secondary.copy(alpha = 0.10f),
                        RoundedCornerShape(topStart = 36.dp, bottomStart = 56.dp, topEnd = 36.dp, bottomEnd = 18.dp)
                    )
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = scheme.primary.copy(alpha = 0.10f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.School,
                                contentDescription = null,
                                tint = scheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = role?.humanLabel ?: "SchoolBridge",
                                style = MaterialTheme.typography.labelLarge,
                                color = scheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    if (currentUser?.isVerified == true) {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = Color(0xFF1E8E5A).copy(alpha = 0.12f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = Color(0xFF1E8E5A),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Verified",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFF1E8E5A),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Hello, $name",
                        style = MaterialTheme.typography.headlineMedium,
                        color = scheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when (role) {
                            UserRole.STUDENT -> "Stay on top of classes, marks, discipline, and what needs your attention today."
                            UserRole.PARENT -> "Track your children, school messages, fee reminders, and academic progress from one place."
                            UserRole.TEACHER -> "See your teaching load, grading pressure, and key classroom actions before the day picks up."
                            UserRole.SCHOOL_ADMIN -> "Keep a close eye on approvals, discipline, finance, and academic operations."
                            else -> "Welcome back to a calmer, clearer SchoolBridge home."
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = scheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HeroMetaChip(
                            label = "${currentUser?.activeRoles?.size ?: 0} active roles",
                            tint = scheme.secondary
                        )
                        HeroMetaChip(
                            label = if (currentUser?.email?.isNotBlank() == true) "Account ready" else "Profile in progress",
                            tint = scheme.tertiary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroMetaChip(label: String, tint: Color) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = tint.copy(alpha = 0.10f)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            style = MaterialTheme.typography.labelMedium,
            color = tint,
            fontWeight = FontWeight.Medium
        )
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
    roleSwitcherModifier: Modifier = Modifier,
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
                        visible = currentRole != null || availableRoles.isNotEmpty()
                    ) {
                        TextButton(
                            onClick = { showRoleSheet.value = true },
                            modifier = roleSwitcherModifier
                        ) {
                            Text(currentRole?.humanLabel ?: "Role & Requests")
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
    val context = LocalContext.current
    val isExpanded = isExpandedLayout()
    val isWideLandscapeHome = isWideLandscapeLayout()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val tutorialRegistry = rememberCoachMarkTargetRegistry()
    var selectedAlert by remember { mutableStateOf<Alert?>(null) }
    var activeTourStep by rememberSaveable {
        mutableStateOf(
            if (HomeFeatureTourStore.shouldShow(context)) 0 else -1
        )
    }
    val homeTourSteps = remember {
        listOf(
            CoachMarkStep(
                targetId = "home_role_switcher",
                title = "Switch roles here",
                body = "If you are both a parent and a teacher, or you manage more than one school role, use this button to change the home context."
            ),
            CoachMarkStep(
                targetId = "bottom_nav_message_screen",
                title = "Messages stay contextual",
                body = "Use Messages for school conversations, approvals, notices, and invited calls so updates stay attached to the right school context."
            ),
            CoachMarkStep(
                targetId = "bottom_nav_weekly_schedule_screen",
                title = "Your timetable is here",
                body = "Open Schedule to see classes, school moments, meetings, and your own plans in one place."
            )
        )
    }

    LaunchedEffect(activeTourStep) {
        if (activeTourStep >= 0 && !HomeFeatureTourStore.hasStarted(context)) {
            HomeFeatureTourStore.markStarted(context)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // ✨ Place the glow gradient behind everything

        GlowingGradientBackground(currentRole = currentUser?.currentRole)
        SchoolBridgePatternBackground(dotAlpha = 0.022f, gradientAlpha = 0.045f)
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
                    roleSwitcherModifier = Modifier.coachMarkTarget("home_role_switcher", tutorialRegistry),
                    modifier = modifier
                )
            },
            bottomBar = {
                if (!isWideLandscapeHome) {
                    CustomBottomNavBar(
                        currentScreen = currentScreen,
                        onTabSelected = onTabSelected,
                        currentUser = currentUser,
                        tutorialRegistry = tutorialRegistry
                    )
                }
            },
            modifier = modifier
        ) { paddingValues ->
            if (isWideLandscapeHome) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    CustomSideNavBar(
                        currentScreen = currentScreen,
                        currentUser = currentUser,
                        onTabSelected = onTabSelected,
                        tutorialRegistry = tutorialRegistry
                    )
                    AdaptivePageFrame(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
                        maxContentWidth = 1680.dp
                    ) {
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
                            modifier = Modifier.fillMaxSize(),
                            userSessionManager = userSessionManager,
                            tutorialRegistry = tutorialRegistry,
                            useWideLandscape = true
                        )
                    }
                }
            } else {
                AdaptivePageFrame(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = paddingValues,
                    maxContentWidth = if (isExpanded) 1320.dp else 1240.dp
                ) {
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
                        modifier = Modifier.fillMaxSize(),
                        userSessionManager = userSessionManager,
                        tutorialRegistry = tutorialRegistry,
                        useWideLandscape = false
                    )
                }
            }
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
                    AlertDetailsBottomSheetContent(
                        alertId = alert.id,
                        userSessionManager = userSessionManager
                    )
                }
            }
        }

        if (activeTourStep >= 0) {
            CoachMarkOverlay(
                registry = tutorialRegistry,
                steps = homeTourSteps,
                currentIndex = activeTourStep,
                onSkip = {
                    HomeFeatureTourStore.markSeen(context)
                    activeTourStep = -1
                },
                onNext = {
                    if (activeTourStep >= homeTourSteps.lastIndex) {
                        HomeFeatureTourStore.markSeen(context)
                        activeTourStep = -1
                    } else {
                        activeTourStep += 1
                    }
                },
                onDone = {
                    HomeFeatureTourStore.markSeen(context)
                    activeTourStep = -1
                },
                onTargetUnavailable = {
                    if (activeTourStep >= homeTourSteps.lastIndex) {
                        HomeFeatureTourStore.markSeen(context)
                        activeTourStep = -1
                    } else {
                        activeTourStep += 1
                    }
                }
            )
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
    tutorialRegistry: com.schoolbridge.v2.ui.common.tutorial.CoachMarkTargetRegistry? = null,
    useWideLandscape: Boolean = false,
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
    if (useWideLandscape) {
        WideHomeUI(
            currentUser = currentUser,
            activeRole = activeRole,
            onWeeklyViewClick = onWeeklyViewClick,
            userSessionManager = userSessionManager,
            modifier = modifier
        )
        return
    }

    Column(
        modifier = modifier
            .background(Color.Transparent)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AltHero(currentUser = currentUser)

        when (activeRole) {
            UserRole.STUDENT -> {
                StudentOverviewSection(currentUser = currentUser)
                SpacerL()
                TodayScheduleSection(
                    userSessionManager = userSessionManager,
                    onWeeklyViewClick = onWeeklyViewClick
                )
                SpacerL()
                GradesSummarySection()
                SpacerL()
                CourseListSection(userSessionManager = userSessionManager)
            }
            UserRole.PARENT -> {
                ParentOverviewSection(currentUser = currentUser)
                SpacerL()
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

                SpacerL()
                TodayScheduleSection(
                    userSessionManager = userSessionManager,
                    onWeeklyViewClick = onWeeklyViewClick
                )
            }

            UserRole.TEACHER -> {
                TeacherOverviewSection()
                SpacerL()
                TeacherQuickActionsSection()
                SpacerL()
                TodayScheduleSection(
                    userSessionManager = userSessionManager,
                    onWeeklyViewClick = onWeeklyViewClick
                )
                SpacerL()
                CourseListSection(userSessionManager = userSessionManager)
            }
            UserRole.SCHOOL_ADMIN -> {
                AdminOverviewSection()
                SpacerL()
                AdminQuickActionsSection()
                SpacerL()
                AdminTodayScheduleSection(
                    userSessionManager = userSessionManager,
                    onWeeklyViewClick = onWeeklyViewClick
                )
                SpacerL()
                AdminOperationsBoard()
                SpacerM()
                AdminPendingRequestsSection()
                SpacerM()
                AcademicOverviewSection()
                SpacerM()
                StudentExplorerSection()
                SpacerM()
                AcademicCalendarSection()
            }
            else -> {}
        }
    }
}

@Composable
private fun WideHomeUI(
    currentUser: CurrentUser?,
    activeRole: UserRole?,
    onWeeklyViewClick: () -> Unit,
    userSessionManager: UserSessionManager,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .background(Color.Transparent)
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        WideHomeShowcaseHero(
            currentUser = currentUser,
            activeRole = activeRole,
            scrollOffset = scrollState.value.toFloat()
        )

        when (activeRole) {
            UserRole.STUDENT -> {
                DashboardBand(
                    title = "Today at school",
                    subtitle = "What deserves your attention before the day moves on."
                    ,
                    scrollOffset = scrollState.value.toFloat(),
                    bandIndex = 0
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(18.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        TodayScheduleSection(
                            userSessionManager = userSessionManager,
                            onWeeklyViewClick = onWeeklyViewClick,
                            modifier = Modifier.weight(1.2f)
                        )
                        GradesSummarySection(modifier = Modifier.weight(0.8f))
                    }
                }
                DashboardBand(
                    title = "Progress and learning",
                    subtitle = "A wider look at performance and the subjects shaping this term."
                    ,
                    scrollOffset = scrollState.value.toFloat(),
                    bandIndex = 1
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(18.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        StudentOverviewSection(
                            currentUser = currentUser,
                            modifier = Modifier.weight(1f)
                        )
                        CourseListSection(
                            userSessionManager = userSessionManager,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            UserRole.PARENT -> {
                DashboardBand(
                    title = "Family watch",
                    subtitle = "Schedule, school communication, and the overview a parent needs first."
                    ,
                    scrollOffset = scrollState.value.toFloat(),
                    bandIndex = 0
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(18.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        TodayScheduleSection(
                            userSessionManager = userSessionManager,
                            onWeeklyViewClick = onWeeklyViewClick,
                            modifier = Modifier.weight(1.18f)
                        )
                        ParentOverviewSection(
                            currentUser = currentUser,
                            modifier = Modifier.weight(0.82f)
                        )
                    }
                }
                DashboardBand(
                    title = "People and context",
                    subtitle = "Keep your children and household school context in one calm place."
                    ,
                    scrollOffset = scrollState.value.toFloat(),
                    bandIndex = 1
                ) {
                    ParentContextPanel(
                        currentUser = currentUser,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            UserRole.TEACHER -> {
                DashboardBand(
                    title = "Teaching now",
                    subtitle = "See the day, respond quickly, and keep classroom work moving."
                    ,
                    scrollOffset = scrollState.value.toFloat(),
                    bandIndex = 0
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(18.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        TodayScheduleSection(
                            userSessionManager = userSessionManager,
                            onWeeklyViewClick = onWeeklyViewClick,
                            modifier = Modifier.weight(1.15f)
                        )
                        TeacherQuickActionsSection(modifier = Modifier.weight(0.85f))
                    }
                }
                DashboardBand(
                    title = "Workload and courses",
                    subtitle = "A fuller read on grading pressure and the courses under your care."
                    ,
                    scrollOffset = scrollState.value.toFloat(),
                    bandIndex = 1
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(18.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        TeacherOverviewSection(modifier = Modifier.weight(0.9f))
                        CourseListSection(
                            userSessionManager = userSessionManager,
                            modifier = Modifier.weight(1.1f)
                        )
                    }
                }
            }

            UserRole.SCHOOL_ADMIN -> {
                DashboardBand(
                    title = "Operations now",
                    subtitle = "The first row is about decisions and bottlenecks that need immediate attention."
                    ,
                    scrollOffset = scrollState.value.toFloat(),
                    bandIndex = 0
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(18.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        AdminOverviewSection(modifier = Modifier.weight(1f))
                        AdminPendingRequestsSection(modifier = Modifier.weight(1f))
                    }
                }
                DashboardBand(
                    title = "Action desk",
                    subtitle = "Quick tools on the left, live school rhythm on the right."
                    ,
                    scrollOffset = scrollState.value.toFloat(),
                    bandIndex = 1
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(18.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        AdminQuickActionsSection(modifier = Modifier.weight(0.95f))
                        AdminTodayScheduleSection(
                            userSessionManager = userSessionManager,
                            onWeeklyViewClick = onWeeklyViewClick,
                            modifier = Modifier.weight(1.05f)
                        )
                    }
                }
                DashboardBand(
                    title = "School pulse",
                    subtitle = "Look beyond urgent tasks into academics, operations, and discovery."
                    ,
                    scrollOffset = scrollState.value.toFloat(),
                    bandIndex = 2
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(18.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        AdminOperationsBoard(modifier = Modifier.weight(1f))
                        AcademicOverviewSection(modifier = Modifier.weight(1f))
                    }
                }
                DashboardBand(
                    title = "Explore and plan ahead",
                    subtitle = "Student discovery and calendar context belong together at the bottom of the page."
                    ,
                    scrollOffset = scrollState.value.toFloat(),
                    bandIndex = 3
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(18.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        StudentExplorerSection(modifier = Modifier.weight(1f))
                        AcademicCalendarSection(modifier = Modifier.weight(1f))
                    }
                }
            }

            else -> {
                WideHomeShowcaseHero(
                    currentUser = currentUser,
                    activeRole = activeRole,
                    scrollOffset = scrollState.value.toFloat()
                )
            }
        }
    }
}

@Composable
private fun WideHomeShowcaseHero(
    currentUser: CurrentUser?,
    activeRole: UserRole?,
    scrollOffset: Float,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val lastName = currentUser?.lastName?.takeIf { it.isNotBlank() } ?: "Uwimana"
    val roleLabel = activeRole?.humanLabel ?: "SchoolBridge"
    val heroLift = (scrollOffset * 0.16f).coerceAtMost(42f)
    val orbDrift = (scrollOffset * 0.22f).coerceAtMost(54f)
    val pillDrift = (scrollOffset * 0.12f).coerceAtMost(28f)
    val statusLabel = if (currentUser?.isVerified == true) "Verified account" else "Profile in progress"
    val focusTitle = when (activeRole) {
        UserRole.STUDENT -> "Learning field"
        UserRole.PARENT -> "Family field"
        UserRole.TEACHER -> "Classroom field"
        UserRole.SCHOOL_ADMIN -> "Operations field"
        else -> "School field"
    }
    val focusBody = when (activeRole) {
        UserRole.STUDENT -> "Classes, marks, and the next academic moves are staged here."
        UserRole.PARENT -> "Schedules, school messages, and household context stay in one calm surface."
        UserRole.TEACHER -> "Teaching load, quick actions, and course flow stay close without noise."
        UserRole.SCHOOL_ADMIN -> "Approvals, rhythm, and academics sit together in one command view."
        else -> "SchoolBridge keeps the right moments and decisions within reach."
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp)
            .graphicsLayer {
                translationY = -heroLift
            },
        shape = RoundedCornerShape(38.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            scheme.surfaceBright,
                            scheme.surface,
                            scheme.surfaceContainerHigh,
                            scheme.primaryContainer.copy(alpha = 0.92f)
                        ),
                        start = Offset.Zero,
                        end = Offset(1400f, 900f)
                    )
                )
                .padding(horizontal = 32.dp, vertical = 30.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(180.dp)
                    .graphicsLayer {
                        translationX = orbDrift * 0.28f
                        translationY = -orbDrift
                    }
                    .blur(34.dp)
                    .background(
                        scheme.primary.copy(alpha = 0.18f),
                        CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 10.dp, y = (-8).dp)
                    .size(148.dp)
                    .graphicsLayer {
                        translationY = orbDrift * 0.55f
                        translationX = -orbDrift * 0.12f
                    }
                    .background(
                        scheme.tertiary.copy(alpha = 0.12f),
                        RoundedCornerShape(topStart = 52.dp, topEnd = 20.dp, bottomEnd = 52.dp, bottomStart = 22.dp)
                    )
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(width = 220.dp, height = 132.dp)
                    .graphicsLayer {
                        translationX = orbDrift * 0.62f
                    }
                    .background(
                        scheme.secondaryContainer.copy(alpha = 0.94f),
                        RoundedCornerShape(999.dp)
                    )
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = scheme.primary.copy(alpha = 0.10f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = scheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = roleLabel,
                                style = MaterialTheme.typography.labelLarge,
                                color = scheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    HeroStatusPill(
                        label = statusLabel,
                        tint = if (currentUser?.isVerified == true) Color(0xFF1E8E5A) else scheme.tertiary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(18.dp),
                        modifier = Modifier
                            .weight(1.2f)
                            .graphicsLayer {
                                translationY = pillDrift * 0.45f
                            }
                    ) {
                        Text(
                            text = "Welcome back, $lastName",
                            style = MaterialTheme.typography.displaySmall,
                            color = scheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = wideRoleNarrative(activeRole),
                            style = MaterialTheme.typography.bodyLarge,
                            color = scheme.onSurfaceVariant,
                            lineHeight = 24.sp
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(0.78f)
                            .graphicsLayer {
                                translationY = -pillDrift * 0.28f
                            },
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HeroSignalPanel(
                            title = focusTitle,
                            body = focusBody,
                            accent = scheme.primary
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            HeroMetricTile(
                                title = "Roles",
                                value = "${currentUser?.activeRoles?.size ?: 0}",
                                accent = scheme.secondary,
                                modifier = Modifier.weight(1f)
                            )
                            HeroMetricTile(
                                title = "Focus",
                                value = wideFocusLabel(activeRole),
                                accent = scheme.tertiary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardBand(
    title: String,
    subtitle: String,
    scrollOffset: Float,
    bandIndex: Int,
    content: @Composable ColumnScope.() -> Unit
) {
    val revealRaw = ((scrollOffset - bandIndex * 130f) / 180f).coerceIn(0f, 1f)
    val revealProgress by animateFloatAsState(
        targetValue = revealRaw,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "dashboardBandReveal"
    )
    val translation = 56f * (1f - revealProgress)
    val alpha = 0.28f + (0.72f * revealProgress)
    val scale = 0.965f + (0.035f * revealProgress)
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        content = {
            Column(
                modifier = Modifier.graphicsLayer {
                    translationY = translation
                    this.alpha = alpha
                    scaleX = scale
                    scaleY = scale
                },
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(
                modifier = Modifier.graphicsLayer {
                    translationY = translation * 0.8f
                    this.alpha = alpha
                    scaleX = scale
                    scaleY = scale
                },
                verticalArrangement = Arrangement.spacedBy(14.dp),
                content = content
            )
        }
    )
}

@Composable
private fun HeroStatusPill(
    label: String,
    tint: Color
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = tint.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = tint,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun HeroSignalPanel(
    title: String,
    body: String,
    accent: Color
) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = accent,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 21.sp
            )
        }
    }
}

@Composable
private fun HeroMetricTile(
    title: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = accent,
                letterSpacing = 1.sp
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun wideRoleNarrative(role: UserRole?): String = when (role) {
    UserRole.STUDENT -> "Everything important for today lives here: classes, marks, and the work that is quietly shaping your term."
    UserRole.PARENT -> "This is your family command view for schedules, school communication, and the moments that need attention before they pile up."
    UserRole.TEACHER -> "Your teaching day, quick classroom actions, and workload signals are arranged here so the next decision is always close."
    UserRole.SCHOOL_ADMIN -> "A school-wide command surface for approvals, rhythm, operations, and the academic signals that deserve the next move."
    else -> "SchoolBridge brings the right school moments, communication, and priorities together in one clear dashboard."
}

private fun wideFocusLabel(role: UserRole?): String = when (role) {
    UserRole.STUDENT -> "Learning"
    UserRole.PARENT -> "Family"
    UserRole.TEACHER -> "Classroom"
    UserRole.SCHOOL_ADMIN -> "Operations"
    else -> "Overview"
}

@Composable
private fun ParentContextPanel(
    currentUser: CurrentUser?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        currentUser?.address?.let {
            Text(
                text = "Your Address",
                style = MaterialTheme.typography.titleMedium
            )
            AddressCard(address = it)
        }
        currentUser?.gender?.let {
            GenderTag(gender = it)
        }
        if (!currentUser?.linkedStudents.isNullOrEmpty()) {
            Text(
                text = "Linked Students",
                style = MaterialTheme.typography.titleMedium
            )
            LinkedStudentRow(list = currentUser!!.linkedStudents)
        }
    }
}

@Composable
fun AdminQuickActionsSection(modifier: Modifier = Modifier) {
    val viewModel: QuickActionViewModel = viewModel()
    val chosenIds by viewModel.selected.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    val chosenActions = adminQuickActions.filter { it.id in chosenIds }

    Column(
        modifier = modifier
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
    userSessionManager: UserSessionManager,
    onWeeklyViewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TodayScheduleSection(
        userSessionManager = userSessionManager,
        onWeeklyViewClick = onWeeklyViewClick,
        modifier = modifier
    )
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
            "Class Attendance",
            style = MaterialTheme.typography.titleMedium,
            color = colorScheme.primary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "See attendance health by class and quickly spot where follow-up is needed.",
            style = MaterialTheme.typography.bodySmall,
            color = colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            //modifier = Modifier.animateItemPlacement()
        ) {
            items(classes.size){index->
                AnimatedVisibility(visible = true) {
                    val classInfo = classes[index]
                    val attendanceRate = ((classInfo.present.toFloat() / classInfo.total.toFloat()) * 100).toInt()
                    val attendanceColor = when {
                        attendanceRate >= 95 -> Color(0xFF1E8E5A)
                        attendanceRate >= 85 -> colorScheme.primary
                        else -> colorScheme.error
                    }
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .width(184.dp)
                            .height(196.dp),
                        //.animateItemPlacement(),
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.surfaceContainerLow,
                            contentColor = colorScheme.onSurface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            Modifier
                                .padding(14.dp)
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    "${classInfo.level} ${classInfo.stream}",
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = attendanceColor.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = "$attendanceRate%",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = attendanceColor
                                    )
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            Text(
                                "${classInfo.present}/${classInfo.total} learners present",
                                style = MaterialTheme.typography.labelSmall,
                                color = colorScheme.onSurfaceVariant
                            )

                            Spacer(Modifier.height(6.dp))

                            LinearProgressIndicator(
                                progress = { classInfo.present.toFloat() / classInfo.total.toFloat() },
                                modifier = Modifier.fillMaxWidth(),
                                color = attendanceColor,
                                trackColor = colorScheme.surfaceVariant
                            )

                            Spacer(Modifier.height(6.dp))

                            Text(
                                "$attendanceRate% attendance",
                                style = MaterialTheme.typography.labelSmall,
                                color = colorScheme.onSurface
                            )

                            Spacer(Modifier.height(12.dp))

                            HorizontalDivider(color = colorScheme.outlineVariant.copy(alpha = 0.5f))
                            Spacer(Modifier.height(10.dp))

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(58.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (classInfo.absentReasons.isNotEmpty()) "Absence reasons" else "Attendance status",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colorScheme.onSurfaceVariant
                                )

                                if (classInfo.absentReasons.isNotEmpty()) {
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        classInfo.absentReasons.forEach { (reason, count) ->
                                            AttendanceReasonChip(
                                                label = "$reason $count",
                                                tint = when (reason) {
                                                    "Medical" -> colorScheme.primary
                                                    "Permission" -> colorScheme.secondary
                                                    else -> colorScheme.error
                                                }
                                            )
                                        }
                                    }
                                } else {
                                    AttendanceReasonChip(
                                        label = "Full attendance",
                                        tint = Color(0xFF1E8E5A)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AttendanceReasonChip(label: String, tint: Color) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = tint.copy(alpha = 0.12f)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelSmall,
            color = tint
        )
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
        Text("Important Dates", style = MaterialTheme.typography.titleMedium, color = colorScheme.primary)
        Spacer(Modifier.height(4.dp))
        Text(
            "Key academic checkpoints that should stay visible even when alerts move into messaging.",
            style = MaterialTheme.typography.bodySmall,
            color = colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(10.dp))
        dates.forEach { d ->
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerLow)
            ) {
                Row(
                    Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(colorScheme.primary.copy(alpha = 0.10f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = colorScheme.primary)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(d.title, fontWeight = FontWeight.SemiBold)
                        Text(d.date, style = MaterialTheme.typography.labelSmall, color = colorScheme.onSurfaceVariant)
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
        Text("Academic Summary", style = MaterialTheme.typography.titleMedium, color = colorScheme.primary)
        Spacer(Modifier.height(4.dp))
        Text(
            "A quick picture of class performance, strongest results, and where academic support may be needed.",
            style = MaterialTheme.typography.bodySmall,
            color = colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(10.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(classSummaries.size){index->
                val item = classSummaries[index]
                Card(
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.width(200.dp),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerLow)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(item.className, fontWeight = FontWeight.SemiBold)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = when {
                                    item.avg >= 70 -> Color(0xFF1E8E5A).copy(alpha = 0.12f)
                                    item.avg >= 60 -> colorScheme.primary.copy(alpha = 0.12f)
                                    else -> colorScheme.error.copy(alpha = 0.12f)
                                }
                            ) {
                                Text(
                                    text = when {
                                        item.avg >= 70 -> "Stable"
                                        item.avg >= 60 -> "Watch"
                                        else -> "Support"
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = when {
                                        item.avg >= 70 -> Color(0xFF1E8E5A)
                                        item.avg >= 60 -> colorScheme.primary
                                        else -> colorScheme.error
                                    }
                                )
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        Text("Class average", style = MaterialTheme.typography.labelSmall, color = colorScheme.onSurfaceVariant)
                        Text("${item.avg}%", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text("Top score: ${item.topScore}%", style = MaterialTheme.typography.bodySmall)
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
