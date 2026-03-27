package com.schoolbridge.v2.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import com.schoolbridge.v2.data.dto.user.StudentLookupDto
import com.schoolbridge.v2.domain.user.RelationshipType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentRoleRequestScreen(
    alreadyHasRole: Boolean = false,
    linkedStudentNames: List<String> = emptyList(),
    onSearchStudents: suspend (String, Long?) -> List<StudentLookupDto>,
    onSubmit: suspend (student: StudentLookupDto, relationship: RelationshipType) -> Unit,
    onCancel: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var studentQuery by remember { mutableStateOf("") }
    var studentLookupMode by remember { mutableStateOf(LookupMode.ID) }
    var studentResults by remember { mutableStateOf(emptyList<StudentLookupDto>()) }
    var selectedStudent by remember { mutableStateOf<StudentLookupDto?>(null) }
    var isSearching by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf<String?>(null) }
    var selectedRelationship by remember { mutableStateOf(RelationshipType.FATHER) }
    var isSubmitting by remember { mutableStateOf(false) }
    var submitError by remember { mutableStateOf<String?>(null) }
    RoleRequestFormScaffold(
        title = if (alreadyHasRole) "Add Another Parent Link" else "Request Parent Access",
        subtitle = if (alreadyHasRole) {
            "You can already use parent tools. Add another child or relationship here so the school can review and attach the new profile."
        } else {
            "Ask the school to link you to a child profile so you can follow attendance, fees, messages, and academic updates."
        },
        actionLabel = if (alreadyHasRole) "Request New Child Link" else "Send Request",
        submitEnabled = selectedStudent != null,
        isSubmitting = isSubmitting,
        submitErrorMessage = submitError,
        onBack = onCancel,
        onSubmit = {
            val student = selectedStudent ?: return@RoleRequestFormScaffold
            scope.launch {
                isSubmitting = true
                submitError = null
                runCatching { onSubmit(student, selectedRelationship) }
                    .onFailure { submitError = it.message ?: "Could not submit your request" }
                isSubmitting = false
            }
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (alreadyHasRole && linkedStudentNames.isNotEmpty()) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Linked children",
                            style = MaterialTheme.typography.titleSmall
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            linkedStudentNames.forEach { name ->
                                AssistChip(
                                    onClick = {},
                                    label = { Text(name) }
                                )
                            }
                        }
                    }
                }
            }

            StudentLookupSection(
                title = "1. Find the student",
                query = studentQuery,
                mode = studentLookupMode,
                isSearching = isSearching,
                selectedStudent = selectedStudent,
                results = studentResults,
                errorMessage = searchError,
                helperText = "Search by student ID when you know it, or by name when you do not.",
                onQueryChange = {
                    studentQuery = it
                    searchError = null
                },
                onModeSelected = { studentLookupMode = it },
                onSearch = {
                    scope.launch {
                        isSearching = true
                        searchError = null
                        runCatching { onSearchStudents(studentQuery, null) }
                            .onSuccess { studentResults = it }
                            .onFailure { searchError = it.message ?: "Student search failed" }
                        isSearching = false
                    }
                },
                onSelect = { selectedStudent = it }
            )

            Text(
                text = "2. Confirm relationship",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = selectedRelationship.label,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Relationship") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    RelationshipType.entries.forEach {
                        DropdownMenuItem(
                            text = { Text(it.label) },
                            onClick = {
                                selectedRelationship = it
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
