package com.schoolbridge.v2.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.domain.user.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestRoleScreen(
    activeRoles: Set<UserRole>,
    onRoleSelected: (UserRole) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Request a New Role") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)      // ← first padding from Scaffold
                .padding(16.dp),            // ← extra 16 dp all around
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(UserRole.entries.size) { idx ->
                val role = UserRole.entries[idx]
                val alreadyHas = role in activeRoles

                /** clickable‑version of ElevatedCard gives you `enabled` **/
                ElevatedCard(
                    onClick = { onRoleSelected(role) },
                    enabled = !alreadyHas,        // disabled if user already has the role
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = {
                            Text(
                                text = role.humanLabel,
                                style = MaterialTheme.typography.titleMedium
                            )
                        },
                        supportingContent = {
                            Text(
                                when (role) {
                                    UserRole.PARENT -> "Monitor your children’s education, finances, attendance, and messages."
                                    UserRole.STUDENT -> "Access your own courses, schedule, assignments, and performance reports."
                                    UserRole.TEACHER -> "Manage your classes, track attendance, share materials, and message students or parents."
                                    UserRole.SCHOOL_ADMIN -> "Oversee staff, academic and financial records, and manage the school system."
                                    else -> ""
                                }
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = when (role) {
                                    UserRole.PARENT        -> Icons.Default.FamilyRestroom
                                    UserRole.STUDENT       -> Icons.Default.School
                                    UserRole.TEACHER       -> Icons.Default.MenuBook
                                    UserRole.SCHOOL_ADMIN  -> Icons.Default.AdminPanelSettings
                                    else                   -> Icons.Default.Person
                                },
                                contentDescription = null
                            )
                        },
                        trailingContent = {
                            if (alreadyHas) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Already assigned",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

