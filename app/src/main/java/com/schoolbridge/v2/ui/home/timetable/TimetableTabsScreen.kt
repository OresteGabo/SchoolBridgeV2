package com.schoolbridge.v2.ui.home.timetable

import AddEventBottomSheet
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.viewmodel.compose.viewModel
import com.schoolbridge.v2.data.remote.TimetableApiServiceImpl
import com.schoolbridge.v2.data.repository.implementations.TimetableRepositoryImpl
import com.schoolbridge.v2.data.session.UserSessionManager
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.time.DayOfWeek
import java.time.temporal.TemporalAdjusters
import com.schoolbridge.v2.ui.common.AdaptivePageFrame
import com.schoolbridge.v2.ui.common.SchoolBridgePatternBackground
import com.schoolbridge.v2.ui.common.isExpandedLayout

// Assuming AddEventBottomSheet and sampleEvents are defined elsewhere

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableTabsScreen(
    userSessionManager: UserSessionManager,
    onBack: (() -> Unit)? = null,
    bottomBar: @Composable (() -> Unit)? = null
) {
    val viewModel: TimetableViewModel = viewModel(
        factory = TimetableViewModelFactory(
            TimetableRepositoryImpl(TimetableApiServiceImpl(userSessionManager))
        )
    )
    val uiState by viewModel.uiState.collectAsState()
    val isExpanded = isExpandedLayout()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Weekly", "Daily")

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showAddSheet by remember { mutableStateOf(false) }

    val today = LocalDate.now()

    var selectedDate by remember { mutableStateOf(today) }
    var selectedWeekDate by rememberSaveable {
        mutableStateOf(LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)))
    }
    val weeklyEvents = remember(uiState, selectedWeekDate) { uiState.weeklyEntries(selectedWeekDate) }
    val dailyEvents = remember(uiState, selectedDate) { uiState.dailyEntries(selectedDate) }
    val nextDateWithEvent = remember(uiState, selectedDate) { uiState.nextDateWithEvent(selectedDate.plusDays(1)) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    when (selectedTabIndex) {
                        0 -> { // Weekly Tab
                            val startOfWeek = selectedWeekDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                            val endOfWeek = startOfWeek.plusDays(6)

                            val weekFields = WeekFields.of(DayOfWeek.MONDAY, 1)
                            val weekNumber = startOfWeek.get(weekFields.weekOfWeekBasedYear())

                            Column {
                                Text(
                                    text = "Week $weekNumber, ${startOfWeek.year}",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = "${startOfWeek.format(DateTimeFormatter.ofPattern("MMM d"))} - ${endOfWeek.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        1 -> { // Daily Tab
                            Text(
                                text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy")),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
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
                    if (selectedTabIndex == 0 && selectedWeekDate != today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))) {
                        OutlinedButton(
                            onClick = {
                                selectedWeekDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                            },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("This Week")
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            bottomBar?.invoke()
        },
        floatingActionButton = {
            if (selectedTabIndex == 1) {
                FloatingActionButton(onClick = {
                    showAddSheet = true
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Event")
                }
            }
        }
    ) { paddingValues ->
        AdaptivePageFrame(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues,
            maxContentWidth = if (isExpanded) 1320.dp else 1240.dp
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                SchoolBridgePatternBackground(dotAlpha = 0.018f, gradientAlpha = 0.04f)
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = {
                                    selectedTabIndex = index
                                    if (index == 0) {
                                        selectedWeekDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                                    } else {
                                        selectedDate = today
                                    }
                                },
                                text = { Text(title) }
                            )
                        }
                    }

                    if (uiState.students.size > 1) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            uiState.students.forEach { student ->
                                FilterChip(
                                    selected = uiState.selectedStudentId == student.id,
                                    onClick = { viewModel.selectStudent(student.id) },
                                    label = { Text(student.name) }
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }

                    Spacer(Modifier.height(8.dp))

                    when (selectedTabIndex) {
                        0->WeeklyTimetableTab(
                            events = weeklyEvents,
                            initialStartOfWeek = selectedWeekDate,
                            onStartOfWeekChange = { newWeek -> selectedWeekDate = newWeek }
                        )
                        1 -> DailyTimetableTab(
                            dailyEvents = dailyEvents,
                            nextDateWithEvent = nextDateWithEvent,
                            selectedDate = selectedDate,
                            onDateChange = { selectedDate = it },
                        )
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
                onAddEvent = { start, end, description ->
                    // TODO: Handle adding the event
                    showAddSheet = false
                }
            )
        }
    }
}
