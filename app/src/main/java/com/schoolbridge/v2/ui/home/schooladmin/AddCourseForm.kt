package com.schoolbridge.v2.ui.home.schooladmin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.domain.academic.TimetableEntry
import com.schoolbridge.v2.domain.academic.TimetableEntryType
import java.time.LocalDateTime
import java.util.Random

@Composable
fun AddCourseForm(onAdd: (TimetableEntry) -> Unit) {
    var title by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("") }
    var teacher by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(TimetableEntryType.LECTURE) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Add Course", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
        OutlinedTextField(value = room, onValueChange = { room = it }, label = { Text("Room") })
        OutlinedTextField(value = teacher, onValueChange = { teacher = it }, label = { Text("Teacher") })
        Spacer(Modifier.height(8.dp))
        Text("Type")
        DropdownMenuBox(current = type, onChange = { type = it })
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            val now = LocalDateTime.now()
            val random = Random()
            onAdd(TimetableEntry(id = random.nextInt(), start = now, end = now.plusHours(1), title, room, teacher, type))
        }) {
            Text("Add Course")
        }
    }
}
