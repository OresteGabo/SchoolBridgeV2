package com.schoolbridge.v2.ui.home.timetable

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.ui.unit.dp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FloatingZoomControls(
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onAddPersonalEvent: () -> Unit,
    onNavigateToday: () -> Unit, // New action for "Go to Today"
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(bottom = 24.dp, end = 16.dp),
        horizontalAlignment = Alignment.End // Align children to the end (right)
    ) {
        // Mini FABs (hidden when collapsed)
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom)
        ) {
            Column(horizontalAlignment = Alignment.End) { // Ensure mini-FABs align right
                SmallFloatingActionButton(
                    onClick = onAddPersonalEvent,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Personal Event")
                }

                SmallFloatingActionButton(
                    onClick = onNavigateToday,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = "Go to Today")
                }

                SmallFloatingActionButton(
                    onClick = onZoomIn,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In")
                }

                SmallFloatingActionButton(
                    onClick = onZoomOut,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out")
                }
            }
        }

        // Main FAB (always visible)
        FloatingActionButton(
            onClick = { expanded = !expanded },
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            // Change icon based on expanded state
            Icon(
                imageVector = if (expanded) Icons.Default.MeetingRoom else Icons.Default.Add, // Example: could be more relevant like 'MoreVert'
                contentDescription = if (expanded) "Collapse actions" else "Expand actions"
            )
        }
    }
}


