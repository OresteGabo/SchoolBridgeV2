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
fun TeacherRoleRequestScreen(
    alreadyHasRole: Boolean = false,
    onSearchSchools: suspend (String) -> List<SchoolLookupDto>,
    onSubmit: (school: SchoolLookupDto, message: String) -> Unit,
    onCancel: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var schoolQuery by remember { mutableStateOf("") }
    var schoolLookupMode by remember { mutableStateOf(LookupMode.ID) }
    var schoolResults by remember { mutableStateOf(emptyList<SchoolLookupDto>()) }
    var selectedSchool by remember { mutableStateOf<SchoolLookupDto?>(null) }
    var isSearching by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf<String?>(null) }
    var description by remember { mutableStateOf("") }

    RoleRequestFormScaffold(
        title = if (alreadyHasRole) "Request Another Teaching Assignment" else "Request Teacher Access",
        subtitle = if (alreadyHasRole) {
            "Use this to ask for another class load, campus, or school assignment without losing your current teacher access."
        } else {
            "Tell the school where and what you teach so your staff access can be reviewed and approved."
        },
        actionLabel = if (alreadyHasRole) "Request Assignment" else "Send Request",
        submitEnabled = selectedSchool != null,
        onBack = onCancel,
        onSubmit = { selectedSchool?.let { onSubmit(it, description) } }
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
                value = description,
                onValueChange = { description = it },
                label = { Text("Courses & Role Description") },
                placeholder = { Text("E.g., I teach Physics and Math to Senior 4 and 5") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 5,
                maxLines = 8
            )
            Text(
                text = "Pick a real school first, then describe the teaching responsibility you want approved there.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
