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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.data.dto.user.SchoolLookupDto
import kotlinx.coroutines.launch

@Composable
fun SchoolAdminRoleRequestScreen(
    alreadyHasRole: Boolean = false,
    onSearchSchools: suspend (String) -> List<SchoolLookupDto>,
    onSubmit: suspend (school: SchoolLookupDto, responsibility: String) -> Unit,
    onCancel: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var schoolQuery by remember { mutableStateOf("") }
    var schoolLookupMode by remember { mutableStateOf(LookupMode.ID) }
    var schoolResults by remember { mutableStateOf(emptyList<SchoolLookupDto>()) }
    var selectedSchool by remember { mutableStateOf<SchoolLookupDto?>(null) }
    var isSearching by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf<String?>(null) }
    var responsibility by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var submitError by remember { mutableStateOf<String?>(null) }

    RoleRequestFormScaffold(
        title = if (alreadyHasRole) "Request More Admin Scope" else "Request School Admin Role",
        subtitle = if (alreadyHasRole) {
            "You already have admin access. Submit this request to add another school, campus, or broader administrative responsibility."
        } else {
            "Provide the school and responsibility details the admin team can use to verify your access."
        },
        actionLabel = if (alreadyHasRole) "Request Additional Access" else "Submit Request",
        submitEnabled = selectedSchool != null,
        isSubmitting = isSubmitting,
        submitErrorMessage = submitError,
        onBack = onCancel,
        onSubmit = {
            val school = selectedSchool ?: return@RoleRequestFormScaffold
            scope.launch {
                isSubmitting = true
                submitError = null
                runCatching { onSubmit(school, responsibility) }
                    .onFailure { submitError = it.message ?: "Could not submit your request" }
                isSubmitting = false
            }
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SchoolLookupSection(
                title = "1. Find the school",
                query = schoolQuery,
                mode = schoolLookupMode,
                isSearching = isSearching,
                selectedSchool = selectedSchool,
                results = schoolResults,
                errorMessage = searchError,
                onQueryChange = {
                    schoolQuery = it
                    searchError = null
                },
                onModeSelected = { schoolLookupMode = it },
                onSearch = {
                    scope.launch {
                        isSearching = true
                        searchError = null
                        runCatching { onSearchSchools(schoolQuery) }
                            .onSuccess { schoolResults = it }
                            .onFailure { searchError = it.message ?: "School search failed" }
                        isSearching = false
                    }
                },
                onSelect = { selectedSchool = it }
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
                text = "Requests should target a real school record so the correct admin team can see and validate them on refresh.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
