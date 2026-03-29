package com.schoolbridge.v2.ui.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.schoolbridge.v2.data.dto.message.MobileMessageActionDto
import com.schoolbridge.v2.data.dto.message.MobileMessageDto
import com.schoolbridge.v2.data.dto.message.MobileMessageConversationDto
import com.schoolbridge.v2.data.dto.message.MobileConversationCallSummaryDto
import com.schoolbridge.v2.data.remote.MessageRealtimeService
import com.schoolbridge.v2.data.repository.interfaces.MessagingRepository
import com.schoolbridge.v2.domain.messaging.Message
import com.schoolbridge.v2.domain.messaging.MessageAction
import com.schoolbridge.v2.domain.messaging.MessageConversation
import com.schoolbridge.v2.domain.messaging.ConversationCallInfo
import com.schoolbridge.v2.domain.messaging.ConversationCallPurpose
import com.schoolbridge.v2.domain.messaging.ConversationCallStatus
import com.schoolbridge.v2.domain.messaging.ConversationCallType
import com.schoolbridge.v2.domain.messaging.ConversationMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

data class MessageConversationsUiState(
    val isLoading: Boolean = false,
    val conversations: List<MessageConversation> = emptyList(),
    val errorMessage: String? = null,
    val pendingMessageActions: Map<String, String> = emptyMap()
)

class MessageConversationViewModel(
    private val messagingRepository: MessagingRepository,
    private val messageRealtimeService: MessageRealtimeService
) : ViewModel() {

    private val _uiState = MutableStateFlow(MessageConversationsUiState(isLoading = true))
    val uiState: StateFlow<MessageConversationsUiState> = _uiState.asStateFlow()
    private var activeUserId: String? = null
    private var realtimeJob: Job? = null

    fun loadConversations(currentUserId: String) {
        refreshConversations(currentUserId, showLoading = true)
    }

    fun refreshInBackground(currentUserId: String) {
        refreshConversations(currentUserId, showLoading = false)
    }

    private fun refreshConversations(currentUserId: String, showLoading: Boolean) {
        activeUserId = currentUserId
        if (showLoading && _uiState.value.isLoading && _uiState.value.conversations.isNotEmpty()) return

        viewModelScope.launch {
            if (showLoading) {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            }
            runCatching {
                messagingRepository.getMessageConversations()
            }.onSuccess { response ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    conversations = response.toDomainConversations(currentUserId),
                    errorMessage = null
                )
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = throwable.message ?: "Could not load messages"
                )
            }
        }
    }

    fun observeRealtime(currentUserId: String) {
        activeUserId = currentUserId
        if (realtimeJob?.isActive == true) return

        realtimeJob = viewModelScope.launch {
            runCatching {
                messageRealtimeService.observeEvents().collectLatest {
                    refreshConversations(currentUserId, showLoading = false)
                }
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = throwable.message ?: "Live message updates are temporarily unavailable."
                )
            }.also {
                realtimeJob = null
            }
        }
    }

    fun retry(currentUserId: String) {
        realtimeJob?.cancel()
        realtimeJob = null
        loadConversations(currentUserId)
        observeRealtime(currentUserId)
    }

    fun addUserMessage(
        conversationId: String,
        content: String,
        replyToId: String? = null,
        replyToContent: String? = null,
        replyToSender: String? = null,
        callInfo: ConversationCallInfo? = null
    ) {
        viewModelScope.launch {
            replyToId
            replyToContent
            replyToSender
            callInfo
            val currentUserId = activeUserId?.toLongOrNull() ?: return@launch
            val conversation = _uiState.value.conversations.find { it.id == conversationId } ?: return@launch
            val conversationId = conversation.backendConversationId ?: conversation.id.toLongOrNull() ?: return@launch

            runCatching {
                messagingRepository.sendMessage(
                    conversationId = conversationId,
                    senderId = currentUserId,
                    content = formatUserReply(content)
                )
            }.onSuccess {
                refreshConversations(activeUserId ?: return@onSuccess, showLoading = false)
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(errorMessage = throwable.message ?: "Could not send response")
            }
        }
    }

    fun submitMessageAction(
        conversationId: String,
        message: Message,
        actionId: String
    ) {
        val pendingKey = pendingActionKey(conversationId = conversationId, messageId = message.id)
        val previousConversation = _uiState.value.conversations.find { it.id == conversationId }
        val previousMessages = previousConversation?.messages?.toList().orEmpty()
        val actionLabel = message.actions.find { it.actionId == actionId }?.label ?: "Responded"
        val replyContent = formatUserReply(actionLabel)

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                pendingMessageActions = _uiState.value.pendingMessageActions + (pendingKey to actionId),
                conversations = _uiState.value.conversations.map { conversation ->
                    if (conversation.id == conversationId) {
                        conversation.copy(
                            messages = upsertActionReplyMessage(
                                messages = conversation.messages,
                                requestMessage = message,
                                updatedStatus = actionStatusFor(actionId, message.status),
                                replyContent = replyContent
                            ).toMutableList()
                        )
                    } else {
                        conversation
                    }
                }
            )

            val currentUserId = activeUserId?.toLongOrNull()
            val conversation = _uiState.value.conversations.find { it.id == conversationId }
            val backendConversationId =
                conversation?.backendConversationId ?: conversationId.toLongOrNull()

            if (currentUserId == null || backendConversationId == null) {
                _uiState.value = _uiState.value.copy(
                    pendingMessageActions = _uiState.value.pendingMessageActions - pendingKey,
                    errorMessage = "Could not send response"
                )
                return@launch
            }

            runCatching {
                messagingRepository.sendMessage(
                    conversationId = backendConversationId,
                    senderId = currentUserId,
                    content = replyContent
                )
            }.onSuccess {
                Unit
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    conversations = _uiState.value.conversations.map { existingConversation ->
                        if (existingConversation.id == conversationId) {
                            existingConversation.copy(messages = previousMessages.toMutableList())
                        } else {
                            existingConversation
                        }
                    },
                    errorMessage = throwable.message ?: "Could not send response"
                )
            }

            _uiState.value = _uiState.value.copy(
                pendingMessageActions = _uiState.value.pendingMessageActions - pendingKey
            )
        }
    }

    fun performAction(conversationId: String, messageId: String, actionId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                conversations = _uiState.value.conversations.map { conversation ->
                    if (conversation.id == conversationId) {
                        val updatedMessages = conversation.messages.map { msg ->
                            if (msg.id == messageId) {
                                val newStatus = actionStatusFor(actionId, msg.status)
                                msg.copy(status = newStatus)
                            } else {
                                msg
                            }
                        }
                        conversation.copy(messages = updatedMessages.toMutableList())
                    } else {
                        conversation
                    }
                }
            )
        }
    }

    fun markAsRead(conversationId: String) {
        viewModelScope.launch {
            val currentUserId = activeUserId?.toLongOrNull() ?: return@launch
            val conversation = _uiState.value.conversations.find { it.id == conversationId } ?: return@launch
            val unreadMessages = conversation.messages.filter { it.isUnread && !it.isFromCurrentUser }

            _uiState.value = _uiState.value.copy(
                conversations = _uiState.value.conversations.map { existing ->
                    if (existing.id == conversationId) {
                        existing.copy(messages = existing.messages.map { it.copy(isUnread = false) }.toMutableList())
                    } else {
                        existing
                    }
                }
            )

            unreadMessages.forEach { message ->
                message.id.toLongOrNull()?.let { messageId ->
                    runCatching {
                        messagingRepository.markMessageAsRead(messageId = messageId, userId = currentUserId)
                    }
                }
            }
        }
    }

    fun addConversationCall(
        conversationId: String,
        title: String,
        content: String,
        callInfo: ConversationCallInfo,
        actions: List<MessageAction>
    ) {
        viewModelScope.launch {
            val callMessage = Message(
                id = UUID.randomUUID().toString(),
                senderUserId = null,
                sender = "SchoolBridge Call",
                title = title,
                content = content,
                timestamp = "Now",
                callInfo = callInfo,
                isFromCurrentUser = false,
                actions = actions
            )

            _uiState.value = _uiState.value.copy(
                conversations = _uiState.value.conversations.map { conversation ->
                    if (conversation.id == conversationId) {
                        conversation.copy(messages = (conversation.messages + callMessage).toMutableList())
                    } else {
                        conversation
                    }
                }
            )
        }
    }

    private fun formatUserReply(actionLabel: String): String = when (actionLabel.lowercase()) {
        "yes" -> "Yes, I'll be there"
        "no" -> "No, I can't make it"
        "not sure" -> "I am not sure yet"
        "mark as paid" -> "I've completed the payment"
        "acknowledge" -> "I have seen this notice"
        else -> actionLabel
    }

    private fun pendingActionKey(conversationId: String, messageId: String): String =
        "$conversationId:$messageId"

    private fun actionStatusFor(actionId: String, currentStatus: String?): String? = when (actionId) {
        "mark_paid" -> "Marked as Paid"
        "acknowledge" -> "Acknowledged"
        "yes" -> "Confirmed"
        "no" -> "Declined"
        "not_sure" -> "Pending"
        "pay_bill" -> currentStatus
        else -> "Updated"
    }

    private fun upsertActionReplyMessage(
        messages: List<Message>,
        requestMessage: Message,
        updatedStatus: String?,
        replyContent: String
    ): List<Message> {
        val updatedMessages = messages.map { existing ->
            if (existing.id == requestMessage.id) {
                existing.copy(status = updatedStatus)
            } else {
                existing
            }
        }.toMutableList()

        val existingReplyIndex = updatedMessages.indexOfLast { candidate ->
            candidate.isFromCurrentUser && candidate.replyToId == requestMessage.id
        }
        val updatedTimestamp = currentReplyTimestamp()

        if (existingReplyIndex >= 0) {
            val existingReply = updatedMessages[existingReplyIndex]
            updatedMessages[existingReplyIndex] = existingReply.copy(
                content = replyContent,
                timestamp = updatedTimestamp,
                isEdited = true
            )
        } else {
            updatedMessages += Message(
                sender = "You",
                content = replyContent,
                timestamp = updatedTimestamp,
                isFromCurrentUser = true,
                replyToId = requestMessage.id,
                replyToContent = requestMessage.content,
                replyToSender = requestMessage.sender
            )
        }

        return updatedMessages
    }

    private fun currentReplyTimestamp(): String =
        "Today, ${LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))}"
}

class MessageConversationViewModelFactory(
    private val messagingRepository: MessagingRepository,
    private val messageRealtimeService: MessageRealtimeService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessageConversationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MessageConversationViewModel(messagingRepository, messageRealtimeService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

private fun List<MobileMessageConversationDto>.toDomainConversations(currentUserId: String): List<MessageConversation> = map { conversation ->
    val mappedMessages = conversation.messages
        .map { it.toDomainMessage(currentUserId) }
        .associateBy { it.id }
        .toMutableMap()

    conversation.calls.forEach { call ->
        val callInfo = call.toCallInfo()
        val relatedMessageId = call.relatedMessageId
        if (relatedMessageId != null && mappedMessages.containsKey(relatedMessageId)) {
            mappedMessages[relatedMessageId] = mappedMessages.getValue(relatedMessageId).copy(callInfo = callInfo)
        } else {
            val syntheticMessageId = "call_${call.id}"
            mappedMessages[syntheticMessageId] = Message(
                id = syntheticMessageId,
                title = call.title,
                senderUserId = null,
                sender = call.hostLabel.ifBlank { "SchoolBridge Call" },
                content = call.note ?: "${call.type.lowercase().replace('_', ' ')} invitation attached to this conversation.",
                timestamp = call.scheduledLabel ?: call.startedAt ?: "Scheduled",
                isUnread = false,
                isFromCurrentUser = false,
                actions = call.defaultActions(),
                callInfo = callInfo
            )
        }
    }

    MessageConversation(
        id = conversation.id,
        backendConversationId = conversation.id.toLongOrNull(),
        topic = conversation.topic,
        participantsLabel = conversation.participantsLabel,
        mode = conversation.mode.toConversationMode(),
        messages = mappedMessages.values.toMutableList()
    )
}

private fun MobileMessageDto.toDomainMessage(currentUserId: String): Message = Message(
    id = id,
    title = title,
    senderUserId = senderUserId,
    sender = sender,
    content = content,
    timestamp = timestamp,
    isUnread = isUnread,
    isFromCurrentUser = senderUserId != null && senderUserId == currentUserId,
    actions = actions.map { it.toDomainAction() },
    status = status,
    isEdited = false,
    replyToId = replyToId,
    replyToContent = replyToContent,
    replyToSender = replyToSender
)

private fun MobileMessageActionDto.toDomainAction(): MessageAction = MessageAction(
    label = label,
    actionId = actionId
)

private fun MobileConversationCallSummaryDto.toCallInfo(): ConversationCallInfo = ConversationCallInfo(
    type = runCatching { ConversationCallType.valueOf(type) }.getOrDefault(ConversationCallType.AUDIO),
    purpose = runCatching { ConversationCallPurpose.valueOf(purpose) }.getOrDefault(ConversationCallPurpose.GENERAL),
    status = runCatching { ConversationCallStatus.valueOf(status) }.getOrDefault(ConversationCallStatus.REQUESTED),
    hostLabel = hostLabel,
    participantSummary = participantSummary ?: hostLabel,
    scheduledLabel = scheduledLabel,
    durationLabel = durationLabel,
    note = note
)

private fun MobileConversationCallSummaryDto.defaultActions(): List<MessageAction> {
    val primaryAction = when (type) {
        "VIDEO" -> MessageAction("Join Verification", "start_video_call")
        "LIVE_ANNOUNCEMENT" -> MessageAction("Join Briefing", "join_live_announcement")
        else -> MessageAction("Join Audio Call", "start_audio_call")
    }
    return listOf(primaryAction)
}

private fun String.toConversationMode(): ConversationMode = when (uppercase()) {
    "ACTION_REQUIRED" -> ConversationMode.ACTION_REQUIRED
    "CONVERSATION" -> ConversationMode.CONVERSATION
    "DIRECT_CONTACT" -> ConversationMode.DIRECT_CONTACT
    else -> ConversationMode.ANNOUNCEMENT
}
