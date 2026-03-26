package com.schoolbridge.v2.ui.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolbridge.v2.domain.messaging.Message
import com.schoolbridge.v2.domain.messaging.MessageThread
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MessageThreadViewModel : ViewModel() {
    private val _messageThreads = MutableStateFlow<List<MessageThread>>(emptyList())
    val messageThreads: StateFlow<List<MessageThread>> = _messageThreads

    private fun updateMessageThreads(topic: String, newMessage: Message) {
        viewModelScope.launch {
            val currentThreads = _messageThreads.value.toMutableList()
            val existingThread = currentThreads.find { it.topic == topic }
            if (existingThread != null) {
                existingThread.messages.add(newMessage)
            } else {
                currentThreads.add(MessageThread(topic, mutableListOf(newMessage)))
            }
            _messageThreads.value = currentThreads
        }
    }

    fun sendMessage(topic: String, content: String) {
        if (content.isBlank()) return

        updateMessageThreads(
            topic = topic,
            newMessage = Message(
                sender = "You",
                content = content,
                timestamp = System.currentTimeMillis().toString(),
                isUnread = false
            )
        )
    }

    fun replaceThreads(threads: List<MessageThread>) {
        _messageThreads.value = threads
    }
}
