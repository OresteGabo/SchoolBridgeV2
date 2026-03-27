package com.schoolbridge.v2.ui.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.schoolbridge.v2.data.dto.message.MobileMessageActionDto
import com.schoolbridge.v2.data.dto.message.MobileMessageDto
import com.schoolbridge.v2.data.dto.message.MobileMessageThreadDto
import com.schoolbridge.v2.data.dto.message.MobileThreadCallSummaryDto
import com.schoolbridge.v2.data.repository.interfaces.MessagingRepository
import com.schoolbridge.v2.domain.messaging.Message
import com.schoolbridge.v2.domain.messaging.MessageAction
import com.schoolbridge.v2.domain.messaging.MessageThread
import com.schoolbridge.v2.domain.messaging.ThreadCallInfo
import com.schoolbridge.v2.domain.messaging.ThreadCallPurpose
import com.schoolbridge.v2.domain.messaging.ThreadCallStatus
import com.schoolbridge.v2.domain.messaging.ThreadCallType
import com.schoolbridge.v2.domain.messaging.ThreadMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class MessageThreadsUiState(
    val isLoading: Boolean = false,
    val threads: List<MessageThread> = emptyList(),
    val errorMessage: String? = null
)

class MessageThreadViewModel(
    private val messagingRepository: MessagingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MessageThreadsUiState(isLoading = true))
    val uiState: StateFlow<MessageThreadsUiState> = _uiState.asStateFlow()

    fun loadThreads(currentUserId: String) {
        if (_uiState.value.isLoading && _uiState.value.threads.isNotEmpty()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            runCatching {
                messagingRepository.getMessageThreads()
            }.onSuccess { response ->
                _uiState.value = MessageThreadsUiState(
                    isLoading = false,
                    threads = response.toDomainThreads(currentUserId),
                    errorMessage = null
                )
            }.onFailure { throwable ->
                _uiState.value = MessageThreadsUiState(
                    isLoading = false,
                    threads = emptyList(),
                    errorMessage = throwable.message ?: "Could not load messages"
                )
            }
        }
    }

    fun addUserMessage(
        threadId: String,
        content: String,
        replyToId: String? = null,
        replyToContent: String? = null,
        replyToSender: String? = null,
        callInfo: ThreadCallInfo? = null
    ) {
        viewModelScope.launch {
            val newMessage = Message(
                id = UUID.randomUUID().toString(),
                senderUserId = null,
                sender = "You",
                content = formatUserReply(content),
                timestamp = "Now",
                title = null,
                status = null,
                callInfo = callInfo,
                isFromCurrentUser = true,
                actions = emptyList(),
                isUnread = false,
                replyToId = replyToId,
                replyToContent = replyToContent,
                replyToSender = replyToSender
            )

            _uiState.value = _uiState.value.copy(
                threads = _uiState.value.threads.map { thread ->
                    if (thread.id == threadId) {
                        thread.copy(messages = (thread.messages + newMessage).toMutableList())
                    } else {
                        thread
                    }
                }
            )
        }
    }

    fun performAction(threadId: String, messageId: String, actionId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                threads = _uiState.value.threads.map { thread ->
                    if (thread.id == threadId) {
                        val updatedMessages = thread.messages.map { msg ->
                            if (msg.id == messageId) {
                                val newStatus = when (actionId) {
                                    "mark_paid" -> "Marked as Paid"
                                    "acknowledge" -> "Acknowledged"
                                    "yes" -> "Confirmed"
                                    "no" -> "Declined"
                                    "not_sure" -> "Pending"
                                    "pay_bill" -> msg.status
                                    else -> "Updated"
                                }
                                msg.copy(status = newStatus)
                            } else {
                                msg
                            }
                        }
                        thread.copy(messages = updatedMessages.toMutableList())
                    } else {
                        thread
                    }
                }
            )
        }
    }

    fun markAsRead(threadId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                threads = _uiState.value.threads.map { thread ->
                    if (thread.id == threadId) {
                        thread.copy(messages = thread.messages.map { it.copy(isUnread = false) }.toMutableList())
                    } else {
                        thread
                    }
                }
            )
        }
    }

    fun addThreadCall(
        threadId: String,
        title: String,
        content: String,
        callInfo: ThreadCallInfo,
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
                threads = _uiState.value.threads.map { thread ->
                    if (thread.id == threadId) {
                        thread.copy(messages = (thread.messages + callMessage).toMutableList())
                    } else {
                        thread
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
}

class MessageThreadViewModelFactory(
    private val messagingRepository: MessagingRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessageThreadViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MessageThreadViewModel(messagingRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

private fun List<MobileMessageThreadDto>.toDomainThreads(currentUserId: String): List<MessageThread> = map { thread ->
    val mappedMessages = thread.messages
        .map { it.toDomainMessage(currentUserId) }
        .associateBy { it.id }
        .toMutableMap()

    thread.calls.forEach { call ->
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
                content = call.note ?: "${call.type.lowercase().replace('_', ' ')} invitation attached to this thread.",
                timestamp = call.scheduledLabel ?: call.startedAt ?: "Scheduled",
                isUnread = false,
                isFromCurrentUser = false,
                actions = call.defaultActions(),
                callInfo = callInfo
            )
        }
    }

    MessageThread(
        id = thread.id,
        topic = thread.topic,
        participantsLabel = thread.participantsLabel,
        mode = thread.mode.toThreadMode(),
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
    replyToId = replyToId,
    replyToContent = replyToContent,
    replyToSender = replyToSender
)

private fun MobileMessageActionDto.toDomainAction(): MessageAction = MessageAction(
    label = label,
    actionId = actionId
)

private fun MobileThreadCallSummaryDto.toCallInfo(): ThreadCallInfo = ThreadCallInfo(
    type = runCatching { ThreadCallType.valueOf(type) }.getOrDefault(ThreadCallType.AUDIO),
    purpose = runCatching { ThreadCallPurpose.valueOf(purpose) }.getOrDefault(ThreadCallPurpose.GENERAL),
    status = runCatching { ThreadCallStatus.valueOf(status) }.getOrDefault(ThreadCallStatus.REQUESTED),
    hostLabel = hostLabel,
    participantSummary = participantSummary ?: hostLabel,
    scheduledLabel = scheduledLabel,
    durationLabel = durationLabel,
    note = note
)

private fun MobileThreadCallSummaryDto.defaultActions(): List<MessageAction> {
    val primaryAction = when (type) {
        "VIDEO" -> MessageAction("Join Verification", "start_video_call")
        "LIVE_ANNOUNCEMENT" -> MessageAction("Join Briefing", "join_live_announcement")
        else -> MessageAction("Join Audio Call", "start_audio_call")
    }
    return listOf(primaryAction)
}

private fun String.toThreadMode(): ThreadMode = when (uppercase()) {
    "ACTION_REQUIRED" -> ThreadMode.ACTION_REQUIRED
    "CONVERSATION" -> ThreadMode.CONVERSATION
    "DIRECT_CONTACT" -> ThreadMode.DIRECT_CONTACT
    else -> ThreadMode.ANNOUNCEMENT
}
