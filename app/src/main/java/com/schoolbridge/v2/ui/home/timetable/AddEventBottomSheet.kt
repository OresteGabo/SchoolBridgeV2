package com.schoolbridge.v2.ui.home.timetable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun AddEventBottomSheet(
    selectedDate: LocalDate,
    onDismiss: () -> Unit,
    onAddEvent: (LocalTime, LocalTime, String) -> Unit
) {
    var startTime by remember { mutableStateOf(LocalTime.of(8, 0)) }
    var endTime by remember { mutableStateOf(LocalTime.of(9, 0)) }
    var description by remember { mutableStateOf("") }

    val formattedDate = remember(selectedDate) {
        val day = selectedDate.dayOfMonth
        val suffix = when {
            day in 11..13 -> "th"
            day % 10 == 1 -> "st"
            day % 10 == 2 -> "nd"
            day % 10 == 3 -> "rd"
            else -> "th"
        }
        val month = selectedDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        "$day$suffix $month"
    }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Add Event on $formattedDate",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Note: These events are for your personal use only. They won't be seen or managed by the school. Use them to plan study time, homework, or reminders. You can delete them anytime.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = {
                if (it.length <= 15) description = it
            },
            label = { Text("Description (max 15 chars)") },
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Start:", modifier = Modifier.width(60.dp))
            TimePickerField(time = startTime) { startTime = it }
        }

        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("End:", modifier = Modifier.width(60.dp))
            TimePickerField(time = endTime) { endTime = it }
        }

        Spacer(Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
            TextButton(onClick = onDismiss) { Text("Cancel") }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = { onAddEvent(startTime, endTime, description) },
                enabled = description.isNotBlank() && startTime < endTime
            ) {
                Text("Add")
            }
        }
    }
}

@Composable
fun TimePickerField(
    time: LocalTime,
    onTimeChange: (LocalTime) -> Unit
) {
    var timeText by remember { mutableStateOf(time.format(DateTimeFormatter.ofPattern("HH:mm"))) }
    var isValid by remember { mutableStateOf(true) }

    OutlinedTextField(
        value = timeText,
        onValueChange = {
            timeText = it
            try {
                val parsed = LocalTime.parse(it, DateTimeFormatter.ofPattern("HH:mm"))
                isValid = true
                onTimeChange(parsed)
            } catch (e: Exception) {
                isValid = false
            }
        },
        label = { Text("HH:mm") },
        singleLine = true,
        isError = !isValid
    )
}