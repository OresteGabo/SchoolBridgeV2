package com.schoolbridge.v2.ui.home.alert

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.ui.event.EventRepository
import com.schoolbridge.v2.ui.home.EventCardCompact

import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    onBack: () -> Unit,
    onEventClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val eventRepository = remember { EventRepository() }
    val allEvents = remember { eventRepository.getUpcomingEvents() }

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var showOnlyMandatory by rememberSaveable { mutableStateOf(false) }
    var showOnlyRSVP by rememberSaveable { mutableStateOf(false) }
    var sortBy by rememberSaveable { mutableStateOf("Soonest") }

    val filteredAndSortedEvents = remember(searchQuery, showOnlyMandatory, showOnlyRSVP, sortBy, allEvents) {
        allEvents
            .filter { event ->
                (searchQuery.isBlank() || event.title.contains(searchQuery, ignoreCase = true)) &&
                        (!showOnlyMandatory || event.isMandatory) &&
                        (!showOnlyRSVP || event.requiresRSVP)
            }
            .sortedWith(
                when (sortBy) {
                    "Respond by" -> compareBy { it.rsvpDeadline ?: LocalDateTime.MAX }
                    "Title (A–Z)" -> compareBy { it.title }
                    else -> compareBy { it.startTime }
                }
            )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Events") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            // 🔍 Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Events") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // 🧠 Filter options
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterChip(
                    selected = showOnlyMandatory,
                    onClick = { showOnlyMandatory = !showOnlyMandatory },
                    label = { Text("Mandatory Only") }
                )
                FilterChip(
                    selected = showOnlyRSVP,
                    onClick = { showOnlyRSVP = !showOnlyRSVP },
                    label = { Text("Needs Response") }
                )
            }

            // 🔁 Sort dropdown
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text("Sort by:", modifier = Modifier.padding(end = 8.dp))
                var expanded by remember { mutableStateOf(false) }

                Box {
                    OutlinedButton(onClick = { expanded = true }) {
                        Text(sortBy)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }

                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = { Text("Soonest") }, onClick = {
                            sortBy = "Soonest"
                            expanded = false
                        })
                        DropdownMenuItem(text = { Text("Respond deadline") }, onClick = {
                            sortBy = "Respond deadline"
                            expanded = false
                        })
                        DropdownMenuItem(text = { Text("Title (A–Z)") }, onClick = {
                            sortBy = "Title (A–Z)"
                            expanded = false
                        })
                    }
                }
            }

            // 📅 Event list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredAndSortedEvents.size) { eventIndex ->
                    EventCardCompact(
                        event = filteredAndSortedEvents[eventIndex],
                        index = eventIndex, // just for uniqueness
                        onEventClick = onEventClick
                    )
                }

                if (filteredAndSortedEvents.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.EventBusy,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(64.dp)
                            )
                            Text("No matching events found", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

