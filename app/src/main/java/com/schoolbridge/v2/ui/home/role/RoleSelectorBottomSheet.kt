package com.schoolbridge.v2.ui.home.role

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.domain.user.UserRole
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut

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
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val tabs = listOf("Active Roles", "Pending Requests")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val hasNoRealRoles = availableRoles.isEmpty() ||
            availableRoles.all { it == UserRole.GUEST }

    val coroutineScope = rememberCoroutineScope()
    var showCheckmark by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(Modifier.height(8.dp))
                Text("Choose Your Role", style = MaterialTheme.typography.titleMedium)
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
                    tabs.forEachIndexed { i, title ->
                        Tab(
                            selected = selectedTabIndex == i,
                            onClick = { selectedTabIndex = i },
                            text = { Text(title) }
                        )
                    }
                }

                when (selectedTabIndex) {
                    0 -> LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                    ) {
                        if (hasNoRealRoles) {
                            item {
                                EmptyStateMessage(
                                    icon = Icons.Default.PersonOff,
                                    title = "No Active Roles",
                                    message = "You currently have no active roles. Request one to unlock the full app experience.",
                                    buttonText = "Request a Role",
                                    onButtonClick = onRequestNewRole
                                )
                            }
                        } else {
                            items(availableRoles.toList()) { role ->
                                val isSelected = role == currentRole

                                ListItem(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(enabled = !isSelected) {
                                            coroutineScope.launch {
                                                showCheckmark = true
                                                delay(800)

                                                // Only after showing the checkmark do we perform the side effects:
                                                onRoleSelected(role)
                                                showCheckmark = false
                                                onDismiss()
                                            }
                                        }

                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .then(
                                            if (isSelected) Modifier
                                                .background(
                                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .border(
                                                    width = 2.dp,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                            else Modifier
                                        ),
                                    headlineContent = {
                                        Text(
                                            text = role.humanLabel,
                                            style = if (isSelected) MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                            else MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    supportingContent = {
                                        Text(
                                            text = role.description,
                                            maxLines = 2,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    trailingContent = {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Selected role",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }

                    1 -> LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                    ) {
                        if (pendingRoles.isEmpty()) {
                            item {
                                EmptyStateMessage(
                                    icon = Icons.Default.HourglassEmpty,
                                    title = "No Pending Requests",
                                    message = "Once you request a role, it will show up here while waiting for approval."
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
                                            "Request pending approval…",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    },
                                    leadingContent = {
                                        Icon(
                                            Icons.Default.HourglassEmpty,
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

                OutlinedButton(
                    onClick = onRequestNewRole,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Text("Request a New Role")
                }
            }

            // ✅ Checkmark confirmation overlay
            if (showCheckmark) {
                Box(
                    modifier = Modifier
                        .matchParentSize() // fill only the bottom sheet bounds
                        .padding(24.dp),   // same horizontal padding as your Column for consistent layout
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = showCheckmark,
                        enter = fadeIn() + scaleIn(initialScale = 0.5f),
                        exit = fadeOut() + scaleOut(targetScale = 0.5f),
                        modifier = Modifier
                            .matchParentSize()
                            .padding(24.dp),
                        //contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Confirmed",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(72.dp)
                        )
                    }

                }
            }

        }
    }
}


@Composable
fun EmptyStateMessage(
    icon: ImageVector = Icons.Default.Info,
    title: String,
    message: String,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (buttonText != null && onButtonClick != null) {
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = onButtonClick,
                modifier = Modifier.fillMaxWidth(0.75f)
            ) { Text(buttonText) }
        }
    }
}
