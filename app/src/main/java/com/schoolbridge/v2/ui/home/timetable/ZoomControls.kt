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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
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
fun FloatingTimetableControls(
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onNavigatePreviousWeek: () -> Unit,
    onNavigateNextWeek: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(bottom = 24.dp, end = 16.dp),
        horizontalAlignment = Alignment.End
    ) {
        // Instead of a toggle button, just show all action buttons always:

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(0.8f) // optional width limit
        ) {
            SmallFloatingActionButton(onClick = onNavigatePreviousWeek,modifier = Modifier.size(56.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Week")
            }
            SmallFloatingActionButton(onClick = onNavigateNextWeek,modifier = Modifier.size(56.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Week")
            }
            SmallFloatingActionButton(onClick = onZoomIn,modifier = Modifier.size(56.dp)) {
                Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In")
            }

            SmallFloatingActionButton(onClick = onZoomOut,modifier = Modifier.size(56.dp)) {
                Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        /* Uncomment if you want this button always visible too
        SmallFloatingActionButton(onClick = onAddPersonalEvent) {
            Icon(Icons.Default.Event, contentDescription = "Add Personal Event")
        }
        */
    }
}




