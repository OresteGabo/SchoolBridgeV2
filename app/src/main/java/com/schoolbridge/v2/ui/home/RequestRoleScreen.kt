package com.schoolbridge.v2.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.domain.user.UserRole
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestRoleScreen(
    activeRoles: Set<UserRole>,
    linkedStudentNames: List<String> = emptyList(),
    pendingRoles: Set<UserRole> = emptySet(),
    onRoleSelected: (UserRole) -> Unit,
    onBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val pendingRoleState = remember { mutableStateListOf<UserRole>() }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val requestableRoles = UserRole.entries
        .filterNot { it == UserRole.GUEST }
        .map { role ->
            role.requestCardContent(
                alreadyHasRole = role in activeRoles,
                linkedStudentNames = linkedStudentNames
            )
        }
    val activeRoleCards = activeRoles
        .filterNot { it == UserRole.GUEST }
        .map { it.managementCardContent() }

    LaunchedEffect(pendingRoles) {
        pendingRoleState.clear()
        pendingRoleState.addAll(pendingRoles.filterNot { it == UserRole.GUEST })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Roles & Access") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Grow or manage your access",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Request a new role, add another child or assignment inside an existing role, or review the access you already hold.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.84f)
                        )
                    }
                }
            }

            item {
                TabRow(selectedTabIndex = selectedTabIndex) {
                    listOf("Request Access", "Manage Roles").forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) }
                        )
                    }
                }
            }

            when (selectedTabIndex) {
                0 -> {
                    items(requestableRoles) { card ->
                        ElevatedCard(
                            onClick = { onRoleSelected(card.role) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(26.dp),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = card.containerColor
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .background(card.iconContainerColor),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = card.icon,
                                            contentDescription = null,
                                            tint = card.iconTint
                                        )
                                    }
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = card.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = card.subtitle,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                AssistChip(
                                    onClick = { onRoleSelected(card.role) },
                                    label = { Text(card.chipLabel) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (card.alreadyHasRole) Icons.Default.AddLink else card.icon,
                                            contentDescription = null
                                        )
                                    }
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "What this unlocks",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = card.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                else -> {
                    item {
                        Text(
                            text = "Validated roles",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (activeRoleCards.isEmpty()) {
                        item {
                            RoleHintCard(
                                title = "No active roles yet",
                                body = "Once a school approves a role for you, it will appear here with its status and management options."
                            )
                        }
                    } else {
                        items(activeRoleCards) { card ->
                            RoleManagementCard(
                                card = card,
                                snackbarHostState = snackbarHostState
                            )
                        }
                    }

                    item {
                        Text(
                            text = "Pending requests",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (pendingRoleState.isEmpty()) {
                        item {
                            EmptyRoleState(
                                title = "No pending requests",
                                body = "Requests waiting for review will appear here."
                            )
                        }
                    } else {
                        items(pendingRoleState, key = { it.name }) { role ->
                            PendingRoleCard(
                                role = role,
                                onCancel = {
                                    pendingRoleState.remove(role)
                                },
                                snackbarHostState = snackbarHostState
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyRoleState(
    title: String,
    body: String
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(
                    imageVector = Icons.Default.HourglassEmpty,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    modifier = Modifier.size(28.dp)
                )
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    modifier = Modifier.size(28.dp)
                )
                Icon(
                    imageVector = Icons.Default.AddLink,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    modifier = Modifier.size(28.dp)
                )
            }
            Icon(
                imageVector = Icons.Default.HourglassEmpty,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(34.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun RoleHintCard(
    title: String,
    body: String
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RoleManagementCard(
    card: RoleManagementCardContent,
    snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(card.iconContainerColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = card.icon,
                        contentDescription = null,
                        tint = card.iconTint
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = card.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = card.statusLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = card.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar(card.primaryActionMessage)
                        }
                    }
                ) {
                    Text(card.primaryActionLabel)
                }
                TextButton(
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar(card.secondaryActionMessage)
                        }
                    }
                ) {
                    Text(card.secondaryActionLabel)
                }
            }
        }
    }
}

@Composable
private fun PendingRoleCard(
    role: UserRole,
    onCancel: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()
    val card = role.pendingCardContent()

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.HourglassEmpty,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = card.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Pending review",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = card.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Request details will open here once the request center is wired to backend data.")
                        }
                    }
                ) {
                    Text("View details")
                }
                TextButton(
                    onClick = {
                        onCancel()
                        scope.launch {
                            snackbarHostState.showSnackbar("${role.humanLabel} request removed from this local list.")
                        }
                    }
                ) {
                    Text("Cancel request")
                }
            }
        }
    }
}

private data class RoleRequestCardContent(
    val role: UserRole,
    val title: String,
    val subtitle: String,
    val description: String,
    val chipLabel: String,
    val icon: ImageVector,
    val alreadyHasRole: Boolean,
    val containerColor: Color,
    val iconContainerColor: Color,
    val iconTint: Color
)

private data class RoleManagementCardContent(
    val title: String,
    val body: String,
    val statusLabel: String,
    val icon: ImageVector,
    val iconContainerColor: Color,
    val iconTint: Color,
    val primaryActionLabel: String,
    val primaryActionMessage: String,
    val secondaryActionLabel: String,
    val secondaryActionMessage: String
)

@Composable
private fun UserRole.requestCardContent(
    alreadyHasRole: Boolean,
    linkedStudentNames: List<String>
): RoleRequestCardContent {
    val colorScheme = MaterialTheme.colorScheme
    return when (this) {
        UserRole.PARENT -> RoleRequestCardContent(
            role = this,
            title = if (alreadyHasRole) "Parent access" else "Become a parent",
            subtitle = when {
                alreadyHasRole && linkedStudentNames.isNotEmpty() -> "${linkedStudentNames.size} linked ${if (linkedStudentNames.size == 1) "child" else "children"}"
                alreadyHasRole -> "Add another child or caregiver relationship"
                else -> "Follow your child's school life in one place"
            },
            description = if (alreadyHasRole && linkedStudentNames.isNotEmpty()) {
                "Currently linked: ${linkedStudentNames.take(3).joinToString(", ")}${if (linkedStudentNames.size > 3) " and ${linkedStudentNames.size - 3} more" else ""}."
            } else {
                "See attendance, grades, messages, finance updates, and family-linked actions for each child profile you are approved to manage."
            },
            chipLabel = if (alreadyHasRole) "View linked kids & request more" else "Request parent access",
            icon = Icons.Default.FamilyRestroom,
            alreadyHasRole = alreadyHasRole,
            containerColor = colorScheme.surface,
            iconContainerColor = colorScheme.primaryContainer,
            iconTint = colorScheme.onPrimaryContainer
        )

        UserRole.STUDENT -> RoleRequestCardContent(
            role = this,
            title = if (alreadyHasRole) "Student access" else "Become a student",
            subtitle = if (alreadyHasRole) "Link another student identity or school record" else "Access your own learning and school updates",
            description = "Open schedules, results, assignments, notices, and payments connected to the student profile that the school verifies for you.",
            chipLabel = if (alreadyHasRole) "Link another student profile" else "Request student access",
            icon = Icons.Default.School,
            alreadyHasRole = alreadyHasRole,
            containerColor = colorScheme.surface,
            iconContainerColor = colorScheme.tertiaryContainer,
            iconTint = colorScheme.onTertiaryContainer
        )

        UserRole.TEACHER -> RoleRequestCardContent(
            role = this,
            title = if (alreadyHasRole) "Teacher access" else "Become a teacher",
            subtitle = if (alreadyHasRole) "Request another teaching assignment or school" else "Manage classes, attendance, and parent communication",
            description = "Use teaching tools for the classes or campuses that approve you, including registers, subject materials, and academic follow-up.",
            chipLabel = if (alreadyHasRole) "Request another teaching post" else "Request teacher access",
            icon = Icons.Default.MenuBook,
            alreadyHasRole = alreadyHasRole,
            containerColor = colorScheme.surface,
            iconContainerColor = colorScheme.secondaryContainer,
            iconTint = colorScheme.onSecondaryContainer
        )

        UserRole.SCHOOL_ADMIN -> RoleRequestCardContent(
            role = this,
            title = if (alreadyHasRole) "School admin access" else "Become a school admin",
            subtitle = if (alreadyHasRole) "Request access for another school, campus, or responsibility" else "Oversee operations, approvals, and school records",
            description = "Handle administrative workflows like approvals, operations, academic oversight, and institution-wide communication where access is granted.",
            chipLabel = if (alreadyHasRole) "Request more admin scope" else "Request admin access",
            icon = Icons.Default.AdminPanelSettings,
            alreadyHasRole = alreadyHasRole,
            containerColor = colorScheme.surface,
            iconContainerColor = colorScheme.errorContainer,
            iconTint = colorScheme.onErrorContainer
        )

        UserRole.GUEST -> RoleRequestCardContent(
            role = this,
            title = humanLabel,
            subtitle = "",
            description = "",
            chipLabel = "Continue",
            icon = Icons.Default.Person,
            alreadyHasRole = alreadyHasRole,
            containerColor = colorScheme.surface,
            iconContainerColor = colorScheme.surfaceVariant,
            iconTint = colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun UserRole.managementCardContent(): RoleManagementCardContent {
    val colorScheme = MaterialTheme.colorScheme
    return when (this) {
        UserRole.PARENT -> RoleManagementCardContent(
            title = "Parent role",
            body = "Validated and ready to use. If you care for more than one child, you can request extra links without affecting the access you already have.",
            statusLabel = "Validated",
            icon = Icons.Default.FamilyRestroom,
            iconContainerColor = colorScheme.primaryContainer,
            iconTint = colorScheme.onPrimaryContainer,
            primaryActionLabel = "View linked access",
            primaryActionMessage = "Child-link management can connect here once request records are stored from backend.",
            secondaryActionLabel = "Request another link",
            secondaryActionMessage = "Use the Request Access tab to add another child or caregiver relationship."
        )

        UserRole.STUDENT -> RoleManagementCardContent(
            title = "Student role",
            body = "Your student identity is active. If you need another campus or verified student record, you can send a fresh request instead of replacing this one.",
            statusLabel = "Validated",
            icon = Icons.Default.School,
            iconContainerColor = colorScheme.tertiaryContainer,
            iconTint = colorScheme.onTertiaryContainer,
            primaryActionLabel = "Review profile link",
            primaryActionMessage = "Student profile management will appear here once the role center is backed by request history.",
            secondaryActionLabel = "Request another profile",
            secondaryActionMessage = "Use the Request Access tab to add another student identity."
        )

        UserRole.TEACHER -> RoleManagementCardContent(
            title = "Teacher role",
            body = "You are visible as an active teacher. If you step away for a while, a future availability flag can help schools know when to call or use WhatsApp instead.",
            statusLabel = "Validated",
            icon = Icons.Default.MenuBook,
            iconContainerColor = colorScheme.secondaryContainer,
            iconTint = colorScheme.onSecondaryContainer,
            primaryActionLabel = "Pause in app",
            primaryActionMessage = "Temporary availability can hook into admin messaging once the backend supports role presence.",
            secondaryActionLabel = "Request another post",
            secondaryActionMessage = "Use the Request Access tab to add another teaching assignment."
        )

        UserRole.SCHOOL_ADMIN -> RoleManagementCardContent(
            title = "School admin role",
            body = "Your admin access is validated. A future availability setting can signal when you are temporarily away from the platform so staff know to use another channel.",
            statusLabel = "Validated",
            icon = Icons.Default.AdminPanelSettings,
            iconContainerColor = colorScheme.errorContainer,
            iconTint = colorScheme.onErrorContainer,
            primaryActionLabel = "Manage availability",
            primaryActionMessage = "Admin availability status will connect here when backend support is added.",
            secondaryActionLabel = "Request more scope",
            secondaryActionMessage = "Use the Request Access tab to request another campus or admin scope."
        )

        UserRole.GUEST -> RoleManagementCardContent(
            title = "Guest role",
            body = "",
            statusLabel = "",
            icon = Icons.Default.Person,
            iconContainerColor = colorScheme.surfaceVariant,
            iconTint = colorScheme.onSurfaceVariant,
            primaryActionLabel = "",
            primaryActionMessage = "",
            secondaryActionLabel = "",
            secondaryActionMessage = ""
        )
    }
}

private data class PendingRoleCardContent(
    val title: String,
    val body: String
)

private fun UserRole.pendingCardContent(): PendingRoleCardContent =
    when (this) {
        UserRole.PARENT -> PendingRoleCardContent(
            title = "Parent link request",
            body = "Waiting for the school to verify the caregiver relationship and attach the requested child profile."
        )

        UserRole.STUDENT -> PendingRoleCardContent(
            title = "Student profile request",
            body = "Waiting for identity confirmation and school-side verification before the additional student record becomes active."
        )

        UserRole.TEACHER -> PendingRoleCardContent(
            title = "Teaching assignment request",
            body = "Waiting for school approval so the new classes, campus, or subject assignment can be added to your account."
        )

        UserRole.SCHOOL_ADMIN -> PendingRoleCardContent(
            title = "Admin scope request",
            body = "Waiting for administrative review before the requested school or responsibility can be granted."
        )

        UserRole.GUEST -> PendingRoleCardContent(
            title = "Request",
            body = ""
        )
    }
