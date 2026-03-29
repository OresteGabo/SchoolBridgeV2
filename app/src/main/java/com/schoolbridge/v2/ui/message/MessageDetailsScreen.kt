package com.schoolbridge.v2.ui.message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.schoolbridge.v2.domain.messaging.Message

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageDetailsScreen(
    message: Message,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onActionClick: (String) -> Unit,
    pendingActionId: String? = null
) {
    val scrollState = rememberScrollState()
    var isEditing by remember(message.id) { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Message",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ── Sender header ─────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                ) {
                    Icon(
                        Icons.Default.School,
                        contentDescription = null,
                        tint     = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        text  = message.sender,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text  = buildString {
                            append(message.timestamp)
                            if (message.isEdited) append(" • Edited")
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // ── Message title + body ──────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                message.title?.let {
                    Text(
                        text  = it,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color      = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    // Accent line under title
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.primary)

                    )
                }

                Text(
                    text  = message.content,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize    = 17.sp,
                        lineHeight  = 26.sp,
                        color       = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            // ── Status (if already responded) ─────────────────────
            if (message.status != null && !isEditing) {
                Card(
                    shape  = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Row(
                        modifier              = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = null,
                            tint     = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.size(22.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Response recorded",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color      = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            )
                            Text(
                                message.status,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            )
                        }
                        TextButton(onClick = { isEditing = true }) {
                            Text(
                                "Change",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color      = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            )
                        }
                    }
                }
            }

            // ── Actions ───────────────────────────────────────────
            if (message.actions.isNotEmpty() && (message.status == null || isEditing)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text  = if (isEditing) "Update your response" else "Required action",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color      = if (isEditing) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    )

                    message.actions.forEachIndexed { index, action ->
                        val isPending = pendingActionId == action.actionId
                        val actionsLocked = pendingActionId != null
                        if (index == 0) {
                            Button(
                                onClick   = { isEditing = false; onActionClick(action.actionId) },
                                enabled   = !actionsLocked,
                                modifier  = Modifier.fillMaxWidth().height(52.dp),
                                shape     = RoundedCornerShape(12.dp)
                            ) {
                                if (isPending) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                }
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    if (isPending) "Sending..." else action.label,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        } else {
                            OutlinedButton(
                                onClick  = { isEditing = false; onActionClick(action.actionId) },
                                enabled = !actionsLocked,
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                shape    = RoundedCornerShape(12.dp)
                            ) {
                                if (isPending) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(Modifier.width(8.dp))
                                }
                                Text(
                                    if (isPending) "Sending..." else action.label,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    if (isEditing) {
                        TextButton(
                            onClick  = { isEditing = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Cancel",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
