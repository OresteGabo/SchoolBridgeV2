package com.schoolbridge.v2.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
    alreadyHasRole: Boolean = false,
    onSubmit: (school: String, level: String, dob: String) -> Unit,
    onCancel: () -> Unit
) {
    var selectedSchool by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    RoleRequestFormScaffold(
        title = if (alreadyHasRole) "Link Another Student Profile" else "Request Student Access",
        subtitle = if (alreadyHasRole) {
            "If you study at another campus or need a second verified student record, submit the details here for review."
        } else {
            "Ask the school to verify your student record so you can open your personal schedule, learning, and finance tools."
        },
        actionLabel = if (alreadyHasRole) "Request Another Student Link" else "Send Request",
        onBack = onCancel,
        onSubmit = { onSubmit(selectedSchool, level, dob) }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = selectedSchool,
                onValueChange = { selectedSchool = it },
                label = { Text("School Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = level,
                onValueChange = { level = it },
                label = { Text("Academic Level (e.g., S4 Science)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = dob,
                onValueChange = { dob = it },
                label = { Text("Date of Birth") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
