package com.schoolbridge.v2.ui.home.timetable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
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
    onAddEvent: (LocalTime, LocalTime, String, String, PersonalPlanType) -> Unit
) {
    var startTime by remember { mutableStateOf(LocalTime.of(8, 0)) }
    var endTime by remember { mutableStateOf(LocalTime.of(9, 0)) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(PersonalPlanType.STUDY_BLOCK) }
    var showTypeMenu by remember { mutableStateOf(false) }

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
            text = "Plan something on $formattedDate",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.Top) {
            Icon(Icons.Default.Info, contentDescription = "Info", modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text(
                text = "Use this for your own school planning, like study blocks, homework sessions, project checkpoints, or group work. Official timetable entries still come from the school.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { if (it.length <= 60) title = it },
            label = { Text("Plan title") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { if (it.length <= 180) description = it },
            label = { Text("Note or purpose (optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4
        )

        Spacer(Modifier.height(12.dp))

        Box {
            OutlinedButton(onClick = { showTypeMenu = true }) {
                Text("Type: ${selectedType.toLabel()}")
            }
            DropdownMenu(
                expanded = showTypeMenu,
                onDismissRequest = { showTypeMenu = false }
            ) {
                PersonalPlanType.entries.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.toLabel()) },
                        onClick = {
                            selectedType = type
                            showTypeMenu = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        TimePickerField(
            label = "Start time",
            onTimeChange = { startTime = it }
        )

        Spacer(Modifier.height(8.dp))

        TimePickerField(
            label = "End time",
            onTimeChange = { endTime = it }
        )

        Spacer(Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(onClick = onDismiss) { Text("Cancel") }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = { onAddEvent(startTime, endTime, title, description, selectedType) },
                enabled = title.isNotBlank() && startTime < endTime
            ) {
                Text("Save plan")
            }
        }
    }
}



@Composable
fun TimePickerField(
    label: String,
    onTimeChange: (LocalTime) -> Unit
) {
    var timeText by remember { mutableStateOf("") }
    var isValid by remember { mutableStateOf(true) }
    var parsedTime by remember { mutableStateOf<LocalTime?>(null) }

    fun parseTime(input: String): LocalTime? {
        val normalizedInput = input.replace("[ ,.-]".toRegex(), ":")
        return try {
            val parts = normalizedInput.split(":")
            val hour = parts[0].padStart(2, '0')
            val minute = if (parts.size > 1) parts[1].padStart(2, '0') else "00"
            LocalTime.parse("$hour:$minute", DateTimeFormatter.ofPattern("HH:mm"))
        } catch (e: Exception) {
            null
        }
    }

    OutlinedTextField(
        value = timeText,
        onValueChange = { input ->
            timeText = input
            parseTime(input)?.let {
                isValid = true
                parsedTime = it
                onTimeChange(it)
            } ?: run {
                isValid = false
                parsedTime = null
            }
        },
        label = { Text(label) },
        placeholder = { Text("e.g. 08.30 or 8-30 or 08,3") },
        singleLine = true,
        isError = !isValid && timeText.isNotEmpty(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )

    when {
        !isValid && timeText.isNotEmpty() -> {
            Text(
                text = "Invalid time format. Try 08:00, 8-30, 0830, etc.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
        isValid && parsedTime != null -> {
            Text(
                text = "Selected: ${parsedTime!!.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

private fun PersonalPlanType.toLabel(): String = when (this) {
    PersonalPlanType.STUDY_BLOCK -> "Study block"
    PersonalPlanType.HOMEWORK -> "Homework"
    PersonalPlanType.GROUP_WORK -> "Group work"
    PersonalPlanType.PROJECT_MILESTONE -> "Project milestone"
    PersonalPlanType.CLUB_ACTIVITY -> "Club activity"
    PersonalPlanType.MEETING -> "Meeting"
    PersonalPlanType.REMINDER -> "Reminder"
}



