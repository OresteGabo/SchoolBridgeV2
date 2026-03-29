package com.schoolbridge.v2.ui.home.timetable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.schoolbridge.v2.data.remote.MessageApiServiceImpl
import com.schoolbridge.v2.data.remote.TimetableApiServiceImpl
import com.schoolbridge.v2.data.repository.implementations.MessagingRepositoryImpl
import com.schoolbridge.v2.data.repository.implementations.TimetableRepositoryImpl
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.ui.common.AdaptivePageFrame
import com.schoolbridge.v2.ui.common.FriendlyNetworkErrorCard
import com.schoolbridge.v2.ui.common.SchoolBridgePatternBackground
import com.schoolbridge.v2.ui.common.isExpandedLayout
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

private enum class TimetablePage(val label: String) {
    Flow("Day"),
    Snapshot("Now"),
    Week("Week")
}

private val LocalDateSaver = Saver<LocalDate, Long>(
    save = { it.toEpochDay() },
    restore = { LocalDate.ofEpochDay(it) }
)

private val AgendaKindSetSaver = Saver<Set<AgendaItemKind>, List<String>>(
    save = { kinds -> kinds.map { it.name } },
    restore = { saved -> saved.mapNotNull { name -> AgendaItemKind.entries.find { it.name == name } }.toSet() }
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableTabsScreen(
    userSessionManager: UserSessionManager,
    onBack: (() -> Unit)? = null,
    onOpenMessageThread: ((String, String?) -> Unit)? = null,
    bottomBar: @Composable (() -> Unit)? = null
) {
    val viewModel: TimetableViewModel = viewModel(
        factory = TimetableViewModelFactory(
            timetableRepository = TimetableRepositoryImpl(TimetableApiServiceImpl(userSessionManager)),
            messagingRepository = MessagingRepositoryImpl(MessageApiServiceImpl(userSessionManager)),
            userSessionManager = userSessionManager
        )
    )
    val uiState by viewModel.uiState.collectAsState()
    val isExpanded = isExpandedLayout()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp
    val useCompactHeader = !isExpanded || isLandscape || configuration.screenHeightDp < 760
    val usePhoneSummaryTab = !isExpanded && !isLandscape
    val useWideLandscapeLayout = false
    var selectedPage by rememberSaveable { mutableIntStateOf(TimetablePage.Flow.ordinal) }
    val pages = remember(usePhoneSummaryTab) {
        if (usePhoneSummaryTab) {
            listOf(TimetablePage.Flow, TimetablePage.Snapshot, TimetablePage.Week)
        } else {
            listOf(TimetablePage.Flow, TimetablePage.Week)
        }
    }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showAddSheet by remember { mutableStateOf(false) }

    val today = LocalDate.now()
    var selectedDate by rememberSaveable(stateSaver = LocalDateSaver) {
        mutableStateOf(today)
    }
    var selectedWeekDate by rememberSaveable(stateSaver = LocalDateSaver) {
        mutableStateOf(today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)))
    }
    var selectedWeekFocusDate by rememberSaveable(stateSaver = LocalDateSaver) {
        mutableStateOf(today)
    }
    var includedKinds by rememberSaveable(stateSaver = AgendaKindSetSaver) {
        mutableStateOf(AgendaItemKind.entries.toSet())
    }
    var densityName by rememberSaveable { mutableStateOf(AgendaDensity.COMFORTABLE.name) }
    var showOnlyMine by rememberSaveable { mutableStateOf(false) }
    val density = remember(densityName) { AgendaDensity.valueOf(densityName) }

    val dailyAgenda = remember(uiState, selectedDate, includedKinds, showOnlyMine) {
        uiState.dailyAgenda(selectedDate, includedKinds, showOnlyMine)
    }
    val nextDateWithEvent = remember(uiState, selectedDate, includedKinds, showOnlyMine) {
        uiState.nextDateWithEvent(selectedDate.plusDays(1), includedKinds, showOnlyMine)
    }
    val highlights = remember(uiState, includedKinds, showOnlyMine) {
        uiState.upcomingHighlights(includedKinds = includedKinds, showOnlyMine = showOnlyMine)
    }
    val nowNext = remember(uiState, includedKinds, showOnlyMine) {
        uiState.nowAndNext(includedKinds = includedKinds, showOnlyMine = showOnlyMine)
    }
    val deadlines = remember(uiState, includedKinds, showOnlyMine) {
        uiState.upcomingDeadlines(includedKinds = includedKinds, showOnlyMine = showOnlyMine)
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val emptyTitle = remember(uiState.audience, selectedDate, includedKinds, showOnlyMine) {
        buildEmptyTitle(uiState.audience, selectedDate, includedKinds, showOnlyMine)
    }
    val emptyMessage = remember(uiState.audience, uiState.selectedStudentIds, includedKinds, showOnlyMine) {
        buildEmptyMessage(uiState.audience, uiState.selectedStudentIds.size, includedKinds, showOnlyMine)
    }
    val selectedSchoolLabels = remember(uiState.students, uiState.selectedStudentIds, uiState.templates, showOnlyMine) {
        uiState.selectedSchoolLabels(showOnlyMine)
    }
    var showLearnerMenu by remember { mutableStateOf(false) }

    LaunchedEffect(pages) {
        if (selectedPage > pages.lastIndex) {
            selectedPage = 0
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = when (pages[selectedPage]) {
                                TimetablePage.Flow -> selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMM d"))
                                TimetablePage.Snapshot -> "Live overview and planner tools"
                                TimetablePage.Week -> "Week of ${selectedWeekDate.format(DateTimeFormatter.ofPattern("MMM d"))}"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = timetableAudienceSummary(
                                students = uiState.students,
                                selectedStudentIds = uiState.selectedStudentIds,
                                showOnlyMine = showOnlyMine
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    if (uiState.students.size > 1) {
                        Box {
                            OutlinedButton(
                                onClick = { showLearnerMenu = true },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = learnerSelectionSummary(uiState.students, uiState.selectedStudentIds),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            DropdownMenu(
                                expanded = showLearnerMenu,
                                onDismissRequest = { showLearnerMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("All children") },
                                    onClick = {
                                        viewModel.selectAllStudents()
                                        showLearnerMenu = false
                                    },
                                    trailingIcon = {
                                        if (uiState.selectedStudentIds.isEmpty()) {
                                            Icon(Icons.Default.Check, contentDescription = null)
                                        }
                                    }
                                )
                                uiState.students.forEach { student ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = student.name,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        },
                                        onClick = {
                                            viewModel.toggleStudentSelection(student.id)
                                        },
                                        trailingIcon = {
                                            if (student.id in uiState.selectedStudentIds) {
                                                Icon(Icons.Default.Check, contentDescription = null)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                    if (pages[selectedPage] == TimetablePage.Week && selectedWeekDate != today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))) {
                        OutlinedButton(
                            onClick = {
                                selectedWeekDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                            },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("This week")
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = { bottomBar?.invoke() },
        floatingActionButton = {
            if (selectedPage == TimetablePage.Flow.ordinal) {
                FloatingActionButton(onClick = { showAddSheet = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Event")
                }
            }
        }
    ) { paddingValues ->
        AdaptivePageFrame(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues,
            maxContentWidth = if (isExpanded) 1380.dp else 1240.dp
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                SchoolBridgePatternBackground(dotAlpha = 0.018f, gradientAlpha = 0.04f)

                if (useWideLandscapeLayout) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .width(360.dp)
                                .fillMaxSize()
                        ) {
                            TimetableSidebar(
                            highlights = highlights,
                                audience = uiState.audience,
                                scopeLabel = uiState.scopeLabel,
                                selectedSchoolLabels = selectedSchoolLabels,
                                showOnlyMine = showOnlyMine,
                                onToggleShowOnlyMine = { showOnlyMine = !showOnlyMine },
                                compact = true,
                                wideLayout = true,
                                uiState = uiState,
                            selectedPage = selectedPage,
                                pages = pages,
                                onPageSelected = { selectedPage = it }
                            )
                        }

                        TimetableContentPane(
                            modifier = Modifier.weight(1f),
                            hasError = uiState.errorMessage != null &&
                                uiState.templates.isEmpty() &&
                                uiState.plannedItems.isEmpty() &&
                                uiState.personalPlans.isEmpty(),
                            errorMessage = uiState.errorMessage,
                            onRetry = viewModel::refresh
                        ) {
                            PlannerPageContent(
                                selectedPage = pages[selectedPage],
                                uiState = uiState,
                                today = today,
                                selectedDate = selectedDate,
                                onSelectedDateChange = { selectedDate = it },
                                selectedWeekDate = selectedWeekDate,
                                onSelectedWeekDateChange = { selectedWeekDate = it },
                                selectedWeekFocusDate = selectedWeekFocusDate,
                                onSelectedWeekFocusDateChange = { selectedWeekFocusDate = it },
                                agendaItems = dailyAgenda,
                                nextDateWithEvent = nextDateWithEvent,
                                nowItem = nowNext.first,
                                nextItem = nowNext.second,
                                deadlines = deadlines,
                                separateSummaryPage = usePhoneSummaryTab,
                                density = density,
                                onDensityChange = { densityName = it.name },
                                includedKinds = includedKinds,
                                onIncludedKindsChange = { includedKinds = it },
                                emptyTitle = emptyTitle,
                                emptyMessage = emptyMessage,
                                onAgendaItemActionClick = { item ->
                                    item.threadId?.let { threadId ->
                                        onOpenMessageThread?.invoke(threadId, item.threadCallMessageId)
                                    }
                                }
                            )
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        TimetableSidebar(
                            highlights = highlights,
                            audience = uiState.audience,
                            scopeLabel = uiState.scopeLabel,
                            selectedSchoolLabels = selectedSchoolLabels,
                            showOnlyMine = showOnlyMine,
                            onToggleShowOnlyMine = { showOnlyMine = !showOnlyMine },
                            compact = useCompactHeader,
                            wideLayout = false,
                            uiState = uiState,
                            selectedPage = selectedPage,
                            pages = pages,
                            onPageSelected = { selectedPage = it }
                        )

                        TimetableContentPane(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            hasError = uiState.errorMessage != null &&
                                uiState.templates.isEmpty() &&
                                uiState.plannedItems.isEmpty() &&
                                uiState.personalPlans.isEmpty(),
                            errorMessage = uiState.errorMessage,
                            onRetry = viewModel::refresh
                        ) {
                            PlannerPageContent(
                                selectedPage = pages[selectedPage],
                                uiState = uiState,
                                today = today,
                                selectedDate = selectedDate,
                                onSelectedDateChange = { selectedDate = it },
                                selectedWeekDate = selectedWeekDate,
                                onSelectedWeekDateChange = { selectedWeekDate = it },
                                selectedWeekFocusDate = selectedWeekFocusDate,
                                onSelectedWeekFocusDateChange = { selectedWeekFocusDate = it },
                                agendaItems = dailyAgenda,
                                nextDateWithEvent = nextDateWithEvent,
                                nowItem = nowNext.first,
                                nextItem = nowNext.second,
                                deadlines = deadlines,
                                separateSummaryPage = usePhoneSummaryTab,
                                density = density,
                                onDensityChange = { densityName = it.name },
                                includedKinds = includedKinds,
                                onIncludedKindsChange = { includedKinds = it },
                                emptyTitle = emptyTitle,
                                emptyMessage = emptyMessage,
                                onAgendaItemActionClick = { item ->
                                    item.threadId?.let { threadId ->
                                        onOpenMessageThread?.invoke(threadId, item.threadCallMessageId)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddSheet = false },
            sheetState = bottomSheetState
        ) {
            AddEventBottomSheet(
                selectedDate = selectedDate,
                onDismiss = { showAddSheet = false },
                onAddEvent = { startTime, endTime, title, description, planType ->
                    viewModel.createPersonalPlan(
                        date = selectedDate,
                        startTime = startTime,
                        endTime = endTime,
                        title = title,
                        description = description,
                        planType = planType
                    )
                    showAddSheet = false
                }
            )
        }
    }

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TimetableSidebar(
    highlights: List<AgendaItemUi>,
    audience: String,
    scopeLabel: String?,
    selectedSchoolLabels: List<String>,
    showOnlyMine: Boolean,
    onToggleShowOnlyMine: () -> Unit,
    compact: Boolean,
    wideLayout: Boolean,
    uiState: TimetableUiState,
    selectedPage: Int,
    pages: List<TimetablePage>,
    onPageSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = if (compact) 6.dp else 10.dp),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.92f)
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (uiState.hasTeachingSchedule()) {
                val nextHighlight = highlights.firstOrNull()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = showOnlyMine,
                        onClick = onToggleShowOnlyMine,
                        label = { Text("Only my schedule") },
                        leadingIcon = {
                            if (showOnlyMine) {
                                Icon(Icons.Default.Check, contentDescription = null)
                            }
                        }
                    )

                    nextHighlight?.let { item ->
                        Text(
                            text = "Next up ${item.start.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            TabRow(
                selectedTabIndex = selectedPage,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.78f)
            ) {
                pages.forEachIndexed { index, page ->
                    Tab(
                        selected = selectedPage == index,
                        onClick = { onPageSelected(index) },
                        text = { Text(page.label) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PlannerPageContent(
    selectedPage: TimetablePage,
    uiState: TimetableUiState,
    today: LocalDate,
    selectedDate: LocalDate,
    onSelectedDateChange: (LocalDate) -> Unit,
    selectedWeekDate: LocalDate,
    onSelectedWeekDateChange: (LocalDate) -> Unit,
    selectedWeekFocusDate: LocalDate,
    onSelectedWeekFocusDateChange: (LocalDate) -> Unit,
    agendaItems: List<AgendaItemUi>,
    nextDateWithEvent: LocalDate?,
    nowItem: AgendaItemUi?,
    nextItem: AgendaItemUi?,
    deadlines: List<AgendaItemUi>,
    separateSummaryPage: Boolean,
    density: AgendaDensity,
    onDensityChange: (AgendaDensity) -> Unit,
    includedKinds: Set<AgendaItemKind>,
    onIncludedKindsChange: (Set<AgendaItemKind>) -> Unit,
    emptyTitle: String,
    emptyMessage: String,
    onAgendaItemActionClick: (AgendaItemUi) -> Unit
) {
    val jumpToDate: (LocalDate) -> Unit = { date ->
        onSelectedDateChange(date)
        onSelectedWeekFocusDateChange(date)
        onSelectedWeekDateChange(date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)))
    }

    val onJumpTodayAction = { jumpToDate(today) }
    val onJumpTomorrowAction = { jumpToDate(today.plusDays(1)) }
    val onJumpNextClassAction = {
        (nextItem ?: deadlines.firstOrNull())?.start?.toLocalDate()?.let(jumpToDate)
        Unit
    }
    val onJumpNextMeetingAction = {
        (deadlines.firstOrNull { it.kind == AgendaItemKind.MEETING || it.kind == AgendaItemKind.CALL }
            ?: nextItem)?.start?.toLocalDate()?.let(jumpToDate)
        Unit
    }

    if (!separateSummaryPage) {
        Column(modifier = Modifier.fillMaxSize()) {
            PlannerOverviewPanel(
                nowItem = nowItem,
                nextItem = nextItem,
                deadlines = deadlines,
                compactLayout = false,
                density = density,
                onDensityChange = onDensityChange,
                includedKinds = includedKinds,
                onIncludedKindsChange = onIncludedKindsChange,
                onJumpToday = onJumpTodayAction,
                onJumpTomorrow = onJumpTomorrowAction,
                onJumpNextClass = onJumpNextClassAction,
                onJumpNextMeeting = onJumpNextMeetingAction
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (selectedPage) {
                    TimetablePage.Flow -> DailyTimetableTab(
                        agendaItems = agendaItems,
                        nextDateWithEvent = nextDateWithEvent,
                        selectedDate = selectedDate,
                        onDateChange = onSelectedDateChange,
                        density = density,
                        emptyTitle = emptyTitle,
                        emptyMessage = emptyMessage,
                        onAgendaItemActionClick = onAgendaItemActionClick
                    )

                    TimetablePage.Week -> WeeklyTimetableTab(
                        uiState = uiState,
                        startOfWeek = selectedWeekDate,
                        selectedDate = selectedWeekFocusDate,
                        includedKinds = includedKinds,
                        density = density,
                        emptyDayMessage = emptyMessage,
                        onSelectedDateChange = onSelectedWeekFocusDateChange,
                        onStartOfWeekChange = onSelectedWeekDateChange,
                        onAgendaItemActionClick = onAgendaItemActionClick
                    )

                    TimetablePage.Snapshot -> Unit
                }
            }
        }
    } else {
        when (selectedPage) {
            TimetablePage.Flow -> DailyTimetableTab(
                agendaItems = agendaItems,
                nextDateWithEvent = nextDateWithEvent,
                selectedDate = selectedDate,
                onDateChange = onSelectedDateChange,
                density = density,
                emptyTitle = emptyTitle,
                emptyMessage = emptyMessage,
                onAgendaItemActionClick = onAgendaItemActionClick
            )

            TimetablePage.Snapshot -> PlannerSnapshotTab(
                nowItem = nowItem,
                nextItem = nextItem,
                deadlines = deadlines,
                density = density,
                onDensityChange = onDensityChange,
                includedKinds = includedKinds,
                onIncludedKindsChange = onIncludedKindsChange,
                onJumpToday = onJumpTodayAction,
                onJumpTomorrow = onJumpTomorrowAction,
                onJumpNextClass = onJumpNextClassAction,
                onJumpNextMeeting = onJumpNextMeetingAction
            )

            TimetablePage.Week -> WeeklyTimetableTab(
                uiState = uiState,
                startOfWeek = selectedWeekDate,
                selectedDate = selectedWeekFocusDate,
                includedKinds = includedKinds,
                density = density,
                emptyDayMessage = emptyMessage,
                onSelectedDateChange = onSelectedWeekFocusDateChange,
                onStartOfWeekChange = onSelectedWeekDateChange,
                onAgendaItemActionClick = onAgendaItemActionClick
            )
        }
    }
}

@Composable
private fun PlannerSnapshotTab(
    nowItem: AgendaItemUi?,
    nextItem: AgendaItemUi?,
    deadlines: List<AgendaItemUi>,
    density: AgendaDensity,
    onDensityChange: (AgendaDensity) -> Unit,
    includedKinds: Set<AgendaItemKind>,
    onIncludedKindsChange: (Set<AgendaItemKind>) -> Unit,
    onJumpToday: () -> Unit,
    onJumpTomorrow: () -> Unit,
    onJumpNextClass: () -> Unit,
    onJumpNextMeeting: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        PlannerOverviewPanel(
            nowItem = nowItem,
            nextItem = nextItem,
            deadlines = deadlines,
            compactLayout = true,
            density = density,
            onDensityChange = onDensityChange,
            includedKinds = includedKinds,
            onIncludedKindsChange = onIncludedKindsChange,
            onJumpToday = onJumpToday,
            onJumpTomorrow = onJumpTomorrow,
            onJumpNextClass = onJumpNextClass,
            onJumpNextMeeting = onJumpNextMeeting
        )
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
private fun PlannerOverviewPanel(
    nowItem: AgendaItemUi?,
    nextItem: AgendaItemUi?,
    deadlines: List<AgendaItemUi>,
    compactLayout: Boolean,
    density: AgendaDensity,
    onDensityChange: (AgendaDensity) -> Unit,
    includedKinds: Set<AgendaItemKind>,
    onIncludedKindsChange: (Set<AgendaItemKind>) -> Unit,
    onJumpToday: () -> Unit,
    onJumpTomorrow: () -> Unit,
    onJumpNextClass: () -> Unit,
    onJumpNextMeeting: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (compactLayout) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PlannerSummaryCard(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Now",
                    item = nowItem,
                    emptyText = "No live school moment right now."
                )
                PlannerSummaryCard(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Next",
                    item = nextItem,
                    emptyText = "Nothing else has been planned yet."
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PlannerSummaryCard(
                    modifier = Modifier.weight(1f),
                    label = "Now",
                    item = nowItem,
                    emptyText = "No live school moment right now."
                )
                PlannerSummaryCard(
                    modifier = Modifier.weight(1f),
                    label = "Next",
                    item = nextItem,
                    emptyText = "Nothing else has been planned yet."
                )
            }
        }

        PlannerControlsCard(
            density = density,
            onDensityChange = onDensityChange,
            includedKinds = includedKinds,
            onIncludedKindsChange = onIncludedKindsChange,
            onJumpToday = onJumpToday,
            onJumpTomorrow = onJumpTomorrow,
            onJumpNextClass = onJumpNextClass,
            onJumpNextMeeting = onJumpNextMeeting
        )

        if (deadlines.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.42f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Deadlines & moments",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    deadlines.take(3).forEach { item ->
                        Text(
                            text = "${item.start.format(DateTimeFormatter.ofPattern("EEE HH:mm"))} • ${item.title}${item.statusLabel?.let { " • $it" } ?: ""}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun PlannerControlsCard(
    density: AgendaDensity,
    onDensityChange: (AgendaDensity) -> Unit,
    includedKinds: Set<AgendaItemKind>,
    onIncludedKindsChange: (Set<AgendaItemKind>) -> Unit,
    onJumpToday: () -> Unit,
    onJumpTomorrow: () -> Unit,
    onJumpNextClass: () -> Unit,
    onJumpNextMeeting: () -> Unit
) {
    var showDensityMenu by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }
    val selectedKindsLabel = when {
        includedKinds.size == AgendaItemKind.entries.size -> "All types"
        includedKinds.size == 1 -> includedKinds.first().toLabel()
        else -> "${includedKinds.size} types"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Planner controls",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickJumpChip("Today", onJumpToday)
                QuickJumpChip("Tomorrow", onJumpTomorrow)
                QuickJumpChip("Next class", onJumpNextClass)
                QuickJumpChip("Next meeting", onJumpNextMeeting)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    FilterChip(
                        selected = false,
                        onClick = { showDensityMenu = true },
                        label = {
                            Text(
                                if (density == AgendaDensity.COMFORTABLE) "Comfortable" else "Compact"
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Tune, contentDescription = null)
                        }
                    )
                    DropdownMenu(
                        expanded = showDensityMenu,
                        onDismissRequest = { showDensityMenu = false }
                    ) {
                        AgendaDensity.entries.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (option == AgendaDensity.COMFORTABLE) {
                                            "Comfortable"
                                        } else {
                                            "Compact"
                                        }
                                    )
                                },
                                onClick = {
                                    onDensityChange(option)
                                    showDensityMenu = false
                                },
                                trailingIcon = {
                                    if (density == option) {
                                        Icon(Icons.Default.Check, contentDescription = null)
                                    }
                                }
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    FilterChip(
                        selected = includedKinds.size != AgendaItemKind.entries.size,
                        onClick = { showFilterMenu = true },
                        label = { Text(selectedKindsLabel) },
                        leadingIcon = {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null)
                        }
                    )
                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All types") },
                            onClick = {
                                onIncludedKindsChange(AgendaItemKind.entries.toSet())
                                showFilterMenu = false
                            },
                            trailingIcon = {
                                if (includedKinds.size == AgendaItemKind.entries.size) {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                }
                            }
                        )
                        AgendaItemKind.entries.forEach { kind ->
                            DropdownMenuItem(
                                text = { Text(kind.toLabel()) },
                                onClick = {
                                    val next = includedKinds.toMutableSet().apply {
                                        if (kind in this) remove(kind) else add(kind)
                                    }
                                    onIncludedKindsChange(
                                        if (next.isEmpty()) AgendaItemKind.entries.toSet() else next
                                    )
                                },
                                trailingIcon = {
                                    if (kind in includedKinds) {
                                        Icon(Icons.Default.Check, contentDescription = null)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlannerSummaryCard(
    modifier: Modifier = Modifier,
    label: String,
    item: AgendaItemUi?,
    emptyText: String
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            if (item == null) {
                Text(
                    text = emptyText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${item.start.format(DateTimeFormatter.ofPattern("EEE HH:mm"))} • ${item.badge}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                item.statusLabel?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun QuickJumpChip(
    label: String,
    onClick: () -> Unit
) {
    InputChip(
        selected = false,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
        }
    )
}

private fun buildEmptyTitle(
    audience: String,
    selectedDate: LocalDate,
    includedKinds: Set<AgendaItemKind>,
    showOnlyMine: Boolean
): String {
    val dayLabel = selectedDate.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.getDefault())
    return when {
        includedKinds.size < AgendaItemKind.entries.size -> "Nothing in the current filters for $dayLabel"
        showOnlyMine -> "No personal school moments are scheduled for $dayLabel"
        audience.contains("PARENT", ignoreCase = true) -> "No school moments are scheduled for $dayLabel"
        audience.contains("TEACHER", ignoreCase = true) -> "No teaching moments are scheduled for $dayLabel"
        else -> "The schedule is clear for $dayLabel"
    }
}

private fun buildEmptyMessage(
    audience: String,
    selectedLearnerCount: Int,
    includedKinds: Set<AgendaItemKind>,
    showOnlyMine: Boolean
): String = when {
    includedKinds.size < AgendaItemKind.entries.size ->
        "Try widening the filters if you want to bring back classes, meetings, calls, or school notices."
    showOnlyMine ->
        "Only your own school schedule is visible right now. Turn that filter off whenever you want to bring your children back into view."
    audience.contains("PARENT", ignoreCase = true) && selectedLearnerCount == 1 ->
        "When this child has a class, meeting, call invite, or school notice, it will appear here in the order it happens."
    audience.contains("PARENT", ignoreCase = true) ->
        "Classes, parent meetings, school calls, and school notices will appear here once they are scheduled."
    audience.contains("TEACHER", ignoreCase = true) ->
        "Classes, invigilation, department meetings, and scheduled calls will appear here once they are planned."
    else ->
        "Any upcoming class, meeting, call invite, or school notice will appear here in one continuous schedule."
}

@Composable
private fun SelectedSchoolStrip(
    schoolLabels: List<String>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        schoolLabels.forEach { schoolLabel ->
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.74f)
            ) {
                Text(
                    text = shortSchoolLabel(schoolLabel),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun learnerSelectionSummary(
    students: List<TimetableStudent>,
    selectedStudentIds: Set<String>
): String = when {
    students.isEmpty() -> "Schedule"
    selectedStudentIds.isEmpty() -> "All children"
    selectedStudentIds.size == 1 -> {
        students.firstOrNull { it.id in selectedStudentIds }?.name?.let(::shortLearnerLabel) ?: "1 child"
    }
    else -> "${selectedStudentIds.size} children"
}

private fun timetableAudienceSummary(
    students: List<TimetableStudent>,
    selectedStudentIds: Set<String>,
    showOnlyMine: Boolean
): String = if (showOnlyMine) {
    "Only your own schedule"
} else {
    learnerSelectionSummary(students, selectedStudentIds)
}

private fun shortLearnerLabel(name: String): String {
    val firstName = name.trim().substringBefore(" ").ifBlank { name.trim() }
    return if (firstName.length <= 14) firstName else "${firstName.take(13)}…"
}

private fun shortSchoolLabel(name: String): String =
    name
        .replace("School", "Sch.")
        .replace("University", "Univ.")
        .replace("Institute", "Inst.")
        .let { if (it.length <= 22) it else "${it.take(21)}…" }

private fun AgendaItemKind.toLabel(): String = when (this) {
    AgendaItemKind.CLASS -> "Classes"
    AgendaItemKind.ASSESSMENT -> "Assessments"
    AgendaItemKind.MEETING -> "Meetings"
    AgendaItemKind.CALL -> "Calls"
    AgendaItemKind.ANNOUNCEMENT -> "Announcements"
    AgendaItemKind.PERSONAL -> "Personal plans"
}

@Composable
private fun TimetableContentPane(
    modifier: Modifier = Modifier,
    hasError: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit,
    content: @Composable () -> Unit
) {
    if (hasError) {
        Box(
            modifier = modifier.padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            FriendlyNetworkErrorCard(
                rawMessage = errorMessage,
                onRetry = onRetry
            )
        }
    } else {
        Box(
            modifier = modifier
                .sizeIn(minHeight = 0.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun TimetableHero(
    highlights: List<AgendaItemUi>,
    audience: String,
    scopeLabel: String?,
    compact: Boolean
) {
    if (compact) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = {},
                        label = { Text(audience.lowercase().replaceFirstChar { it.titlecase() }) },
                        leadingIcon = {
                            Icon(Icons.Default.Groups, contentDescription = null)
                        }
                    )
                    scopeLabel?.takeIf { it.isNotBlank() }?.let {
                        AssistChip(
                            onClick = {},
                            label = { Text(it) },
                            leadingIcon = {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null)
                            }
                        )
                    }
                }

                highlights.firstOrNull()?.let { item ->
                    Text(
                        text = "Next up: ${item.start.format(DateTimeFormatter.ofPattern("EEE HH:mm"))} • ${item.title}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        return
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = if (compact) 10.dp else 16.dp),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.68f)
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = if (compact) 16.dp else 20.dp, vertical = if (compact) 14.dp else 20.dp),
            verticalArrangement = Arrangement.spacedBy(if (compact) 10.dp else 14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AssistChip(
                    onClick = {},
                    label = { Text(audience.lowercase().replaceFirstChar { it.titlecase() }) },
                    leadingIcon = {
                        Icon(Icons.Default.Groups, contentDescription = null)
                    }
                )
                scopeLabel?.takeIf { it.isNotBlank() }?.let {
                    AssistChip(
                        onClick = {},
                        label = { Text(it) },
                        leadingIcon = {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null)
                        }
                    )
                }
            }

            Text(
                text = "A calmer view of school time",
                style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = if (compact) {
                    "Day, now, and week are split so the real timetable stays easy to scan."
                } else {
                    "Classes, planned thread calls, parent-teacher meetings, and live school moments now show up in one place so the schedule feels more like a daily rhythm than a rigid table."
                },
                style = if (compact) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!compact && highlights.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Coming up next",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    highlights.take(if (compact) 1 else 3).forEach { item ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (item.kind == AgendaItemKind.CALL || item.kind == AgendaItemKind.MEETING) Icons.Default.VideoCall else Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${item.start.format(DateTimeFormatter.ofPattern("EEE HH:mm"))} • ${item.title}",
                                style = if (compact) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimetableInfoSheet(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "About this timetable",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Day focuses on the real schedule. Now keeps the live summary, quick jumps, and filters together. Week gives the broader view.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "The phone layout keeps explanation out of the way so classes and meetings stay visible first.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedButton(onClick = onDismiss) {
            Text("Close")
        }
        Spacer(Modifier.height(12.dp))
    }
}
