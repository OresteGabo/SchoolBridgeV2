package com.schoolbridge.v2.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
fun StudentRoleRequestScreen(
    alreadyHasRole: Boolean = false,
    onSearchSchools: suspend (String) -> List<SchoolLookupDto>,
    onSubmit: (school: SchoolLookupDto, level: String, dob: String) -> Unit,
    onCancel: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var schoolQuery by remember { mutableStateOf("") }
    var schoolLookupMode by remember { mutableStateOf(LookupMode.ID) }
    var schoolResults by remember { mutableStateOf(emptyList<SchoolLookupDto>()) }
    var selectedSchool by remember { mutableStateOf<SchoolLookupDto?>(null) }
    var isSearching by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf<String?>(null) }
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
        submitEnabled = selectedSchool != null,
        onBack = onCancel,
        onSubmit = { selectedSchool?.let { onSubmit(it, level, dob) } }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SchoolLookupSection(
                title = "1. Find your school",
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
            Text(
                text = "Selecting a real school record ensures the request goes to the correct school admins.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
