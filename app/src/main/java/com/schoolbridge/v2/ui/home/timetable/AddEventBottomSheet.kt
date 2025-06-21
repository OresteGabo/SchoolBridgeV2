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
import java.time.format.DateTimeParseException

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
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.Top) {
            Icon(Icons.Default.Info, contentDescription = "Info", modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text(
                text = "Note: These events are only visible to you. Use them to manage study time, homework, etc. They can be deleted anytime. Official school timetable is managed by your school.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { if (it.length <= 15) description = it },
            label = { Text("Description (max 15 chars)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

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





