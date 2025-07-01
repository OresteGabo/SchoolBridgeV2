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
fun StudentRoleRequestScreen(
    onSubmit: (school: String, level: String, dob: String) -> Unit,
    onCancel: () -> Unit
) {
    var selectedSchool by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }

    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Request Student Access", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(value = selectedSchool, onValueChange = { selectedSchool = it }, label = { Text("School Name") })
        OutlinedTextField(value = level, onValueChange = { level = it }, label = { Text("Academic Level (e.g., S4 Science)") })
        OutlinedTextField(value = dob, onValueChange = { dob = it }, label = { Text("Date of Birth") })

        Spacer(Modifier.height(16.dp))
        Button(onClick = { onSubmit(selectedSchool, level, dob) }) {
            Text("Send Request")
        }
    }
}
