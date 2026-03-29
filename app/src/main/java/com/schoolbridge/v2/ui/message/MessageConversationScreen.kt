package com.schoolbridge.v2.ui.message

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.schoolbridge.v2.R
import com.schoolbridge.v2.data.remote.MessageApiServiceImpl
import com.schoolbridge.v2.data.remote.MessageRealtimeServiceImpl
import com.schoolbridge.v2.data.repository.implementations.MessagingRepositoryImpl
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.domain.messaging.*
import com.schoolbridge.v2.domain.user.CurrentUser
import com.schoolbridge.v2.domain.user.UserRole
import com.schoolbridge.v2.ui.home.timetable.MeetingDecision
import com.schoolbridge.v2.ui.home.timetable.NotificationInteractionStore
import com.schoolbridge.v2.ui.common.isExpandedLayout
import com.schoolbridge.v2.ui.common.SchoolBridgePatternBackground
import com.schoolbridge.v2.ui.common.tutorial.CoachMarkOverlay
import com.schoolbridge.v2.ui.common.tutorial.CoachMarkStep
import com.schoolbridge.v2.ui.common.tutorial.CoachMarkTargetRegistry
import com.schoolbridge.v2.ui.common.tutorial.coachMarkTarget
import com.schoolbridge.v2.ui.common.tutorial.rememberCoachMarkTargetRegistry
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import java.util.UUID
import android.content.Context

// ─────────────────────────────────────────────────────────────
// Root navigation shell
// ─────────────────────────────────────────────────────────────

private const val MESSAGE_RETRY_INTERVAL_MILLIS = 5_000L
private const val MESSAGE_RECOVERY_TILE_DURATION_MILLIS = 2_500L

private enum class MessageConnectionState {
    DISCONNECTED,
    RECONNECTING,
    RECOVERED
}

enum class MessageInboxFilter(val label: String) {
    ALL("All conversations"),
    PINNED("Pinned"),
    UNREAD("Unread"),
    ACTION_NEEDED("Action needed"),
    ANNOUNCEMENTS("Announcements"),
    DIRECT("Direct contact"),
    MUTED("Muted"),
    ARCHIVED("Archived")
}

@Composable
fun MessageConversationScreen(
    userSessionManager: UserSessionManager,
    initialConversationId: String? = null,
    initialCallMessageId: String? = null,
    searchQuery: String = "",
    inboxFilter: MessageInboxFilter = MessageInboxFilter.ALL,
    onConversationSelected: ((String) -> Unit)? = null,
    onBack: (() -> Unit)? = null
) {
    val isExpanded = isExpandedLayout()
    val context = LocalContext.current
    val messagingRepository = remember(userSessionManager) {
        MessagingRepositoryImpl(MessageApiServiceImpl(userSessionManager))
    }
    val messageRealtimeService = remember(userSessionManager) {
        MessageRealtimeServiceImpl(userSessionManager)
    }
    val viewModel: MessageConversationViewModel = viewModel(
        factory = MessageConversationViewModelFactory(messagingRepository, messageRealtimeService)
    )
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by userSessionManager.currentUser.collectAsState(initial = null)
    var pinnedConversationIds by remember(currentUser?.userId) { mutableStateOf(emptySet<String>()) }
    var mutedConversationIds by remember(currentUser?.userId) { mutableStateOf(emptySet<String>()) }
    var archivedConversationIds by remember(currentUser?.userId) { mutableStateOf(emptySet<String>()) }
    LaunchedEffect(currentUser?.userId) {
        pinnedConversationIds = MessageInboxPreferences.getPinnedConversationIds(
            context = context,
            userId = currentUser?.userId
        )
        mutedConversationIds = MessageInboxPreferences.getMutedConversationIds(
            context = context,
            userId = currentUser?.userId
        )
        archivedConversationIds = MessageInboxPreferences.getArchivedConversationIds(
            context = context,
            userId = currentUser?.userId
        )
    }
    val messageConversations = remember(
        uiState.conversations,
        searchQuery,
        inboxFilter,
        pinnedConversationIds,
        mutedConversationIds,
        archivedConversationIds
    ) {
        uiState.conversations.filteredForInbox(
            searchQuery = searchQuery,
            filter = inboxFilter,
            pinnedConversationIds = pinnedConversationIds,
            mutedConversationIds = mutedConversationIds,
            archivedConversationIds = archivedConversationIds
        )
    }
    val latestUiState by rememberUpdatedState(uiState)
    var showRecoveryTile by remember { mutableStateOf(false) }
    var hadConnectionIssue by remember { mutableStateOf(false) }
    val isReconnectAttemptInFlight = hadConnectionIssue && uiState.isLoading
    val showPinnedStatusTile =
        (!uiState.errorMessage.isNullOrBlank() || isReconnectAttemptInFlight || showRecoveryTile) &&
            messageConversations.isNotEmpty()

    LaunchedEffect(currentUser?.userId) {
        currentUser?.userId?.let { userId ->
            viewModel.loadConversations(userId)
            viewModel.observeRealtime(userId)
        }
    }

    LaunchedEffect(currentUser?.userId) {
        val userId = currentUser?.userId ?: return@LaunchedEffect
        while (isActive) {
            delay(4_000L)
            viewModel.refreshInBackground(userId)
        }
    }

    LaunchedEffect(currentUser?.userId, uiState.errorMessage) {
        val userId = currentUser?.userId ?: return@LaunchedEffect
        if (uiState.errorMessage.isNullOrBlank()) return@LaunchedEffect

        while (isActive && !latestUiState.errorMessage.isNullOrBlank()) {
            delay(MESSAGE_RETRY_INTERVAL_MILLIS)
            if (!latestUiState.isLoading) {
                viewModel.retry(userId)
            }
        }
    }

    LaunchedEffect(uiState.errorMessage, uiState.isLoading, uiState.conversations) {
        if (!uiState.errorMessage.isNullOrBlank()) {
            hadConnectionIssue = true
            showRecoveryTile = false
            return@LaunchedEffect
        }

        if (hadConnectionIssue && !uiState.isLoading && uiState.conversations.isNotEmpty()) {
            showRecoveryTile = true
            hadConnectionIssue = false
            delay(MESSAGE_RECOVERY_TILE_DURATION_MILLIS)
            showRecoveryTile = false
        }
    }

    var internalSelectedConversationId by remember { mutableStateOf<String?>(null) }
    var viewingMessage by remember { mutableStateOf<Message?>(null) }
    var activePaymentMessage by remember { mutableStateOf<Message?>(null) }
    var activeCallMessage by remember { mutableStateOf<Message?>(null) }
    val pendingMessageActions = uiState.pendingMessageActions

    fun handleMessageAction(conversation: MessageConversation, originalMessage: Message, actionId: String) {
        if (actionId == "pay_bill") {
            activePaymentMessage = originalMessage
            return
        }
        if (actionId in callActionIds) {
            activeCallMessage = originalMessage
            return
        }

        actionId.toMeetingDecisionOrNull()?.let { decision ->
            NotificationInteractionStore.saveMeetingDecision(context, conversation.id, decision)
        }
        viewModel.submitMessageAction(
            conversationId = conversation.id,
            message = originalMessage,
            actionId = actionId
        )
    }

    LaunchedEffect(isExpanded, messageConversations, initialConversationId) {
        if (isExpanded && initialConversationId == null && internalSelectedConversationId == null && messageConversations.isNotEmpty()) {
            internalSelectedConversationId = messageConversations.first().id
        }
    }

    val effectiveConversationId = initialConversationId ?: internalSelectedConversationId
    val selectedConversation = messageConversations.find { it.id == effectiveConversationId }
    val leaveConversation: () -> Unit = {
        if (initialConversationId != null) {
            onBack?.invoke()
        } else {
            internalSelectedConversationId = null
        }
    }

    LaunchedEffect(selectedConversation?.id) {
        selectedConversation?.id?.let(viewModel::markAsRead)
    }

    LaunchedEffect(selectedConversation, initialCallMessageId) {
        val callMessageId = initialCallMessageId ?: return@LaunchedEffect
        val targetMessage = selectedConversation?.messages?.find { it.id == callMessageId } ?: return@LaunchedEffect
        if (targetMessage.callInfo != null && activeCallMessage?.id != targetMessage.id) {
            activeCallMessage = targetMessage
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        SchoolBridgePatternBackground()

        // MAIN CONTENT LAYER
        AnimatedContent(
            targetState = viewingMessage != null,
            label = "ScreenTransition"
        ) { isViewing ->
            if (isReconnectAttemptInFlight && messageConversations.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    MessageConnectionStatusTile(
                        message = "SchoolBridge is still unavailable. We are retrying automatically.",
                        state = MessageConnectionState.RECONNECTING,
                        onRetry = {
                            currentUser?.userId?.let(viewModel::retry)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    )
                }
            } else if (uiState.isLoading && messageConversations.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.errorMessage != null && messageConversations.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    MessageConnectionStatusTile(
                        message = uiState.errorMessage ?: "SchoolBridge is temporarily unavailable.",
                        state = if (uiState.isLoading) {
                            MessageConnectionState.RECONNECTING
                        } else {
                            MessageConnectionState.DISCONNECTED
                        },
                        onRetry = {
                            currentUser?.userId?.let(viewModel::retry)
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
                        selectedConversation?.let { conversation ->
                            handleMessageAction(conversation, viewingMessage!!, actionId)
                        }
                    },
                    pendingActionId = selectedConversation?.let { conversation ->
                        pendingMessageActions["${conversation.id}:${viewingMessage!!.id}"]
                    }
                )
            } else {
                if (isExpanded && initialConversationId == null) {
                    TabletConversationLayout(
                        conversations = messageConversations,
                        selectedConversationId = selectedConversation?.id,
                        selectedConversation = selectedConversation,
                        currentUser = currentUser,
                        pinnedConversationIds = pinnedConversationIds,
                        mutedConversationIds = mutedConversationIds,
                        archivedConversationIds = archivedConversationIds,
                        onConversationClick = {
                            internalSelectedConversationId = it.id
                            viewModel.markAsRead(it.id)
                        },
                        onTogglePinned = { conversationId ->
                            pinnedConversationIds = MessageInboxPreferences.togglePinnedConversation(
                                context = context,
                                userId = currentUser?.userId,
                                conversationId = conversationId
                            )
                        },
                        onToggleMuted = { conversationId ->
                            mutedConversationIds = MessageInboxPreferences.toggleMutedConversation(
                                context = context,
                                userId = currentUser?.userId,
                                conversationId = conversationId
                            )
                        },
                        onToggleArchived = { conversationId ->
                            archivedConversationIds = MessageInboxPreferences.toggleArchivedConversation(
                                context = context,
                                userId = currentUser?.userId,
                                conversationId = conversationId
                            )
                        },
                        onMessageClick = { viewingMessage = it },
                        onActionClick = { msgId, actionId ->
                            val actualConversation = selectedConversation ?: return@TabletConversationLayout
                            val originalMsg = actualConversation.messages.find { it.id == msgId }
                            if (originalMsg != null) {
                                handleMessageAction(actualConversation, originalMsg, actionId)
                            }
                        },
                        onSendMessage = { content ->
                            selectedConversation?.let { selected ->
                                viewModel.addUserMessage(conversationId = selected.id, content = content)
                            }
                        },
                        onLaunchConversationAction = { action ->
                            selectedConversation?.let { selected ->
                                handleConversationComposerAction(
                                    action = action,
                                    conversation = selected,
                                    currentUser = currentUser,
                                    onSendMessage = { content ->
                                        viewModel.addUserMessage(conversationId = selected.id, content = content)
                                    },
                                    onOpenCallPreview = { message -> activeCallMessage = message }
                                )
                            }
                        },
                        pendingMessageActions = pendingMessageActions
                    )
                } else {
                    AnimatedContent(targetState = selectedConversation, label = "ConversationTransition") { conversation ->
                        if (conversation == null) {
                            ConversationListScreen(
                                conversations = messageConversations,
                                pinnedConversationIds = pinnedConversationIds,
                                onConversationClick = {
                                    internalSelectedConversationId = it.id
                                    viewModel.markAsRead(it.id)
                                },
                                onTogglePinned = { conversationId ->
                                    pinnedConversationIds = MessageInboxPreferences.togglePinnedConversation(
                                        context = context,
                                        userId = currentUser?.userId,
                                        conversationId = conversationId
                                    )
                                }
                            )
                        } else {
                            ConversationDetailScreen(
                                conversation = conversation,
                                currentUser = currentUser,
                                isConversationMuted = conversation.id in mutedConversationIds,
                                isConversationArchived = conversation.id in archivedConversationIds,
                                onBack = leaveConversation,
                                onToggleMuted = {
                                    mutedConversationIds = MessageInboxPreferences.toggleMutedConversation(
                                        context = context,
                                        userId = currentUser?.userId,
                                        conversationId = conversation.id
                                    )
                                },
                                onToggleArchived = {
                                    archivedConversationIds = MessageInboxPreferences.toggleArchivedConversation(
                                        context = context,
                                        userId = currentUser?.userId,
                                        conversationId = conversation.id
                                    )
                                    leaveConversation()
                                },
                                onMessageClick = { viewingMessage = it },
                                onActionClick = { msgId, actionId ->
                                    val originalMsg = conversation.messages.find { it.id == msgId }
                                    if (originalMsg != null) {
                                        handleMessageAction(conversation, originalMsg, actionId)
                                    }
                                },
                                onSendMessage = { content ->
                                    viewModel.addUserMessage(conversationId = conversation.id, content = content)
                                },
                                onLaunchConversationAction = { action ->
                                    handleConversationComposerAction(
                                        action = action,
                                        conversation = conversation,
                                        currentUser = currentUser,
                                        onSendMessage = { content ->
                                            viewModel.addUserMessage(conversationId = conversation.id, content = content)
                                        },
                                        onOpenCallPreview = { message -> activeCallMessage = message }
                                    )
                                },
                                pendingMessageActions = pendingMessageActions
                            )
                        }
                    }
                }
            }
        }

        if (showPinnedStatusTile) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .align(Alignment.TopCenter)
            ) {
                MessageConnectionStatusTile(
                    message = when {
                        !uiState.errorMessage.isNullOrBlank() -> uiState.errorMessage ?: "SchoolBridge is temporarily unavailable."
                        isReconnectAttemptInFlight -> "Messages are still trying to reconnect to SchoolBridge."
                        else -> "Messages are live again. SchoolBridge reconnected successfully."
                    },
                    state = when {
                        showRecoveryTile -> MessageConnectionState.RECOVERED
                        isReconnectAttemptInFlight -> MessageConnectionState.RECONNECTING
                        else -> MessageConnectionState.DISCONNECTED
                    },
                    onRetry = {
                        currentUser?.userId?.let(viewModel::retry)
                    },
                    modifier = Modifier.alpha(0.98f)
                )
            }
        }

        // PAYMENT LAYER (Moved to bottom of Box to be ON TOP)
        if (activePaymentMessage != null) {
            PaymentGatewayScreen(
                message = activePaymentMessage!!,
                onDismiss = { activePaymentMessage = null },
                onPaymentComplete = { transactionId ->
                    selectedConversation?.let { conversation ->
                        viewModel.addUserMessage(
                            conversationId = conversation.id,
                            content = "Paid via Mobile Money. Ref: $transactionId",
                            replyToId = activePaymentMessage!!.id,
                            replyToContent = activePaymentMessage!!.content,
                            replyToSender = activePaymentMessage!!.sender
                        )
                        // Also mark original message as updated in VM
                        viewModel.performAction(conversation.id, activePaymentMessage!!.id, "mark_paid")
                    }
                    activePaymentMessage = null
                    viewingMessage = null // Close detail view if open
                }
            )
        }

        if (activeCallMessage != null) {
            ConversationCallPreviewScreen(
                message = activeCallMessage!!,
                onDismiss = { activeCallMessage = null },
                onStart = { type ->
                    selectedConversation?.let { conversation ->
                        val info = activeCallMessage!!.callInfo
                        viewModel.addConversationCall(
                            conversationId = conversation.id,
                            title = if (type == ConversationCallType.VIDEO) "Video call started" else "Audio call started",
                            content = "The ${type.name.lowercase()} room was opened from this conversation, and the outcome can stay here after the call ends.",
                            callInfo = ConversationCallInfo(
                                type = type,
                                purpose = info?.purpose ?: ConversationCallPurpose.GENERAL,
                                status = ConversationCallStatus.ACTIVE,
                                hostLabel = "You",
                                participantSummary = conversation.participantsLabel,
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

    BackHandler(enabled = viewingMessage != null || internalSelectedConversationId != null || activePaymentMessage != null || activeCallMessage != null) {
        when {
            activeCallMessage != null -> activeCallMessage = null
            activePaymentMessage != null -> activePaymentMessage = null
            viewingMessage != null -> viewingMessage = null
            else -> leaveConversation()
        }
    }
}

@Composable
private fun MessageConnectionStatusTile(
    message: String,
    state: MessageConnectionState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val accentContainer = when (state) {
        MessageConnectionState.DISCONNECTED -> colorScheme.errorContainer.copy(alpha = 0.7f)
        MessageConnectionState.RECONNECTING -> colorScheme.tertiaryContainer.copy(alpha = 0.78f)
        MessageConnectionState.RECOVERED -> colorScheme.secondaryContainer.copy(alpha = 0.82f)
    }
    val accentColor = when (state) {
        MessageConnectionState.DISCONNECTED -> colorScheme.error
        MessageConnectionState.RECONNECTING -> colorScheme.tertiary
        MessageConnectionState.RECOVERED -> colorScheme.secondary
    }
    val title = when (state) {
        MessageConnectionState.DISCONNECTED -> "SchoolBridge is temporarily down"
        MessageConnectionState.RECONNECTING -> "Reconnecting to SchoolBridge"
        MessageConnectionState.RECOVERED -> "SchoolBridge is back"
    }
    val helper = when (state) {
        MessageConnectionState.DISCONNECTED -> "We will keep checking the server in the background."
        MessageConnectionState.RECONNECTING -> "Trying again automatically every few seconds."
        MessageConnectionState.RECOVERED -> "Live updates are available again."
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = colorScheme.surfaceContainerHigh,
        tonalElevation = 4.dp,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentContainer)
            ) {
                ExpressiveRetryIndicator(
                    color = accentColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = helper,
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor
                )
            }

            if (state != MessageConnectionState.RECOVERED) {
                TextButton(onClick = onRetry) {
                    Text(if (state == MessageConnectionState.RECONNECTING) "Try now" else "Retry")
                }
            }
        }
    }
}

@Composable
private fun ExpressiveRetryIndicator(
    color: Color,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "reconnectLoader")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (Math.PI * 2f).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )
    val pulse by transition.animateFloat(
        initialValue = 0.82f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    androidx.compose.foundation.Canvas(modifier = modifier) {
        drawExpressiveRetryIndicator(
            color = color,
            phase = phase,
            pulse = pulse
        )
    }
}

private fun DrawScope.drawExpressiveRetryIndicator(
    color: Color,
    phase: Float,
    pulse: Float
) {
    val radius = min(size.width, size.height) / 2f
    val center = Offset(size.width / 2f, size.height / 2f)
    val orbitRadius = radius * 0.62f * pulse
    val dotRadius = radius * 0.12f

    repeat(6) { index ->
        val angle = phase + (index * 0.9f)
        val dotCenter = Offset(
            x = center.x + cos(angle) * orbitRadius,
            y = center.y + sin(angle) * orbitRadius
        )
        val alpha = 0.3f + (((sin(angle) + 1f) / 2f) * 0.7f)
        drawCircle(
            color = color.copy(alpha = alpha),
            radius = dotRadius + (alpha * radius * 0.04f),
            center = dotCenter
        )
    }

    drawCircle(
        color = color.copy(alpha = 0.18f),
        radius = radius * 0.38f * pulse,
        center = center
    )
}

@Composable
private fun TabletConversationLayout(
    conversations: List<MessageConversation>,
    selectedConversationId: String?,
    selectedConversation: MessageConversation?,
    currentUser: CurrentUser?,
    pinnedConversationIds: Set<String>,
    mutedConversationIds: Set<String>,
    archivedConversationIds: Set<String>,
    onConversationClick: (MessageConversation) -> Unit,
    onTogglePinned: (String) -> Unit,
    onToggleMuted: (String) -> Unit,
    onToggleArchived: (String) -> Unit,
    onMessageClick: (Message) -> Unit,
    onActionClick: (String, String) -> Unit,
    onSendMessage: (String) -> Unit,
    onLaunchConversationAction: (ConversationComposerAction) -> Unit,
    pendingMessageActions: Map<String, String>
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
            ConversationListScreen(
                conversations = conversations,
                selectedConversationId = selectedConversationId,
                pinnedConversationIds = pinnedConversationIds,
                mutedConversationIds = mutedConversationIds,
                onConversationClick = onConversationClick,
                onTogglePinned = onTogglePinned,
                onToggleMuted = onToggleMuted
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
            if (selectedConversation == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Select a conversation",
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
                ConversationDetailScreen(
                    conversation = selectedConversation,
                    currentUser = currentUser,
                    isConversationMuted = selectedConversation.id in mutedConversationIds,
                    isConversationArchived = selectedConversation.id in archivedConversationIds,
                    onBack = {},
                    onToggleMuted = { onToggleMuted(selectedConversation.id) },
                    onToggleArchived = { onToggleArchived(selectedConversation.id) },
                    onMessageClick = onMessageClick,
                    onActionClick = onActionClick,
                    onSendMessage = onSendMessage,
                    onLaunchConversationAction = onLaunchConversationAction,
                    showBackButton = false,
                    pendingMessageActions = pendingMessageActions
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Thread list screen
// ─────────────────────────────────────────────────────────────

@Composable
fun ConversationListScreen(
    conversations: List<MessageConversation>,
    onConversationClick: (MessageConversation) -> Unit,
    selectedConversationId: String? = null,
    pinnedConversationIds: Set<String> = emptySet(),
    mutedConversationIds: Set<String> = emptySet(),
    onTogglePinned: (String) -> Unit = {},
    onToggleMuted: (String) -> Unit = {},
    emptyStateTitle: String = "No messages yet",
    emptyStateBody: String = "New school conversations will appear here when they arrive."
) {
    if (conversations.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Text("📭", fontSize = 44.sp)
                Spacer(Modifier.height(12.dp))
                Text(
                    text = emptyStateTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = emptyStateBody,
                    style = MaterialTheme.typography.bodyMedium,
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
        items(conversations, key = { it.id }) { conversation ->
            ConversationCard(
                conversation = conversation,
                isSelected = conversation.id == selectedConversationId,
                isPinned = conversation.id in pinnedConversationIds,
                isMuted = conversation.id in mutedConversationIds,
                onClick = { onConversationClick(conversation) },
                onTogglePinned = { onTogglePinned(conversation.id) },
                onToggleMuted = { onToggleMuted(conversation.id) }
            )
        }
    }
}

private fun List<MessageConversation>.filteredForInbox(
    searchQuery: String,
    filter: MessageInboxFilter,
    pinnedConversationIds: Set<String>,
    mutedConversationIds: Set<String>,
    archivedConversationIds: Set<String>
): List<MessageConversation> {
    val normalizedQuery = searchQuery.trim().lowercase()
    return filter { conversation ->
        val matchesFilter = when (filter) {
            MessageInboxFilter.ALL -> conversation.id !in archivedConversationIds
            MessageInboxFilter.PINNED -> conversation.id in pinnedConversationIds
            MessageInboxFilter.UNREAD -> conversation.getUnreadCount() > 0
            MessageInboxFilter.ACTION_NEEDED -> conversation.findPendingIncomingAction() != null
            MessageInboxFilter.ANNOUNCEMENTS -> conversation.mode == ConversationMode.ANNOUNCEMENT
            MessageInboxFilter.DIRECT -> conversation.mode == ConversationMode.DIRECT_CONTACT
            MessageInboxFilter.MUTED -> conversation.id in mutedConversationIds && conversation.id !in archivedConversationIds
            MessageInboxFilter.ARCHIVED -> conversation.id in archivedConversationIds
        }
        if (!matchesFilter) return@filter false
        if (normalizedQuery.isBlank()) return@filter true

        buildList {
            add(conversation.topic)
            add(conversation.participantsLabel)
            conversation.getLatestMessage()?.content?.let(::add)
            conversation.getLatestMessage()?.sender?.let(::add)
            conversation.messages.takeLast(4).forEach { message ->
                add(message.content)
                add(message.sender)
                message.title?.let(::add)
            }
        }.any { candidate ->
            candidate.lowercase().contains(normalizedQuery)
        }
    }.sortedWith(
        compareByDescending<MessageConversation> { it.id in pinnedConversationIds }
            .thenBy { it.id in mutedConversationIds }
            .thenByDescending { it.getLatestMessage()?.timestamp.orEmpty() }
    )
}

// ─────────────────────────────────────────────────────────────
// Thread card
// ─────────────────────────────────────────────────────────────

@Composable
private fun ConversationCard(
    conversation: MessageConversation,
    isSelected: Boolean,
    isPinned: Boolean,
    isMuted: Boolean,
    onClick: () -> Unit,
    onTogglePinned: () -> Unit,
    onToggleMuted: () -> Unit
) {
    val isUnread    = conversation.getUnreadCount() > 0
    val latestMsg   = conversation.getLatestMessage()
    val badgeStyle  = rememberConversationBadgeStyle(conversation)
    val modeColor   = badgeStyle.containerColor
    val modeOnColor = badgeStyle.onContainerColor

    Surface(
        onClick         = onClick,
        shape           = RoundedCornerShape(16.dp),
        color           = if (isSelected) {
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        tonalElevation  = if (isSelected || isUnread) 2.dp else 0.dp,
        border          = if (isSelected) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
        } else {
            null
        },
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
                        text     = conversation.participantsLabel,
                        style    = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isUnread) FontWeight.SemiBold else FontWeight.Normal,
                            color      = MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    latestMsg?.timestamp?.let { ts ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isPinned) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            if (isMuted) {
                                Icon(
                                    imageVector = Icons.Default.VolumeOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text  = ts,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }

                Text(
                    text     = conversation.topic,
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

                    if (conversation.getUnreadCount() > 0) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .defaultMinSize(minWidth = 20.dp, minHeight = 20.dp)
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 5.dp)
                        ) {
                            Text(
                                text  = conversation.getUnreadCount().toString(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color      = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize   = 10.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = onTogglePinned,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isPinned) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = if (isPinned) "Unpin conversation" else "Pin conversation",
                            tint = if (isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = onToggleMuted,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeOff,
                            contentDescription = if (isMuted) "Unmute conversation" else "Mute conversation",
                            tint = if (isMuted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberConversationBadgeStyle(conversation: MessageConversation): ConversationBadgeStyle {
    val latestMessage = conversation.getLatestMessage()
    val hasPendingIncomingAction = conversation.findPendingIncomingAction() != null

    return when {
        hasPendingIncomingAction -> ConversationBadgeStyle(
            label = "Action needed",
            emoji = "⚡",
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            onContainerColor = MaterialTheme.colorScheme.onTertiaryContainer
        )

        conversation.mode == ConversationMode.ACTION_REQUIRED && latestMessage?.isFromCurrentUser == true -> ConversationBadgeStyle(
            label = "Awaiting reply",
            emoji = "⏳",
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            onContainerColor = MaterialTheme.colorScheme.onSecondaryContainer
        )

        conversation.mode == ConversationMode.ACTION_REQUIRED -> ConversationBadgeStyle(
            label = "Updated",
            emoji = "✓",
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            onContainerColor = MaterialTheme.colorScheme.onSecondaryContainer
        )

        else -> ConversationBadgeStyle(
            label = conversationModeLabel(conversation.mode),
            emoji = conversationModeEmoji(conversation.mode),
            containerColor = conversationModeContainerColor(conversation.mode),
            onContainerColor = conversationModeOnContainerColor(conversation.mode)
        )
    }
}

private data class ConversationBadgeStyle(
    val label: String,
    val emoji: String,
    val containerColor: Color,
    val onContainerColor: Color
)

private data class ConversationReplyPolicy(
    val canTypeFreely: Boolean,
    val canLaunchTools: Boolean,
    val pendingActionMessage: Message? = null,
    val helperTitle: String,
    val helperBody: String
)

enum class ConversationComposerAction(
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
        subtitle = "Broadcast an official update in-conversation",
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
fun ConversationDetailScreen(
    conversation: MessageConversation,
    currentUser: CurrentUser?,
    isConversationMuted: Boolean,
    isConversationArchived: Boolean,
    onBack: () -> Unit,
    onToggleMuted: () -> Unit,
    onToggleArchived: () -> Unit,
    onMessageClick: (Message) -> Unit,
    onActionClick: (String, String) -> Unit,
    onSendMessage: (String) -> Unit,
    onLaunchConversationAction: (ConversationComposerAction) -> Unit,
    showBackButton: Boolean = true,
    pendingMessageActions: Map<String, String> = emptyMap()
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val tutorialRegistry = rememberCoachMarkTargetRegistry()
    var starredMessageIds by remember(conversation.id, currentUser?.userId) {
        mutableStateOf(
            MessageInboxPreferences.getStarredMessageIds(
                context = context,
                userId = currentUser?.userId,
                conversationId = conversation.id
            )
        )
    }
    var showStarredOnly by rememberSaveable(conversation.id) { mutableStateOf(false) }
    val visibleMessages = remember(conversation.messages, starredMessageIds, showStarredOnly) {
        if (showStarredOnly) {
            conversation.messages.filter { it.id in starredMessageIds }
        } else {
            conversation.messages
        }
    }
    val pendingActionMessageId = remember(conversation.id, conversation.messages) {
        conversation.findPendingIncomingAction()?.id
    }
    val pendingActionIndex = remember(conversation.id, conversation.messages) {
        conversation.messages.indexOfFirst { it.id == pendingActionMessageId }
    }
    val scheduledCallMessageId = remember(conversation.id, conversation.messages) {
        conversation.messages.firstOrNull { message ->
            !message.isFromCurrentUser && message.callInfo?.status == ConversationCallStatus.SCHEDULED
        }?.id
    }
    val scheduledCallIndex = remember(conversation.id, conversation.messages) {
        conversation.messages.indexOfFirst { it.id == scheduledCallMessageId }
    }
    val conversationTourSteps = remember(
        conversation.id,
        pendingActionMessageId,
        scheduledCallMessageId
    ) {
        buildList {
            if (pendingActionMessageId != null) {
                add(
                    CoachMarkStep(
                        targetId = "conversation_pending_action",
                        title = "Respond from here",
                        body = "When the school needs a decision, use these action buttons instead of typing. Your answer stays in this conversation and updates the workflow."
                    )
                )
            }
            if (scheduledCallMessageId != null) {
                add(
                    CoachMarkStep(
                        targetId = "conversation_scheduled_call",
                        title = "Scheduled moments also appear in Schedule",
                        body = "Calls and planned school moments stay linked to this conversation, and they also show up in Schedule so you can find them later."
                    )
                )
            }
        }
    }
    var activeTourStep by rememberSaveable(conversation.id) {
        mutableStateOf(
            if (MessageConversationTourStore.shouldShow(context) && conversationTourSteps.isNotEmpty()) 0 else -1
        )
    }

    LaunchedEffect(activeTourStep) {
        if (activeTourStep >= 0 && !MessageConversationTourStore.hasStarted(context)) {
            MessageConversationTourStore.markStarted(context)
        }
    }

    LaunchedEffect(activeTourStep, conversationTourSteps) {
        val step = conversationTourSteps.getOrNull(activeTourStep) ?: return@LaunchedEffect
        val targetIndex = when (step.targetId) {
            "conversation_pending_action" -> pendingActionIndex
            "conversation_scheduled_call" -> scheduledCallIndex
            else -> -1
        }
        if (targetIndex >= 0) {
            listState.animateScrollToItem(targetIndex)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                            .background(conversationModeContainerColor(conversation.mode))
                    ) {
                        Text(conversationModeEmoji(conversation.mode), fontSize = 18.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = conversation.participantsLabel,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = conversation.topic,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (starredMessageIds.isNotEmpty()) {
                        FilterChip(
                            selected = showStarredOnly,
                            onClick = { showStarredOnly = !showStarredOnly },
                            label = {
                                Text(if (showStarredOnly) "Starred only" else "${starredMessageIds.size} starred")
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (showStarredOnly) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                    IconButton(onClick = onToggleMuted) {
                        Icon(
                            imageVector = Icons.Default.VolumeOff,
                            contentDescription = if (isConversationMuted) "Unmute conversation" else "Mute conversation",
                            tint = if (isConversationMuted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onToggleArchived) {
                        Icon(
                            imageVector = Icons.Default.Archive,
                            contentDescription = if (isConversationArchived) "Unarchive conversation" else "Archive conversation",
                            tint = if (isConversationArchived) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)

            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(visibleMessages, key = { it.id }) { message ->
                    MessageCard(
                        message = message,
                        isStarred = message.id in starredMessageIds,
                        onClick = { onMessageClick(message) },
                        onToggleStarred = {
                            starredMessageIds = MessageInboxPreferences.toggleStarredMessage(
                                context = context,
                                userId = currentUser?.userId,
                                conversationId = conversation.id,
                                messageId = message.id
                            )
                        },
                        onActionClick = { onActionClick(message.id, it) },
                        onReplyClick = { replyId ->
                            val index = conversation.messages.indexOfFirst { it.id == replyId }
                            if (index != -1) {
                                scope.launch {
                                    listState.animateScrollToItem(index)
                                }
                            }
                        },
                        tutorialRegistry = tutorialRegistry,
                        actionTargetId = if (message.id == pendingActionMessageId) {
                            "conversation_pending_action"
                        } else {
                            null
                        },
                        callTargetId = if (message.id == scheduledCallMessageId) {
                            "conversation_scheduled_call"
                        } else {
                            null
                        },
                        pendingActionId = pendingMessageActions["${conversation.id}:${message.id}"]
                    )
                }
            }

            ConversationActionDock(
                conversation = conversation,
                currentUser = currentUser,
                onSendMessage = onSendMessage,
                onLaunchConversationAction = onLaunchConversationAction
            )
        }

        if (activeTourStep >= 0 && conversationTourSteps.isNotEmpty()) {
            CoachMarkOverlay(
                registry = tutorialRegistry,
                steps = conversationTourSteps,
                currentIndex = activeTourStep,
                onSkip = {
                    MessageConversationTourStore.markSeen(context)
                    activeTourStep = -1
                },
                onNext = {
                    if (activeTourStep >= conversationTourSteps.lastIndex) {
                        MessageConversationTourStore.markSeen(context)
                        activeTourStep = -1
                    } else {
                        activeTourStep += 1
                    }
                },
                onDone = {
                    MessageConversationTourStore.markSeen(context)
                    activeTourStep = -1
                },
                onTargetUnavailable = {
                    if (activeTourStep >= conversationTourSteps.lastIndex) {
                        MessageConversationTourStore.markSeen(context)
                        activeTourStep = -1
                    } else {
                        activeTourStep += 1
                    }
                }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Message card
// ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageCard(
    message: Message,
    isStarred: Boolean,
    onClick: () -> Unit,
    onToggleStarred: () -> Unit,
    onActionClick: (String) -> Unit,
    onReplyClick: (String) -> Unit,
    tutorialRegistry: CoachMarkTargetRegistry? = null,
    actionTargetId: String? = null,
    callTargetId: String? = null,
    pendingActionId: String? = null
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onToggleStarred,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isStarred) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = if (isStarred) "Unstar message" else "Star message",
                            tint = if (isStarred) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            },
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

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
                    ConversationCallCard(
                        callInfo = callInfo,
                        modifier = if (callTargetId != null) {
                            Modifier.coachMarkTarget(callTargetId, tutorialRegistry)
                        } else {
                            Modifier
                        }
                    )
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
                    Column(
                        modifier = if (actionTargetId != null) {
                            Modifier
                                .padding(top = 12.dp)
                                .coachMarkTarget(actionTargetId, tutorialRegistry)
                        } else {
                            Modifier.padding(top = 12.dp)
                        }
                    ) {
                        message.actions.forEach { action ->
                            val isPaymentAction = action.actionId == "pay_bill"
                            val isPending = pendingActionId == action.actionId
                            val actionsLocked = pendingActionId != null

                            Button(
                                onClick = { onActionClick(action.actionId) },
                                enabled = !actionsLocked,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = if (isPaymentAction) {
                                    ButtonDefaults.buttonColors(containerColor = primary)
                                } else {
                                    ButtonDefaults.filledTonalButtonColors()
                                },
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                if (isPending) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(Modifier.width(8.dp))
                                } else if (isPaymentAction) {
                                    Text("💳 ", fontSize = 16.sp)
                                }
                                Text(
                                    text = if (isPending) "Sending..." else action.label,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                        }
                    }
                }

                // --- TIMESTAMP ---
                Text(
                    text = buildString {
                        append(message.timestamp.split(", ").lastOrNull() ?: "")
                        if (isStarred) append(" • Starred")
                        if (message.isEdited) append(" • Edited")
                    },
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
private fun ConversationActionDock(
    conversation: MessageConversation,
    currentUser: CurrentUser?,
    onSendMessage: (String) -> Unit,
    onLaunchConversationAction: (ConversationComposerAction) -> Unit
) {
    val replyPolicy = remember(conversation, currentUser?.currentRole) {
        buildConversationReplyPolicy(conversation = conversation, currentUser = currentUser)
    }
    var composerText by remember(conversation.id) { mutableStateOf("") }
    var toolsExpanded by remember(conversation.id) { mutableStateOf(false) }
    val availableActions = remember(currentUser?.currentRole, conversation.mode) {
        availableConversationComposerActions(currentUser?.currentRole, conversation.mode)
    }
    val showToolsToggle = replyPolicy.canLaunchTools && availableActions.isNotEmpty()
    val showComposer = replyPolicy.canTypeFreely

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
            ConversationReplyStatusCard(
                title = replyPolicy.helperTitle,
                body = replyPolicy.helperBody,
                isMuted = !showComposer && !showToolsToggle
            )

            if (toolsExpanded && showToolsToggle) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Launch something from this conversation",
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
                                onClick = { onLaunchConversationAction(action) },
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

            if (showComposer || showToolsToggle) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (showToolsToggle) {
                        FilledTonalIconButton(
                            onClick = { toolsExpanded = !toolsExpanded }
                        ) {
                            Icon(
                                imageVector = if (toolsExpanded) Icons.Default.Close else Icons.Default.Schedule,
                                contentDescription = if (toolsExpanded) "Hide tools" else "Show tools"
                            )
                        }
                    }

                    if (showComposer) {
                        OutlinedTextField(
                            value = composerText,
                            onValueChange = { composerText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = {
                                Text(
                                    when (currentUser?.currentRole) {
                                        UserRole.SCHOOL_ADMIN -> "Write an official follow-up for this conversation..."
                                        UserRole.TEACHER -> "Share a guided school follow-up..."
                                        else -> "Reply in this school conversation..."
                                    }
                                )
                            },
                            maxLines = 4,
                            shape = RoundedCornerShape(22.dp)
                        )
                    } else {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(22.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerLow
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Structured replies only",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "This conversation is using guided actions instead of free typing right now.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    if (showComposer) {
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
    }
}

@Composable
private fun ConversationReplyStatusCard(
    title: String,
    body: String,
    isMuted: Boolean
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (isMuted) {
            MaterialTheme.colorScheme.surfaceContainerLow
        } else {
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.58f)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun buildConversationReplyPolicy(
    conversation: MessageConversation,
    currentUser: CurrentUser?
): ConversationReplyPolicy {
    val role = currentUser?.currentRole
    val pendingIncomingAction = conversation.findPendingIncomingAction()
    val canLaunchTools = role == UserRole.SCHOOL_ADMIN || role == UserRole.TEACHER
    val canTypeFreely = (role == UserRole.SCHOOL_ADMIN || role == UserRole.TEACHER) &&
        conversation.mode in setOf(ConversationMode.CONVERSATION, ConversationMode.DIRECT_CONTACT)

    if (pendingIncomingAction != null) {
        return ConversationReplyPolicy(
            canTypeFreely = false,
            canLaunchTools = false,
            pendingActionMessage = pendingIncomingAction,
            helperTitle = "Reply expected through the request above",
            helperBody = "Use the buttons on the pending school message to answer this step. Free typing stays closed until that requested response is handled."
        )
    }

    return when {
        canTypeFreely -> ConversationReplyPolicy(
            canTypeFreely = true,
            canLaunchTools = canLaunchTools,
            helperTitle = "Direct school follow-up is open",
            helperBody = "This conversation allows a guided text response because your current role can continue the discussion from here."
        )

        canLaunchTools -> ConversationReplyPolicy(
            canTypeFreely = false,
            canLaunchTools = true,
            helperTitle = "Launch the next school action",
            helperBody = "Use the tools below to request documents, schedule a meeting, invite a call, or post the next official step without turning this into an open chat."
        )

        conversation.mode == ConversationMode.ANNOUNCEMENT -> ConversationReplyPolicy(
            canTypeFreely = false,
            canLaunchTools = false,
            helperTitle = "This conversation is read-only",
            helperBody = "Announcements can carry updates, links, and planned moments, but they do not always accept replies."
        )

        conversation.mode == ConversationMode.ACTION_REQUIRED -> ConversationReplyPolicy(
            canTypeFreely = false,
            canLaunchTools = false,
            helperTitle = "This step is complete for now",
            helperBody = "You already responded or there is no action waiting on this side right now. The conversation will reopen if the school asks for the next step."
        )

        else -> ConversationReplyPolicy(
            canTypeFreely = false,
            canLaunchTools = false,
            helperTitle = "No free reply needed right now",
            helperBody = "This conversation is still contextual and guided. When the workflow expects your input, SchoolBridge can reopen the right response control."
        )
    }
}

private fun MessageConversation.findPendingIncomingAction(): Message? {
    val candidateIndex = messages.indexOfLast { message ->
        !message.isFromCurrentUser && message.actions.isNotEmpty() && message.status == null
    }
    if (candidateIndex == -1) return null

    val hasAlreadyReplied = messages.drop(candidateIndex + 1).any { later ->
        later.isFromCurrentUser || later.replyToId == messages[candidateIndex].id
    }
    return if (hasAlreadyReplied) null else messages[candidateIndex]
}

@Composable
private fun ConversationCallCard(
    callInfo: ConversationCallInfo,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val (container, content) = when (callInfo.status) {
        ConversationCallStatus.ACTIVE -> scheme.primaryContainer to scheme.onPrimaryContainer
        ConversationCallStatus.SCHEDULED -> scheme.secondaryContainer to scheme.onSecondaryContainer
        ConversationCallStatus.MISSED,
        ConversationCallStatus.DECLINED -> scheme.errorContainer to scheme.onErrorContainer
        ConversationCallStatus.NEEDS_DOCUMENTS -> scheme.tertiaryContainer to scheme.onTertiaryContainer
        else -> scheme.surfaceContainerHighest to scheme.onSurface
    }

    Surface(
        modifier = modifier,
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
                            ConversationCallType.AUDIO -> Icons.Default.Phone
                            ConversationCallType.VIDEO -> Icons.Default.VideoCall
                            ConversationCallType.LIVE_ANNOUNCEMENT -> Icons.Default.LiveTv
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
                        text = conversationCallTitle(callInfo),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = content
                    )
                    Text(
                        text = conversationCallSubtitle(callInfo),
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
                        ConversationCallStatus.SCHEDULED -> Icons.Default.Schedule
                        ConversationCallStatus.MISSED -> Icons.Default.CallMissed
                        ConversationCallStatus.NEEDS_DOCUMENTS -> Icons.Default.Upload
                        else -> Icons.Default.Check
                    },
                    label = conversationCallStatusLabel(callInfo.status)
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

private object MessageConversationTourStore {
    private const val PREFS_NAME = "feature_tour_prefs"
    private const val KEY_CONVERSATION_TOUR_SEEN = "conversation_tour_seen"
    private const val KEY_CONVERSATION_TOUR_STARTED = "conversation_tour_started"

    fun shouldShow(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).let { prefs ->
            !prefs.getBoolean(KEY_CONVERSATION_TOUR_SEEN, false) &&
                !prefs.getBoolean(KEY_CONVERSATION_TOUR_STARTED, false)
        }

    fun hasStarted(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_CONVERSATION_TOUR_STARTED, false)

    fun markStarted(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_CONVERSATION_TOUR_STARTED, true)
            .apply()
    }

    fun markSeen(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_CONVERSATION_TOUR_SEEN, true)
            .apply()
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

private fun availableConversationComposerActions(
    role: UserRole?,
    mode: ConversationMode
): List<ConversationComposerAction> = when (role) {
    UserRole.SCHOOL_ADMIN -> listOf(
        ConversationComposerAction.REQUEST_DOCUMENTS,
        ConversationComposerAction.VERIFICATION_CALL,
        ConversationComposerAction.ANNOUNCEMENT,
        ConversationComposerAction.FINANCE_REMINDER
    )
    UserRole.TEACHER -> listOf(
        ConversationComposerAction.PROGRESS_UPDATE,
        ConversationComposerAction.SCHEDULE_MEETING,
        ConversationComposerAction.VERIFICATION_CALL,
        ConversationComposerAction.ANNOUNCEMENT
    )
    UserRole.PARENT -> listOf(
        ConversationComposerAction.SCHEDULE_MEETING,
        ConversationComposerAction.PROGRESS_UPDATE
    )
    else -> when (mode) {
        ConversationMode.ACTION_REQUIRED -> listOf(
            ConversationComposerAction.REQUEST_DOCUMENTS,
            ConversationComposerAction.SCHEDULE_MEETING
        )
        else -> listOf(
            ConversationComposerAction.SCHEDULE_MEETING,
            ConversationComposerAction.PROGRESS_UPDATE
        )
    }
}

private fun handleConversationComposerAction(
    action: ConversationComposerAction,
    conversation: MessageConversation,
    currentUser: CurrentUser?,
    onSendMessage: (String) -> Unit,
    onOpenCallPreview: (Message) -> Unit
) {
    when (action) {
        ConversationComposerAction.REQUEST_DOCUMENTS -> {
            onSendMessage(
                "Please upload the requested documents for this conversation so the school side can review them here."
            )
        }
        ConversationComposerAction.SCHEDULE_MEETING -> {
            onSendMessage(
                "I would like to schedule a follow-up meeting from this conversation so the next steps stay attached to this conversation."
            )
        }
        ConversationComposerAction.ANNOUNCEMENT -> {
            onSendMessage(
                "Official update: please use this conversation as the source of truth for the latest school-side follow-up and replies."
            )
        }
        ConversationComposerAction.FINANCE_REMINDER -> {
            onSendMessage(
                "Finance reminder: there is still a pending item linked to this conversation. Please review it here and reply if clarification is needed."
            )
        }
        ConversationComposerAction.PROGRESS_UPDATE -> {
            onSendMessage(
                "Progress update: I am sharing a structured follow-up from this conversation so attendance, performance, or behavior notes stay in one place."
            )
        }
        ConversationComposerAction.VERIFICATION_CALL -> {
            onOpenCallPreview(
                Message(
                    senderUserId = currentUser?.userId,
                    sender = listOfNotNull(currentUser?.firstName, currentUser?.lastName).joinToString(" ").ifBlank { "You" },
                    title = "Call invitation",
                    content = "Open a call from this conversation so the discussion, verification, and follow-up all stay together.",
                    timestamp = "Now",
                    isFromCurrentUser = true,
                    callInfo = ConversationCallInfo(
                        type = ConversationCallType.VIDEO,
                        purpose = when (conversation.mode) {
                            ConversationMode.ACTION_REQUIRED -> ConversationCallPurpose.ROLE_VERIFICATION
                            ConversationMode.ANNOUNCEMENT -> ConversationCallPurpose.ANNOUNCEMENT
                            else -> ConversationCallPurpose.GENERAL
                        },
                        status = ConversationCallStatus.REQUESTED,
                        hostLabel = "You",
                        participantSummary = conversation.participantsLabel,
                        note = "This invitation is being prepared from the action dock."
                    )
                )
            )
        }
    }
}

private fun conversationCallTitle(callInfo: ConversationCallInfo): String = when (callInfo.purpose) {
    ConversationCallPurpose.ROLE_VERIFICATION -> "Verification room"
    ConversationCallPurpose.FINANCE_ESCALATION -> "Finance escalation"
    ConversationCallPurpose.DISCIPLINE_ESCALATION -> "Discipline follow-up"
    ConversationCallPurpose.ANNOUNCEMENT -> "Announcement room"
    ConversationCallPurpose.GENERAL -> "Conversation call"
}

private fun conversationCallSubtitle(callInfo: ConversationCallInfo): String = when (callInfo.type) {
    ConversationCallType.AUDIO -> "Audio with ${callInfo.participantSummary}"
    ConversationCallType.VIDEO -> "Video with ${callInfo.participantSummary}"
    ConversationCallType.LIVE_ANNOUNCEMENT -> "Live session for ${callInfo.participantSummary}"
}

private fun conversationCallStatusLabel(status: ConversationCallStatus): String = when (status) {
    ConversationCallStatus.REQUESTED -> "Requested"
    ConversationCallStatus.SCHEDULED -> "Scheduled"
    ConversationCallStatus.RINGING -> "Ringing"
    ConversationCallStatus.ACTIVE -> "Live now"
    ConversationCallStatus.ENDED -> "Ended"
    ConversationCallStatus.MISSED -> "Missed"
    ConversationCallStatus.DECLINED -> "Declined"
    ConversationCallStatus.NEEDS_DOCUMENTS -> "Need docs"
}

private fun String.toMeetingDecisionOrNull(): MeetingDecision? = when (lowercase()) {
    "yes" -> MeetingDecision.ATTENDING
    "no" -> MeetingDecision.DECLINED
    "not_sure" -> MeetingDecision.MAYBE
    else -> null
}

// ─────────────────────────────────────────────────────────────
// ConversationMode helpers
// ─────────────────────────────────────────────────────────────

@Composable
fun conversationModeContainerColor(mode: ConversationMode) = when (mode) {
    ConversationMode.ANNOUNCEMENT    -> MaterialTheme.colorScheme.primaryContainer
    ConversationMode.ACTION_REQUIRED -> MaterialTheme.colorScheme.tertiaryContainer
    ConversationMode.CONVERSATION    -> MaterialTheme.colorScheme.secondaryContainer
    ConversationMode.DIRECT_CONTACT  -> MaterialTheme.colorScheme.secondaryContainer
}

@Composable
fun conversationModeOnContainerColor(mode: ConversationMode) = when (mode) {
    ConversationMode.ANNOUNCEMENT    -> MaterialTheme.colorScheme.onPrimaryContainer
    ConversationMode.ACTION_REQUIRED -> MaterialTheme.colorScheme.onTertiaryContainer
    ConversationMode.CONVERSATION    -> MaterialTheme.colorScheme.onSecondaryContainer
    ConversationMode.DIRECT_CONTACT  -> MaterialTheme.colorScheme.onSecondaryContainer
}

fun conversationModeEmoji(mode: ConversationMode) = when (mode) {
    ConversationMode.ANNOUNCEMENT    -> "📢"
    ConversationMode.ACTION_REQUIRED -> "⚡"
    ConversationMode.CONVERSATION    -> "💬"
    ConversationMode.DIRECT_CONTACT  -> "👤"
}

fun conversationModeLabel(mode: ConversationMode) = when (mode) {
    ConversationMode.ANNOUNCEMENT    -> "Announcement"
    ConversationMode.ACTION_REQUIRED -> "Action needed"
    ConversationMode.CONVERSATION    -> "Conversation"
    ConversationMode.DIRECT_CONTACT  -> "Direct"
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
fun ConversationCallPreviewScreen(
    message: Message,
    onDismiss: () -> Unit,
    onStart: (ConversationCallType) -> Unit
) {
    val callInfo = message.callInfo ?: return

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Conversation Call Room", style = MaterialTheme.typography.titleMedium) },
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
                            text = message.title ?: conversationCallTitle(callInfo),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = message.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.92f)
                        )
                        ConversationCallCard(callInfo = callInfo)
                    }
                }

                Text(
                    text = "This room starts from the conversation so the call, uploaded documents, and follow-up stay linked in one place.",
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
                            ConversationCallType.AUDIO -> Icons.Default.Phone
                            ConversationCallType.VIDEO -> Icons.Default.VideoCall
                            ConversationCallType.LIVE_ANNOUNCEMENT -> Icons.Default.LiveTv
                        },
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        when (callInfo.type) {
                            ConversationCallType.AUDIO -> "Start Audio Call"
                            ConversationCallType.VIDEO -> "Start Video Call"
                            ConversationCallType.LIVE_ANNOUNCEMENT -> "Open Live Room"
                        }
                    )
                }

                if (callInfo.purpose == ConversationCallPurpose.ROLE_VERIFICATION) {
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
