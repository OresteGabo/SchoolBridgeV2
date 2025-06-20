package com.schoolbridge.v2.ui.home.timetable

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Horizontal +/- zoom buttons to be placed in a bottom bar.
 */
@Composable
fun FloatingZoomControls(
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .padding(bottom = 24.dp, end = 16.dp),     // margin from screen edges
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FloatingActionButton(
            onClick = onZoomIn,
            containerColor = MaterialTheme.colorScheme.primary
        ) { Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In") }

        FloatingActionButton(
            onClick = onZoomOut,
            containerColor = MaterialTheme.colorScheme.primary
        ) { Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out") }
    }
}

