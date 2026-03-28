package com.schoolbridge.v2.ui.message

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CallMissed
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.schoolbridge.v2.R
import com.schoolbridge.v2.data.remote.MessageApiServiceImpl
import com.schoolbridge.v2.data.remote.MessageRealtimeServiceImpl
import com.schoolbridge.v2.data.repository.implementations.MessagingRepositoryImpl
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.domain.messaging.*
import com.schoolbridge.v2.domain.user.CurrentUser
import com.schoolbridge.v2.domain.user.UserRole
import com.schoolbridge.v2.ui.common.FriendlyNetworkErrorCard
import com.schoolbridge.v2.ui.common.isExpandedLayout
import com.schoolbridge.v2.ui.common.SchoolBridgePatternBackground
import kotlinx.coroutines.launch
import java.util.UUID

// ─────────────────────────────────────────────────────────────
// Root navigation shell
// ─────────────────────────────────────────────────────────────

@Composable
fun MessageThreadScreen(
    userSessionManager: UserSessionManager,
    initialThreadId: String? = null,
    onThreadSelected: ((String) -> Unit)? = null,
    onBack: (() -> Unit)? = null
) {
    val isExpanded = isExpandedLayout()
    val messagingRepository = remember(userSessionManager) {
        MessagingRepositoryImpl(MessageApiServiceImpl(userSessionManager))
    }
    val messageRealtimeService = remember(userSessionManager) {
        MessageRealtimeServiceImpl(userSessionManager)
    }
    val viewModel: MessageThreadViewModel = viewModel(
        factory = MessageThreadViewModelFactory(messagingRepository, messageRealtimeService)
    )
    val uiState by viewModel.uiState.collectAsState()
    val messageThreads = uiState.threads
    val currentUser by userSessionManager.currentUser.collectAsState(initial = null)

    LaunchedEffect(currentUser?.userId) {
        currentUser?.userId?.let { userId ->
            viewModel.loadThreads(userId)
            viewModel.observeRealtime(userId)
        }
    }

    var internalSelectedThreadId by remember { mutableStateOf<String?>(null) }
    var viewingMessage by remember { mutableStateOf<Message?>(null) }
    var activePaymentMessage by remember { mutableStateOf<Message?>(null) }
    var activeCallMessage by remember { mutableStateOf<Message?>(null) }

    LaunchedEffect(isExpanded, messageThreads, initialThreadId) {
        if (isExpanded && initialThreadId == null && internalSelectedThreadId == null && messageThreads.isNotEmpty()) {
            internalSelectedThreadId = messageThreads.first().id
        }
    }

    val effectiveThreadId = initialThreadId ?: internalSelectedThreadId
    val selectedThread = messageThreads.find { it.id == effectiveThreadId }

    Box(modifier = Modifier.fillMaxSize()) {
        SchoolBridgePatternBackground()

        // MAIN CONTENT LAYER
        AnimatedContent(
            targetState = viewingMessage != null,
            label = "ScreenTransition"
        ) { isViewing ->
            if (uiState.isLoading && messageThreads.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.errorMessage != null && messageThreads.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    FriendlyNetworkErrorCard(
                        rawMessage = uiState.errorMessage,
                        onRetry = {
                            currentUser?.userId?.let { userId ->
                                viewModel.loadThreads(userId)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    )
                }
            } else if (isViewing && viewingMessage != null) {
                MessageDetailsScreen(
                    message = viewingMessage!!,
                    onBack = { viewingMessage = null },
                    onDelete = { viewingMessage = null },
                    onActionClick = { actionId ->
                        selectedThread?.let { thread ->
                            val originalMsg = viewingMessage!!
                            if (actionId == "pay_bill") {
                                activePaymentMessage = originalMsg
                            } else if (actionId in callActionIds) {
                                activeCallMessage = originalMsg
                            } else {
                                viewModel.performAction(thread.id, originalMsg.id, actionId)
                                val actionLabel = originalMsg.actions.find { it.actionId == actionId }?.label ?: "Confirmed"
                                viewModel.addUserMessage(
                                    threadId = thread.id,
                                    content = actionLabel,
                                    replyToId = originalMsg.id,
                                    replyToContent = originalMsg.content,
                                    replyToSender = originalMsg.sender
                                )
                                viewingMessage = viewingMessage!!.copy(status = "Confirmed")
                            }
                        }
                    }
                )
            } else {
                AnimatedContent(targetState = selectedThread, label = "ThreadTransition") { thread ->
                    if (isExpanded && initialThreadId == null) {
                        TabletThreadLayout(
                            threads = messageThreads,
                            selectedThread = thread,
                            currentUser = currentUser,
                            onThreadClick = {
                                internalSelectedThreadId = it.id
                                viewModel.markAsRead(it.id)
                            },
                            onMessageClick = { viewingMessage = it },
                            onActionClick = { msgId, actionId ->
                                val actualThread = thread ?: return@TabletThreadLayout
                                val originalMsg = actualThread.messages.find { it.id == msgId }
                                if (actionId == "pay_bill") {
                                    activePaymentMessage = originalMsg
                                } else if (actionId in callActionIds) {
                                    activeCallMessage = originalMsg
                                } else {
                                    viewModel.performAction(actualThread.id, msgId, actionId)
                                    val actionLabel = originalMsg?.actions?.find { it.actionId == actionId }?.label ?: "Responded"
                                    viewModel.addUserMessage(
                                        threadId = actualThread.id,
                                        content = actionLabel,
                                        replyToId = msgId,
                                        replyToContent = originalMsg?.content,
                                        replyToSender = originalMsg?.sender
                                    )
                                }
                            },
                            onSendMessage = { content ->
                                thread?.let { selected ->
                                    viewModel.addUserMessage(threadId = selected.id, content = content)
                                }
                            },
                            onLaunchThreadAction = { action ->
                                thread?.let { selected ->
                                    handleThreadComposerAction(
                                        action = action,
                                        thread = selected,
                                        currentUser = currentUser,
                                        onSendMessage = { content ->
                                            viewModel.addUserMessage(threadId = selected.id, content = content)
                                        },
                                        onOpenCallPreview = { message -> activeCallMessage = message }
                                    )
                                }
                            }
                        )
                    } else if (thread == null) {
                        ThreadListScreen(messageThreads) {
                            internalSelectedThreadId = it.id
                            viewModel.markAsRead(it.id)
                        }
                    } else {
                        ThreadDetailScreen(
                            thread = thread,
                            currentUser = currentUser,
                            onBack = { internalSelectedThreadId = null },
                            onMessageClick = { viewingMessage = it },
                            onActionClick = { msgId, actionId ->
                                val originalMsg = thread.messages.find { it.id == msgId }
                                if (actionId == "pay_bill") {
                                    activePaymentMessage = originalMsg
                                } else if (actionId in callActionIds) {
                                    activeCallMessage = originalMsg
                                } else {
                                    viewModel.performAction(thread.id, msgId, actionId)
                                    val actionLabel = originalMsg?.actions?.find { it.actionId == actionId }?.label ?: "Responded"
                                    viewModel.addUserMessage(
                                        threadId = thread.id,
                                        content = actionLabel,
                                        replyToId = msgId,
                                        replyToContent = originalMsg?.content,
                                        replyToSender = originalMsg?.sender
                                    )
                                }
                            },
                            onSendMessage = { content ->
                                viewModel.addUserMessage(threadId = thread.id, content = content)
                            },
                            onLaunchThreadAction = { action ->
                                handleThreadComposerAction(
                                    action = action,
                                    thread = thread,
                                    currentUser = currentUser,
                                    onSendMessage = { content ->
                                        viewModel.addUserMessage(threadId = thread.id, content = content)
                                    },
                                    onOpenCallPreview = { message -> activeCallMessage = message }
                                )
                            }
                        )
                    }
                }
            }
        }

        // PAYMENT LAYER (Moved to bottom of Box to be ON TOP)
        if (activePaymentMessage != null) {
            PaymentGatewayScreen(
                message = activePaymentMessage!!,
                onDismiss = { activePaymentMessage = null },
                onPaymentComplete = { transactionId ->
                    selectedThread?.let { thread ->
                        viewModel.addUserMessage(
                            threadId = thread.id,
                            content = "Paid via Mobile Money. Ref: $transactionId",
                            replyToId = activePaymentMessage!!.id,
                            replyToContent = activePaymentMessage!!.content,
                            replyToSender = activePaymentMessage!!.sender
                        )
                        // Also mark original message as updated in VM
                        viewModel.performAction(thread.id, activePaymentMessage!!.id, "mark_paid")
                    }
                    activePaymentMessage = null
                    viewingMessage = null // Close detail view if open
                }
            )
        }

        if (activeCallMessage != null) {
            ThreadCallPreviewScreen(
                message = activeCallMessage!!,
                onDismiss = { activeCallMessage = null },
                onStart = { type ->
                    selectedThread?.let { thread ->
                        val info = activeCallMessage!!.callInfo
                        viewModel.addThreadCall(
                            threadId = thread.id,
                            title = if (type == ThreadCallType.VIDEO) "Video call started" else "Audio call started",
                            content = "The ${type.name.lowercase()} room was opened from this thread, and the outcome can stay here after the call ends.",
                            callInfo = ThreadCallInfo(
                                type = type,
                                purpose = info?.purpose ?: ThreadCallPurpose.GENERAL,
                                status = ThreadCallStatus.ACTIVE,
                                hostLabel = "You",
                                participantSummary = thread.participantsLabel,
                                note = "This state is ready to be connected to real WebRTC signaling later."
                            ),
                            actions = listOf(
                                MessageAction("End Call", "end_call"),
                                MessageAction("Ask for Documents", "upload_documents")
                            )
                        )
                    }
                    activeCallMessage = null
                }
            )
        }
    }

    BackHandler(enabled = viewingMessage != null || internalSelectedThreadId != null || activePaymentMessage != null || activeCallMessage != null) {
        when {
            activeCallMessage != null -> activeCallMessage = null
            activePaymentMessage != null -> activePaymentMessage = null
            viewingMessage != null -> viewingMessage = null
            else -> internalSelectedThreadId = null
        }
    }
}

@Composable
private fun TabletThreadLayout(
    threads: List<MessageThread>,
    selectedThread: MessageThread?,
    currentUser: CurrentUser?,
    onThreadClick: (MessageThread) -> Unit,
    onMessageClick: (Message) -> Unit,
    onActionClick: (String, String) -> Unit,
    onSendMessage: (String) -> Unit,
    onLaunchThreadAction: (ThreadComposerAction) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            modifier = Modifier
                .weight(0.95f)
                .fillMaxHeight(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
            tonalElevation = 2.dp
        ) {
            ThreadListScreen(
                threads = threads,
                onThreadClick = onThreadClick
            )
        }

        Surface(
            modifier = Modifier
                .weight(1.35f)
                .fillMaxHeight(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
            tonalElevation = 3.dp
        ) {
            if (selectedThread == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Select a thread",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Tablet layouts work better when the conversation list stays visible beside the active discussion.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                ThreadDetailScreen(
                    thread = selectedThread,
                    currentUser = currentUser,
                    onBack = {},
                    onMessageClick = onMessageClick,
                    onActionClick = onActionClick,
                    onSendMessage = onSendMessage,
                    onLaunchThreadAction = onLaunchThreadAction,
                    showBackButton = false
                )
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
    val badgeStyle  = rememberThreadBadgeStyle(thread)
    val modeColor   = badgeStyle.containerColor
    val modeOnColor = badgeStyle.onContainerColor

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
                Text(badgeStyle.emoji, fontSize = 20.sp)
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
                            text     = badgeStyle.label.uppercase(),
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

@Composable
private fun rememberThreadBadgeStyle(thread: MessageThread): ThreadBadgeStyle {
    val latestMessage = thread.getLatestMessage()
    val latestIncomingActionIndex = thread.messages.indexOfLast { message ->
        !message.isFromCurrentUser && message.actions.isNotEmpty()
    }
    val hasPendingIncomingAction = latestIncomingActionIndex >= 0 &&
        thread.messages.drop(latestIncomingActionIndex + 1).none { it.isFromCurrentUser }

    return when {
        hasPendingIncomingAction -> ThreadBadgeStyle(
            label = "Action needed",
            emoji = "⚡",
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            onContainerColor = MaterialTheme.colorScheme.onTertiaryContainer
        )

        thread.mode == ThreadMode.ACTION_REQUIRED && latestMessage?.isFromCurrentUser == true -> ThreadBadgeStyle(
            label = "Awaiting reply",
            emoji = "⏳",
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            onContainerColor = MaterialTheme.colorScheme.onSecondaryContainer
        )

        thread.mode == ThreadMode.ACTION_REQUIRED -> ThreadBadgeStyle(
            label = "Updated",
            emoji = "✓",
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            onContainerColor = MaterialTheme.colorScheme.onSecondaryContainer
        )

        else -> ThreadBadgeStyle(
            label = threadModeLabel(thread.mode),
            emoji = threadModeEmoji(thread.mode),
            containerColor = threadModeContainerColor(thread.mode),
            onContainerColor = threadModeOnContainerColor(thread.mode)
        )
    }
}

private data class ThreadBadgeStyle(
    val label: String,
    val emoji: String,
    val containerColor: Color,
    val onContainerColor: Color
)

enum class ThreadComposerAction(
    val title: String,
    val subtitle: String,
    val icon: ImageVector
) {
    REQUEST_DOCUMENTS(
        title = "Request docs",
        subtitle = "Ask for evidence or files",
        icon = Icons.Default.Description
    ),
    SCHEDULE_MEETING(
        title = "Schedule meeting",
        subtitle = "Propose a parent or staff meeting",
        icon = Icons.Default.Event
    ),
    VERIFICATION_CALL(
        title = "Call invite",
        subtitle = "Open a verification or follow-up call",
        icon = Icons.Default.VideoCall
    ),
    ANNOUNCEMENT(
        title = "Announcement",
        subtitle = "Broadcast an official update in-thread",
        icon = Icons.Default.Campaign
    ),
    FINANCE_REMINDER(
        title = "Finance reminder",
        subtitle = "Remind about a pending payment",
        icon = Icons.Default.Payments
    ),
    PROGRESS_UPDATE(
        title = "Progress update",
        subtitle = "Share academic or behavior feedback",
        icon = Icons.Default.Info
    );
}

// ─────────────────────────────────────────────────────────────
// Thread detail screen
// ─────────────────────────────────────────────────────────────

@Composable
fun ThreadDetailScreen(
    thread: MessageThread,
    currentUser: CurrentUser?,
    onBack: () -> Unit,
    onMessageClick: (Message) -> Unit,
    onActionClick: (String, String) -> Unit,
    onSendMessage: (String) -> Unit,
    onLaunchThreadAction: (ThreadComposerAction) -> Unit,
    showBackButton: Boolean = true
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // ADDED COLUMN: This ensures the Top Bar stays at the top and the list below it
    Column(modifier = Modifier.fillMaxSize()) {
        Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 2.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (showBackButton) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
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
                        text = thread.participantsLabel,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = thread.topic,
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
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f) // Takes up remaining space
        ) {
            items(thread.messages, key = { it.id }) { message ->
                MessageCard(
                    message = message,
                    onClick = { onMessageClick(message) },
                    onActionClick = { onActionClick(message.id, it) },
                    onReplyClick = { replyId ->
                        val index = thread.messages.indexOfFirst { it.id == replyId }
                        if (index != -1) {
                            scope.launch {
                                listState.animateScrollToItem(index)
                            }
                        }
                    }
                )
            }
        }

        ThreadActionDock(
            thread = thread,
            currentUser = currentUser,
            onSendMessage = onSendMessage,
            onLaunchThreadAction = onLaunchThreadAction
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Message card
// ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageCard(
    message: Message,
    onClick: () -> Unit,
    onActionClick: (String) -> Unit,
    onReplyClick: (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val isSystem = !message.isFromCurrentUser
    val primary = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (isSystem) Alignment.Start else Alignment.End
    ) {
        // --- SENDER NAME (Only for System Messages) ---
        if (isSystem) {
            Text(
                text = message.sender,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )
        }

        // --- MAIN MESSAGE BUBBLE ---
        Surface(
            shape = RoundedCornerShape(
                topStart = if (isSystem) 4.dp else 20.dp,
                topEnd = 20.dp,
                bottomStart = 20.dp,
                bottomEnd = if (isSystem) 20.dp else 4.dp
            ),
            color = if (isSystem) MaterialTheme.colorScheme.surfaceContainerHigh
            else MaterialTheme.colorScheme.primaryContainer,
            tonalElevation = if (isSystem) 1.dp else 0.dp,
            modifier = Modifier
                .widthIn(max = 320.dp)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        // Trigger additional options like Copy or Delete if needed
                    }
                )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {

                // --- REPLY PREVIEW (The WhatsApp Scroll Trigger) ---
                if (message.replyToId != null) {
                    Surface(
                        onClick = { onReplyClick(message.replyToId) },
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                            // Vertical accent line
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(4.dp)
                                    .background(primary)
                            )
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = message.replyToSender ?: "Message",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = primary,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = message.replyToContent ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                message.callInfo?.let { callInfo ->
                    Spacer(Modifier.height(10.dp))
                    ThreadCallCard(callInfo = callInfo)
                }

                // --- MESSAGE TITLE (Optional) ---
                message.title?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isSystem) primary else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.height(4.dp))
                }

                // --- MESSAGE CONTENT ---
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSystem) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onPrimaryContainer
                )

                // --- STATUS BADGE (e.g., "Confirmed", "Marked as Paid") ---
                if (message.status != null) {
                    Spacer(Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.tertiary)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = message.status,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }

                // --- ACTION BUTTONS (Hidden if status exists) ---
                if (message.actions.isNotEmpty() && message.status == null && !message.isFromCurrentUser) {
                    Spacer(Modifier.height(12.dp))
                    message.actions.forEach { action ->
                        val isPaymentAction = action.actionId == "pay_bill"

                        Button(
                            onClick = { onActionClick(action.actionId) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = if (isPaymentAction) {
                                ButtonDefaults.buttonColors(containerColor = primary)
                            } else {
                                ButtonDefaults.filledTonalButtonColors()
                            },
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            if (isPaymentAction) {
                                Text("💳 ", fontSize = 16.sp)
                            }
                            Text(
                                text = action.label,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                    }
                }

                // --- TIMESTAMP ---
                Text(
                    text = message.timestamp.split(", ").lastOrNull() ?: "",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp),
                    color = (if (isSystem) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onPrimaryContainer).copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun ThreadActionDock(
    thread: MessageThread,
    currentUser: CurrentUser?,
    onSendMessage: (String) -> Unit,
    onLaunchThreadAction: (ThreadComposerAction) -> Unit
) {
    var composerText by remember(thread.id) { mutableStateOf("") }
    var toolsExpanded by remember(thread.id) { mutableStateOf(false) }
    val availableActions = remember(currentUser?.currentRole, thread.mode) {
        availableThreadComposerActions(currentUser?.currentRole, thread.mode)
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (toolsExpanded && availableActions.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Launch something from this thread",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        availableActions.forEach { action ->
                            ElevatedCard(
                                onClick = { onLaunchThreadAction(action) },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                ),
                                modifier = Modifier.width(196.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.primaryContainer
                                        ) {
                                            Icon(
                                                imageVector = action.icon,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }
                                        Text(
                                            text = action.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Text(
                                        text = action.subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilledTonalIconButton(
                    onClick = { toolsExpanded = !toolsExpanded }
                ) {
                    Icon(
                        imageVector = if (toolsExpanded) Icons.Default.Close else Icons.Default.Schedule,
                        contentDescription = if (toolsExpanded) "Hide tools" else "Show tools"
                    )
                }

                OutlinedTextField(
                    value = composerText,
                    onValueChange = { composerText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            when (currentUser?.currentRole) {
                                UserRole.SCHOOL_ADMIN -> "Write an official follow-up or launch an action..."
                                UserRole.TEACHER -> "Share an update, meeting note, or class action..."
                                else -> "Reply in this school thread..."
                            }
                        )
                    },
                    maxLines = 4,
                    shape = RoundedCornerShape(22.dp)
                )

                FilledIconButton(
                    onClick = {
                        val message = composerText.trim()
                        if (message.isNotEmpty()) {
                            onSendMessage(message)
                            composerText = ""
                        }
                    },
                    enabled = composerText.isNotBlank()
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }
}

@Composable
private fun ThreadCallCard(callInfo: ThreadCallInfo) {
    val scheme = MaterialTheme.colorScheme
    val (container, content) = when (callInfo.status) {
        ThreadCallStatus.ACTIVE -> scheme.primaryContainer to scheme.onPrimaryContainer
        ThreadCallStatus.SCHEDULED -> scheme.secondaryContainer to scheme.onSecondaryContainer
        ThreadCallStatus.MISSED,
        ThreadCallStatus.DECLINED -> scheme.errorContainer to scheme.onErrorContainer
        ThreadCallStatus.NEEDS_DOCUMENTS -> scheme.tertiaryContainer to scheme.onTertiaryContainer
        else -> scheme.surfaceContainerHighest to scheme.onSurface
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = container,
        border = BorderStroke(1.dp, scheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(shape = CircleShape, color = content.copy(alpha = 0.12f)) {
                    Icon(
                        imageVector = when (callInfo.type) {
                            ThreadCallType.AUDIO -> Icons.Default.Phone
                            ThreadCallType.VIDEO -> Icons.Default.VideoCall
                            ThreadCallType.LIVE_ANNOUNCEMENT -> Icons.Default.LiveTv
                        },
                        contentDescription = null,
                        tint = content,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(18.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = threadCallTitle(callInfo),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = content
                    )
                    Text(
                        text = threadCallSubtitle(callInfo),
                        style = MaterialTheme.typography.bodySmall,
                        color = content.copy(alpha = 0.86f)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CallMetaChip(
                    icon = when (callInfo.status) {
                        ThreadCallStatus.SCHEDULED -> Icons.Default.Schedule
                        ThreadCallStatus.MISSED -> Icons.Default.CallMissed
                        ThreadCallStatus.NEEDS_DOCUMENTS -> Icons.Default.Upload
                        else -> Icons.Default.Check
                    },
                    label = threadCallStatusLabel(callInfo.status)
                )
                callInfo.scheduledLabel?.let {
                    CallMetaChip(icon = Icons.Default.Schedule, label = it)
                }
                callInfo.durationLabel?.let {
                    CallMetaChip(icon = Icons.Default.Info, label = it)
                }
            }

            callInfo.note?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CallMetaChip(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(12.dp))
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

private val callActionIds = setOf(
    "start_audio_call",
    "start_video_call",
    "join_live_announcement",
    "schedule_verification_call"
)

private fun availableThreadComposerActions(
    role: UserRole?,
    mode: ThreadMode
): List<ThreadComposerAction> = when (role) {
    UserRole.SCHOOL_ADMIN -> listOf(
        ThreadComposerAction.REQUEST_DOCUMENTS,
        ThreadComposerAction.VERIFICATION_CALL,
        ThreadComposerAction.ANNOUNCEMENT,
        ThreadComposerAction.FINANCE_REMINDER
    )
    UserRole.TEACHER -> listOf(
        ThreadComposerAction.PROGRESS_UPDATE,
        ThreadComposerAction.SCHEDULE_MEETING,
        ThreadComposerAction.VERIFICATION_CALL,
        ThreadComposerAction.ANNOUNCEMENT
    )
    UserRole.PARENT -> listOf(
        ThreadComposerAction.SCHEDULE_MEETING,
        ThreadComposerAction.PROGRESS_UPDATE
    )
    else -> when (mode) {
        ThreadMode.ACTION_REQUIRED -> listOf(
            ThreadComposerAction.REQUEST_DOCUMENTS,
            ThreadComposerAction.SCHEDULE_MEETING
        )
        else -> listOf(
            ThreadComposerAction.SCHEDULE_MEETING,
            ThreadComposerAction.PROGRESS_UPDATE
        )
    }
}

private fun handleThreadComposerAction(
    action: ThreadComposerAction,
    thread: MessageThread,
    currentUser: CurrentUser?,
    onSendMessage: (String) -> Unit,
    onOpenCallPreview: (Message) -> Unit
) {
    when (action) {
        ThreadComposerAction.REQUEST_DOCUMENTS -> {
            onSendMessage(
                "Please upload the requested documents for this thread so the school side can review them here."
            )
        }
        ThreadComposerAction.SCHEDULE_MEETING -> {
            onSendMessage(
                "I would like to schedule a follow-up meeting from this thread so the next steps stay attached to this conversation."
            )
        }
        ThreadComposerAction.ANNOUNCEMENT -> {
            onSendMessage(
                "Official update: please use this thread as the source of truth for the latest school-side follow-up and replies."
            )
        }
        ThreadComposerAction.FINANCE_REMINDER -> {
            onSendMessage(
                "Finance reminder: there is still a pending item linked to this thread. Please review it here and reply if clarification is needed."
            )
        }
        ThreadComposerAction.PROGRESS_UPDATE -> {
            onSendMessage(
                "Progress update: I am sharing a structured follow-up from this thread so attendance, performance, or behavior notes stay in one place."
            )
        }
        ThreadComposerAction.VERIFICATION_CALL -> {
            onOpenCallPreview(
                Message(
                    senderUserId = currentUser?.userId,
                    sender = listOfNotNull(currentUser?.firstName, currentUser?.lastName).joinToString(" ").ifBlank { "You" },
                    title = "Call invitation",
                    content = "Open a call from this thread so the discussion, verification, and follow-up all stay together.",
                    timestamp = "Now",
                    isFromCurrentUser = true,
                    callInfo = ThreadCallInfo(
                        type = ThreadCallType.VIDEO,
                        purpose = when (thread.mode) {
                            ThreadMode.ACTION_REQUIRED -> ThreadCallPurpose.ROLE_VERIFICATION
                            ThreadMode.ANNOUNCEMENT -> ThreadCallPurpose.ANNOUNCEMENT
                            else -> ThreadCallPurpose.GENERAL
                        },
                        status = ThreadCallStatus.REQUESTED,
                        hostLabel = "You",
                        participantSummary = thread.participantsLabel,
                        note = "This invitation is being prepared from the action dock."
                    )
                )
            )
        }
    }
}

private fun threadCallTitle(callInfo: ThreadCallInfo): String = when (callInfo.purpose) {
    ThreadCallPurpose.ROLE_VERIFICATION -> "Verification room"
    ThreadCallPurpose.FINANCE_ESCALATION -> "Finance escalation"
    ThreadCallPurpose.DISCIPLINE_ESCALATION -> "Discipline follow-up"
    ThreadCallPurpose.ANNOUNCEMENT -> "Announcement room"
    ThreadCallPurpose.GENERAL -> "Thread call"
}

private fun threadCallSubtitle(callInfo: ThreadCallInfo): String = when (callInfo.type) {
    ThreadCallType.AUDIO -> "Audio with ${callInfo.participantSummary}"
    ThreadCallType.VIDEO -> "Video with ${callInfo.participantSummary}"
    ThreadCallType.LIVE_ANNOUNCEMENT -> "Live session for ${callInfo.participantSummary}"
}

private fun threadCallStatusLabel(status: ThreadCallStatus): String = when (status) {
    ThreadCallStatus.REQUESTED -> "Requested"
    ThreadCallStatus.SCHEDULED -> "Scheduled"
    ThreadCallStatus.RINGING -> "Ringing"
    ThreadCallStatus.ACTIVE -> "Live now"
    ThreadCallStatus.ENDED -> "Ended"
    ThreadCallStatus.MISSED -> "Missed"
    ThreadCallStatus.DECLINED -> "Declined"
    ThreadCallStatus.NEEDS_DOCUMENTS -> "Need docs"
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentGatewayScreen(
    message: Message,
    onDismiss: () -> Unit,
    onPaymentComplete: (String) -> Unit
) {
    val amount = "250,000 RWF" // Ideally parsed from message.content or a new field

    // Use a Surface to create a "Modal" or "Full Screen" overlay effect
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // TOP BAR
            TopAppBar(
                title = { Text("Select Payment Method", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            )

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // INFO CARD
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Paying for: ${message.title ?: "School Fees"}", style = MaterialTheme.typography.bodySmall)
                            Text("Total Amount: $amount", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Text("Available Providers", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)

                // 1. MTN MoMo (STK Push / USSD)
                PaymentProviderCard(
                    name = "MTN Mobile Money",
                    subtitle = "Pay via MoMo Push (Enter PIN on phone)",
                    iconRes = R.drawable.momo,
                    brandColor = Color(0xFFFFCC00), // MTN Yellow
                    onClick = { onPaymentComplete("MTN-TX-${UUID.randomUUID().toString().take(6)}") }
                )

                // 2. Airtel Money (STK Push)
                PaymentProviderCard(
                    name = "Airtel Money",
                    subtitle = "Pay via Airtel Push (Enter PIN on phone)",
                    iconRes = R.drawable.airtel_logo,
                    brandColor = Color(0xFFFF0000), // Airtel Red
                    onClick = { onPaymentComplete("AIR-TX-${UUID.randomUUID().toString().take(6)}") }
                )

                // 3. Irembo (Billing ID Generation)
                PaymentProviderCard(
                    name = "Irembo",
                    subtitle = "Generate Billing ID for Bank/Agent payment",
                    iconRes = R.drawable.irembo_logo,
                    brandColor = Color(0xFF005FB8), // Irembo Blue
                    onClick = { onPaymentComplete("IREMBO-${UUID.randomUUID().toString().take(8).uppercase()}") }
                )

                // 4. Bank of Kigali (BK Quick)
                PaymentProviderCard(
                    name = "Bank of Kigali",
                    subtitle = "Pay via BK App or *334#",
                    iconRes = R.drawable.bk,
                    brandColor = Color(0xFF003366), // BK Blue
                    onClick = { onPaymentComplete("BK-REF-${UUID.randomUUID().toString().take(6)}") }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreadCallPreviewScreen(
    message: Message,
    onDismiss: () -> Unit,
    onStart: (ThreadCallType) -> Unit
) {
    val callInfo = message.callInfo ?: return

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Thread Call Room", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            )

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = message.title ?: threadCallTitle(callInfo),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = message.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.92f)
                        )
                        ThreadCallCard(callInfo = callInfo)
                    }
                }

                Text(
                    text = "This room starts from the thread so the call, uploaded documents, and follow-up stay linked in one place.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = { onStart(callInfo.type) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = when (callInfo.type) {
                            ThreadCallType.AUDIO -> Icons.Default.Phone
                            ThreadCallType.VIDEO -> Icons.Default.VideoCall
                            ThreadCallType.LIVE_ANNOUNCEMENT -> Icons.Default.LiveTv
                        },
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        when (callInfo.type) {
                            ThreadCallType.AUDIO -> "Start Audio Call"
                            ThreadCallType.VIDEO -> "Start Video Call"
                            ThreadCallType.LIVE_ANNOUNCEMENT -> "Open Live Room"
                        }
                    )
                }

                if (callInfo.purpose == ThreadCallPurpose.ROLE_VERIFICATION) {
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Upload, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Request or Upload Documents")
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentProviderCard(
    name: String,
    subtitle: String,
    iconRes: Int,
    brandColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = name,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(brandColor.copy(alpha = 0.1f))
                    .padding(8.dp)
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}
