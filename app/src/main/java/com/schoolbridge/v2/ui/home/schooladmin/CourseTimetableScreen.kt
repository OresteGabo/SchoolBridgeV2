package com.schoolbridge.v2.ui.home.schooladmin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.schoolbridge.v2.domain.academic.TimetableEntry
import com.schoolbridge.v2.domain.academic.TimetableEntryType
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseTimetableScreen() {
    val allTypes = TimetableEntryType.values().toList()
    var selectedType by remember { mutableStateOf<TimetableEntryType?>(null) }
    var timetableEntries by remember { mutableStateOf(sampleTimetableEntries) }
    val bottomSheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Courses") },
                actions = {
                    IconButton(onClick = { showBottomSheet = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showBottomSheet = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Course")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            FilterChipsRow(
                types = allTypes,
                selectedType = selectedType,
                onSelected = { selectedType = if (selectedType == it) null else it }
            )
            LazyColumn {
                val filteredEntries = timetableEntries.filter { selectedType == null || it.type == selectedType }
                items(filteredEntries) { entry ->
                    TimetableEntryCard(entry)
                }
            }
        }
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = bottomSheetState
            ) {
                AddCourseForm(onAdd = {
                    timetableEntries = timetableEntries + it
                    showBottomSheet = false
                })
            }
        }
    }
}


val sampleTimetableEntries = listOf(
    TimetableEntry(
        id = 1,
        start = LocalDateTime.of(2025, 7, 14, 10, 0),
        end = LocalDateTime.of(2025, 7, 14, 11, 0),
        title = "Mathematics Lecture",
        room = "Room A1",
        teacher = "Mr. Habimana",
        type = TimetableEntryType.LECTURE
    ),
    TimetableEntry(
        id = 2,
        start = LocalDateTime.of(2025, 7, 14, 11, 0),
        end = LocalDateTime.of(2025, 7, 14, 12, 0),
        title = "Physics Lab",
        room = "Lab 3",
        teacher = "Ms. Uwimana",
        type = TimetableEntryType.LAB
    )
)


@Preview
@Composable
private fun CourseTimetableScreenPrev() {
    CourseTimetableScreen()
}