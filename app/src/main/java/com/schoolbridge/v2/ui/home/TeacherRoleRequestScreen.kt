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
fun TeacherRoleRequestScreen(
    alreadyHasRole: Boolean = false,
    onSubmit: (school: String, message: String) -> Unit,
    onCancel: () -> Unit
) {
    var selectedSchool by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    RoleRequestFormScaffold(
        title = if (alreadyHasRole) "Request Another Teaching Assignment" else "Request Teacher Access",
        subtitle = if (alreadyHasRole) {
            "Use this to ask for another class load, campus, or school assignment without losing your current teacher access."
        } else {
            "Tell the school where and what you teach so your staff access can be reviewed and approved."
        },
        actionLabel = if (alreadyHasRole) "Request Assignment" else "Send Request",
        onBack = onCancel,
        onSubmit = { onSubmit(selectedSchool, description) }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = selectedSchool,
                onValueChange = { selectedSchool = it },
                label = { Text("School Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Courses & Role Description") },
                placeholder = { Text("E.g., I teach Physics and Math to Senior 4 and 5") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 5,
                maxLines = 8
            )
        }
    }
}
