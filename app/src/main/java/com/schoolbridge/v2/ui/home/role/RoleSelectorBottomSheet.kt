package com.schoolbridge.v2.ui.home.role

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.domain.user.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectorBottomSheet(
    currentRole: UserRole?,
    availableRoles: Set<UserRole>,
    pendingRoles: Set<UserRole> = emptySet(),
    onRoleSelected: (UserRole) -> Unit,
    onRequestNewRole: () -> Unit,
    onDismiss: () -> Unit
) {
    val tabs = listOf("Active Roles", "Pending Requests")
    var selectedTabIndex by remember { mutableStateOf(0) }

    // --- START OF CHANGE ---
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true // This ensures the sheet will always try to expand fully
    )
    // --- END OF CHANGE ---

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState, // Pass the sheetState here
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding() // Keep this to handle system bars
                .padding(horizontal = 24.dp)
        ) {
            val maxSheetHeight = maxHeight

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                // .heightIn(max = maxSheetHeight) // This line can often be removed now,
                // as the sheet will manage its own max height.
                // If you have specific minimum heights, heightIn(min = ...) might still be useful.
            ) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Choose Your Role",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Your current role determines which features and permissions are available in the app.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) }
                        )
                    }
                }

                when (selectedTabIndex) {
                    0 -> { // Active Roles
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f, fill = false) // fill = false is correct here for LazyColumn
                        ) {
                            if (availableRoles.isEmpty()) {
                                item {
                                    Text(
                                        "No active roles available.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(vertical = 16.dp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                items(availableRoles.toList()) { role ->
                                    val isSelected = role == currentRole
                                    ListItem(
                                        headlineContent = { Text(role.humanLabel) },
                                        supportingContent = {
                                            Text(
                                                role.description,
                                                maxLines = 2
                                            )
                                        },
                                        trailingContent = {
                                            if (isSelected) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(enabled = !isSelected) {
                                                onRoleSelected(role)
                                            }
                                            .padding(vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    1 -> { // Pending Roles
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f, fill = false) // fill = false is correct here for LazyColumn
                        ) {
                            if (pendingRoles.isEmpty()) {
                                item {
                                    Text(
                                        "No pending requests.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(vertical = 16.dp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                items(pendingRoles.toList()) { role ->
                                    ListItem(
                                        headlineContent = {
                                            Text(
                                                role.humanLabel,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            )
                                        },
                                        supportingContent = {
                                            Text(
                                                "Request pending approval...",
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        },
                                        leadingContent = {
                                            Icon(
                                                imageVector = Icons.Default.HourglassEmpty,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                OutlinedButton(
                    onClick = onRequestNewRole,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Text("Request a New Role")
                }
            }
        }
    }
}