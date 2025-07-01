package com.schoolbridge.v2.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.domain.user.RelationshipType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentRoleRequestScreen(
    onSubmit: (school: String, childName: String, dob: String, relationship: RelationshipType) -> Unit,
    onCancel: () -> Unit
) {
    var selectedSchool by remember { mutableStateOf("") }
    var childName by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var selectedRelationship by remember { mutableStateOf(RelationshipType.FATHER) }

    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Request Parent Access", style = MaterialTheme.typography.titleLarge)

        // TODO: Replace with actual school picker
        OutlinedTextField(value = selectedSchool, onValueChange = { selectedSchool = it }, label = { Text("School Name") })
        OutlinedTextField(value = childName, onValueChange = { childName = it }, label = { Text("Child’s Full Name") })
        OutlinedTextField(value = dob, onValueChange = { dob = it }, label = { Text("Child’s Date of Birth") })

        // Relationship dropdown
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = selectedRelationship.label,
                onValueChange = {},
                readOnly = true,
                label = { Text("Relationship") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor()
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

        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            onSubmit(selectedSchool, childName, dob, selectedRelationship)
        }) {
            Text("Send Request")
        }
    }
}
