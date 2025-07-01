package com.schoolbridge.v2.ui.home

import androidx.compose.foundation.layout.Arrangement
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
@Composable
fun TeacherRoleRequestScreen(
    onSubmit: (school: String, message: String) -> Unit,
    onCancel: () -> Unit
) {
    var selectedSchool by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Request Teacher Access", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(value = selectedSchool, onValueChange = { selectedSchool = it }, label = { Text("School Name") })
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Courses & Role Description") },
            placeholder = { Text("E.g., I teach Physics and Math to Senior 4 and 5") },
            modifier = Modifier.height(150.dp),
            maxLines = 6
        )

        Spacer(Modifier.height(16.dp))
        Button(onClick = { onSubmit(selectedSchool, description) }) {
            Text("Send Request")
        }
    }
}
