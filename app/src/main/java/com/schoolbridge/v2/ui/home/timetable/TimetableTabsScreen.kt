package com.schoolbridge.v2.ui.home.timetable

import AddEventBottomSheet
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale
import java.time.DayOfWeek // Import DayOfWeek
import java.time.temporal.TemporalAdjusters // NEW IMPORT

// Assuming AddEventBottomSheet and sampleEvents are defined elsewhere

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableTabsScreen(
    onBack: () -> Unit,
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Weekly", "Daily")

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showAddSheet by remember { mutableStateOf(false) }

    val today = LocalDate.now()

    // Initialize selectedDate for Daily Timetable
    var selectedDate by remember { mutableStateOf(today) }

    // Initialize selectedWeekDate to the Monday of the current week
    var selectedWeekDate by remember {
        mutableStateOf(today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)))
    }

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
                                    text = "Week $weekNumber, ${startOfWeek.year}", // Example: "Week 26, 2025"
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    // CORRECTED PATTERN: Use "MMM d, yyyy" for full date with year
                                    text = "${startOfWeek.format(DateTimeFormatter.ofPattern("MMM d"))} - ${endOfWeek.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        1 -> { // Daily Tab
                            Text(
                                // CORRECTED PATTERN: Use "EEEE, MMM d, yyyy" for full date with year
                                text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy")),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // ...
                },
                scrollBehavior = scrollBehavior
            )
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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

            Spacer(Modifier.height(8.dp))

            when (selectedTabIndex) {
                0 -> WeeklyTimetableTab(
                    events = sampleEvents,
                    startOfWeek = selectedWeekDate
                )
                1 -> DailyTimetableTab(
                    selectedDate = selectedDate,
                    onDateChange = { selectedDate = it }
                )
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