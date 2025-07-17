package com.schoolbridge.v2.ui.home.schooladmin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.domain.academic.TimetableEntry
import com.schoolbridge.v2.domain.academic.timetableEntryColor

@Composable
internal fun TimetableEntryCard(entry: TimetableEntry) {
    val backgroundColor = timetableEntryColor(entry.type)
    Card(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(entry.title, style = MaterialTheme.typography.titleMedium)
            Text("${entry.type} • ${entry.room}", style = MaterialTheme.typography.bodyMedium)
            Text(
                "${entry.start.toLocalTime()} - ${entry.end.toLocalTime()}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
