package com.schoolbridge.v2.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.data.dto.user.SchoolLookupDto
import com.schoolbridge.v2.data.dto.user.StudentLookupDto

enum class LookupMode(val label: String, val fieldLabel: String) {
    ID("Search by ID", "Enter exact ID"),
    NAME("Search by name", "Enter name")
}

@Composable
fun SchoolLookupSection(
    title: String,
    query: String,
    mode: LookupMode,
    isSearching: Boolean,
    selectedSchool: SchoolLookupDto?,
    results: List<SchoolLookupDto>,
    errorMessage: String?,
    onQueryChange: (String) -> Unit,
    onModeSelected: (LookupMode) -> Unit,
    onSearch: () -> Unit,
    onSelect: (SchoolLookupDto) -> Unit
) {
    LookupShell(
        title = title,
        query = query,
        mode = mode,
        isSearching = isSearching,
        errorMessage = errorMessage,
        onQueryChange = onQueryChange,
        onModeSelected = onModeSelected,
        onSearch = onSearch
    ) {
        selectedSchool?.let {
            SelectedLookupCard(
                title = it.name,
                meta = "ID ${it.id}",
                supporting = listOfNotNull(it.abbrevName, it.sectorName).joinToString(" • ")
            )
        }

        results.forEach { school ->
            LookupResultCard(
                title = school.name,
                meta = "ID ${school.id}",
                supporting = listOfNotNull(school.abbrevName, school.sectorName).joinToString(" • "),
                actionLabel = if (selectedSchool?.id == school.id) "Selected" else "Select",
                enabled = selectedSchool?.id != school.id,
                onClick = { onSelect(school) }
            )
        }
    }
}

@Composable
fun StudentLookupSection(
    title: String,
    query: String,
    mode: LookupMode,
    isSearching: Boolean,
    selectedStudent: StudentLookupDto?,
    results: List<StudentLookupDto>,
    errorMessage: String?,
    helperText: String? = null,
    onQueryChange: (String) -> Unit,
    onModeSelected: (LookupMode) -> Unit,
    onSearch: () -> Unit,
    onSelect: (StudentLookupDto) -> Unit
) {
    LookupShell(
        title = title,
        query = query,
        mode = mode,
        isSearching = isSearching,
        errorMessage = errorMessage,
        helperText = helperText,
        onQueryChange = onQueryChange,
        onModeSelected = onModeSelected,
        onSearch = onSearch
    ) {
        selectedStudent?.let {
            SelectedLookupCard(
                title = it.fullName,
                meta = "Student ID ${it.studentUserId}",
                supporting = listOf(it.schoolName, it.academicLevel, it.combinationName)
                    .filterNotNull()
                    .joinToString(" • ")
            )
        }

        results.forEach { student ->
            LookupResultCard(
                title = student.fullName,
                meta = "Student ID ${student.studentUserId}",
                supporting = listOf(student.schoolName, student.academicLevel, student.combinationName)
                    .filterNotNull()
                    .joinToString(" • "),
                actionLabel = if (selectedStudent?.studentUserId == student.studentUserId) "Selected" else "Select",
                enabled = selectedStudent?.studentUserId != student.studentUserId,
                onClick = { onSelect(student) }
            )
        }
    }
}

@Composable
private fun LookupShell(
    title: String,
    query: String,
    mode: LookupMode,
    isSearching: Boolean,
    errorMessage: String?,
    helperText: String? = null,
    onQueryChange: (String) -> Unit,
    onModeSelected: (LookupMode) -> Unit,
    onSearch: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LookupMode.entries.forEach { lookupMode ->
                    AssistChip(
                        onClick = { onModeSelected(lookupMode) },
                        label = { Text(lookupMode.label) }
                    )
                }
            }

            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                label = { Text(mode.fieldLabel) },
                modifier = Modifier.fillMaxWidth()
            )

            helperText?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row {
                Button(onClick = onSearch, enabled = query.isNotBlank() && !isSearching) {
                    Text(if (isSearching) "Searching..." else "Search")
                }
            }

            errorMessage?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            content()
        }
    }
}

@Composable
private fun SelectedLookupCard(
    title: String,
    meta: String,
    supporting: String
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(meta, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            if (supporting.isNotBlank()) {
                Text(supporting, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun LookupResultCard(
    title: String,
    meta: String,
    supporting: String,
    actionLabel: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(meta, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            if (supporting.isNotBlank()) {
                Text(supporting, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row {
                Button(onClick = onClick, enabled = enabled) {
                    Text(actionLabel)
                }
            }
        }
    }
}
