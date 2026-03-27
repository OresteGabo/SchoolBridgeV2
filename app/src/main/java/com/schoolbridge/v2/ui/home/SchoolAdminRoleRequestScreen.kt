package com.schoolbridge.v2.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SchoolAdminRoleRequestScreen(
    alreadyHasRole: Boolean = false,
    onSubmit: () -> Unit,
    onCancel: () -> Unit
) {
    var schoolName by remember { mutableStateOf("") }
    var responsibility by remember { mutableStateOf("") }

    RoleRequestFormScaffold(
        title = if (alreadyHasRole) "Request More Admin Scope" else "Request School Admin Role",
        subtitle = if (alreadyHasRole) {
            "You already have admin access. Submit this request to add another school, campus, or broader administrative responsibility."
        } else {
            "Provide the school and responsibility details the admin team can use to verify your access."
        },
        actionLabel = if (alreadyHasRole) "Request Additional Access" else "Submit Request",
        onBack = onCancel,
        onSubmit = onSubmit
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = schoolName,
                onValueChange = { schoolName = it },
                label = { Text("School or Campus") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = responsibility,
                onValueChange = { responsibility = it },
                label = { Text("Role or Responsibility") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                maxLines = 6
            )
            Text(
                text = "This request can later connect to document checks and chat-based follow-up from the approving school admin.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
