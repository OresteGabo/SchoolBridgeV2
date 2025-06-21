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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableTabsScreen(
    onBack: () -> Unit,
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Weekly", "Daily")

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showAddSheet by remember { mutableStateOf(false) }

    // Lift selectedDate here
    val today = LocalDate.now()
    var selectedDate by remember { mutableStateOf(today) }

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Timetable") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
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
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                when (selectedTabIndex) {
                    0 -> WeeklyTimetableTab(events = sampleEvents)
                    1 -> DailyTimetableTab(
                        selectedDate = selectedDate,
                        onDateChange = { selectedDate = it }
                    )
                }
            }
        }

        // Bottom Sheet
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
}










