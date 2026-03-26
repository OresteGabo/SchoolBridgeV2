package com.schoolbridge.v2.ui.message

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.schoolbridge.v2.domain.messaging.*

// ─────────────────────────────────────────────────────────────
// Root navigation shell
// ─────────────────────────────────────────────────────────────

@Composable
fun MessageThreadScreen(
    initialThreadId: String? = null,
    onThreadSelected: ((String) -> Unit)? = null,
    onBack: (() -> Unit)? = null
) {
    val viewModel: MessageThreadViewModel = viewModel()
    val messageThreads by viewModel.messageThreads.collectAsState()

    var internalSelectedThreadId by remember { mutableStateOf<String?>(null) }
    var viewingMessage by remember { mutableStateOf<Message?>(null) }

    val effectiveThreadId = initialThreadId ?: internalSelectedThreadId
    val selectedThread = messageThreads.find { it.id == effectiveThreadId }

    Box(modifier = Modifier.fillMaxSize()) {
        ImigongoBackground()

        AnimatedContent(targetState = viewingMessage != null, label = "viewingMessage") { isViewing ->
            if (isViewing && viewingMessage != null) {
                MessageDetailsScreen(
                    message = viewingMessage!!,
                    onBack = { viewingMessage = null },
                    onDelete = { viewingMessage = null },
                    onActionClick = { actionId ->
                        selectedThread?.let { thread ->
                            viewModel.performAction(thread.id, viewingMessage!!.id, actionId)
                            val actionLabel = viewingMessage!!
                                .actions.find { it.actionId == actionId }?.label ?: "Responded"
                            viewModel.addUserMessage(threadId = thread.id, content = actionLabel)
                            viewingMessage = viewingMessage!!.copy(status = "Confirmed")
                        }
                    }
                )
            } else {
                AnimatedContent(targetState = selectedThread, label = "selectedThread") { thread ->
                    if (thread == null) {
                        ThreadListScreen(messageThreads) {
                            internalSelectedThreadId = it.id
                            viewModel.markAsRead(it.id)
                        }
                    } else {
                        ThreadDetailScreen(
                            thread = thread,
                            onBack = { internalSelectedThreadId = null },
                            onMessageClick = { viewingMessage = it },
                            onActionClick = { msgId, actionId ->
                                viewModel.performAction(thread.id, msgId, actionId)
                                val message = thread.messages.find { it.id == msgId }
                                val actionLabel = message
                                    ?.actions?.find { it.actionId == actionId }?.label ?: "Responded"
                                viewModel.addUserMessage(threadId = thread.id, content = actionLabel)
                            }
                        )
                    }
                }
            }
        }
    }

    BackHandler(enabled = viewingMessage != null || internalSelectedThreadId != null) {
        if (viewingMessage != null) viewingMessage = null
        else internalSelectedThreadId = null
    }
}

// ─────────────────────────────────────────────────────────────
// Subtle dot-grid background
// ─────────────────────────────────────────────────────────────

@Composable
fun ImigongoBackground() {
    val color = MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)
    Canvas(modifier = Modifier.fillMaxSize()) {
        val step = 60f
        for (x in 0..size.width.toInt() step step.toInt()) {
            for (y in 0..size.height.toInt() step step.toInt()) {
                drawCircle(color, 4f, Offset(x.toFloat(), y.toFloat()))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Thread list screen
// ─────────────────────────────────────────────────────────────

@Composable
fun ThreadListScreen(
    threads: List<MessageThread>,
    onThreadClick: (MessageThread) -> Unit
) {
    if (threads.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📭", fontSize = 48.sp)
                Spacer(Modifier.height(12.dp))
                Text(
                    "No messages yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(threads, key = { it.id }) { thread ->
            ThreadCard(thread = thread, onClick = { onThreadClick(thread) })
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Thread card
// ─────────────────────────────────────────────────────────────

@Composable
private fun ThreadCard(thread: MessageThread, onClick: () -> Unit) {
    val isUnread    = thread.getUnreadCount() > 0
    val latestMsg   = thread.getLatestMessage()
    val modeColor   = threadModeContainerColor(thread.mode)
    val modeOnColor = threadModeOnContainerColor(thread.mode)

    Surface(
        onClick         = onClick,
        shape           = RoundedCornerShape(16.dp),
        color           = MaterialTheme.colorScheme.surface,
        tonalElevation  = if (isUnread) 2.dp else 0.dp,
        modifier        = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier              = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment     = Alignment.Top
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(modeColor)
            ) {
                Text(threadModeEmoji(thread.mode), fontSize = 20.sp)
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text     = thread.participantsLabel,
                        style    = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isUnread) FontWeight.SemiBold else FontWeight.Normal,
                            color      = MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    latestMsg?.timestamp?.let { ts ->
                        Text(
                            text  = ts,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Text(
                    text     = thread.topic,
                    style    = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color      = MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                latestMsg?.content?.let {
                    Text(
                        text     = it,
                        style    = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier              = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Surface(shape = RoundedCornerShape(50), color = modeColor) {
                        Text(
                            text     = threadModeLabel(thread.mode).uppercase(),
                            style    = MaterialTheme.typography.labelSmall.copy(
                                color         = modeOnColor,
                                fontWeight    = FontWeight.Bold,
                                fontSize      = 10.sp,
                                letterSpacing = 0.5.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    if (thread.getUnreadCount() > 0) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .defaultMinSize(minWidth = 20.dp, minHeight = 20.dp)
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 5.dp)
                        ) {
                            Text(
                                text  = thread.getUnreadCount().toString(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color      = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize   = 10.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Thread detail screen
// ─────────────────────────────────────────────────────────────

@Composable
fun ThreadDetailScreen(
    thread: MessageThread,
    onBack: () -> Unit,
    onMessageClick: (Message) -> Unit,
    onActionClick: (String, String) -> Unit
) {
    val listState = rememberLazyListState()

    Column(modifier = Modifier.fillMaxSize()) {

        Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 2.dp) {
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(threadModeContainerColor(thread.mode))
                ) {
                    Text(threadModeEmoji(thread.mode), fontSize = 18.sp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text     = thread.participantsLabel,
                        style    = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text  = thread.topic,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)

        LazyColumn(
            state               = listState,
            contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier            = Modifier.fillMaxSize()
        ) {
            items(thread.messages, key = { it.id }) { message ->
                MessageCard(
                    message       = message,
                    onClick       = { onMessageClick(message) },
                    onActionClick = { onActionClick(message.id, it) }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Message card
// ─────────────────────────────────────────────────────────────

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun MessageCard(
    message: Message,
    onClick: () -> Unit,
    onActionClick: (String) -> Unit
) {
    val haptic   = LocalHapticFeedback.current
    val isSystem = message.sender != "You"
    val primary  = MaterialTheme.colorScheme.primary

    Column(
        modifier            = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isSystem) Alignment.Start else Alignment.End
    ) {
        if (isSystem) {
            Text(
                text     = message.sender,
                style    = MaterialTheme.typography.labelSmall.copy(
                    color      = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )
        }

        Surface(
            shape = RoundedCornerShape(
                topStart    = if (isSystem) 4.dp else 16.dp,
                topEnd      = 16.dp,
                bottomStart = 16.dp,
                bottomEnd   = if (isSystem) 16.dp else 4.dp
            ),
            color    = if (isSystem) MaterialTheme.colorScheme.surfaceContainerHigh
            else MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .widthIn(max = 300.dp)
                .combinedClickable(
                    onClick     = onClick,
                    onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onClick()
                    }
                )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {

                message.title?.let {
                    Text(
                        text  = it,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color      = if (isSystem) primary
                            else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                    Spacer(Modifier.height(4.dp))
                }

                Text(
                    text  = message.content,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isSystem) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )

                if (message.status != null) {
                    Spacer(Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Row(
                            modifier              = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint     = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text  = message.status,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color      = MaterialTheme.colorScheme.onTertiaryContainer,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Text(
                                text  = "· tap to change",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.6f)
                                )
                            )
                        }
                    }
                }

                if (message.actions.isNotEmpty() && message.status == null) {
                    Spacer(Modifier.height(10.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        message.actions.forEachIndexed { index, action ->
                            if (index == 0) {
                                Button(
                                    onClick        = { onActionClick(action.actionId) },
                                    modifier       = Modifier.fillMaxWidth(),
                                    shape          = RoundedCornerShape(10.dp),
                                    colors         = ButtonDefaults.buttonColors(
                                        containerColor = primary,
                                        contentColor   = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    contentPadding = PaddingValues(vertical = 10.dp)
                                ) {
                                    Text(
                                        action.label,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            } else {
                                OutlinedButton(
                                    onClick        = { onActionClick(action.actionId) },
                                    modifier       = Modifier.fillMaxWidth(),
                                    shape          = RoundedCornerShape(10.dp),
                                    colors         = ButtonDefaults.outlinedButtonColors(
                                        contentColor = primary
                                    ),
                                    contentPadding = PaddingValues(vertical = 10.dp)
                                ) {
                                    Text(
                                        action.label,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(6.dp))
                Text(
                    text     = message.timestamp.split(", ").lastOrNull() ?: "",
                    style    = MaterialTheme.typography.labelSmall.copy(
                        color = if (isSystem) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.65f)
                    ),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
        Spacer(Modifier.height(2.dp))
    }
}

// ─────────────────────────────────────────────────────────────
// ThreadMode helpers
// ─────────────────────────────────────────────────────────────

@Composable
fun threadModeContainerColor(mode: ThreadMode) = when (mode) {
    ThreadMode.ANNOUNCEMENT    -> MaterialTheme.colorScheme.primaryContainer
    ThreadMode.ACTION_REQUIRED -> MaterialTheme.colorScheme.tertiaryContainer
    ThreadMode.CONVERSATION    -> MaterialTheme.colorScheme.secondaryContainer
    ThreadMode.DIRECT_CONTACT  -> MaterialTheme.colorScheme.secondaryContainer
}

@Composable
fun threadModeOnContainerColor(mode: ThreadMode) = when (mode) {
    ThreadMode.ANNOUNCEMENT    -> MaterialTheme.colorScheme.onPrimaryContainer
    ThreadMode.ACTION_REQUIRED -> MaterialTheme.colorScheme.onTertiaryContainer
    ThreadMode.CONVERSATION    -> MaterialTheme.colorScheme.onSecondaryContainer
    ThreadMode.DIRECT_CONTACT  -> MaterialTheme.colorScheme.onSecondaryContainer
}

fun threadModeEmoji(mode: ThreadMode) = when (mode) {
    ThreadMode.ANNOUNCEMENT    -> "📢"
    ThreadMode.ACTION_REQUIRED -> "⚡"
    ThreadMode.CONVERSATION    -> "💬"
    ThreadMode.DIRECT_CONTACT  -> "👤"
}

fun threadModeLabel(mode: ThreadMode) = when (mode) {
    ThreadMode.ANNOUNCEMENT    -> "Announcement"
    ThreadMode.ACTION_REQUIRED -> "Action needed"
    ThreadMode.CONVERSATION    -> "Conversation"
    ThreadMode.DIRECT_CONTACT  -> "Direct"
}
