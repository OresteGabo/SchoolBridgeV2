package com.schoolbridge.v2.ui.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolbridge.v2.domain.messaging.Message
import com.schoolbridge.v2.domain.messaging.MessageAction
import com.schoolbridge.v2.domain.messaging.MessageThread
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class MessageThreadViewModel : ViewModel() {

    private val _messageThreads = MutableStateFlow(defaultThreads())
    val messageThreads: StateFlow<List<MessageThread>> = _messageThreads

    // 🔥 ADD USER MESSAGE (NEW)
    fun addUserMessage(threadId: String, content: String) {
        viewModelScope.launch {

            val newMessage = Message(
                id = UUID.randomUUID().toString(),
                sender = "You",
                content = formatUserReply(content),
                timestamp = now(),
                title = null,
                status = null,
                actions = emptyList(),
                isUnread = false
            )

            val updatedThreads = _messageThreads.value.map { thread ->
                if (thread.id == threadId) {
                    thread.copy(
                        messages = (thread.messages + newMessage).toMutableList()
                    )
                } else thread
            }

            _messageThreads.value = updatedThreads
        }
    }

    // 🔥 IMPROVED ACTION HANDLING
    fun performAction(threadId: String, messageId: String, actionId: String) {
        viewModelScope.launch {

            val updatedThreads = _messageThreads.value.map { thread ->
                if (thread.id == threadId) {

                    val updatedMessages = thread.messages.map { msg ->
                        if (msg.id == messageId) {

                            val newStatus = when (actionId) {
                                "mark_paid" -> "Marked as Paid"
                                "acknowledge" -> "Acknowledged"
                                "seen" -> "Seen"
                                "yes" -> "Confirmed"
                                "no" -> "Declined"
                                "not_sure" -> "Pending"
                                else -> "Updated"
                            }

                            msg.copy(status = newStatus)

                        } else msg
                    }

                    thread.copy(messages = updatedMessages.toMutableList())
                } else thread
            }

            _messageThreads.value = updatedThreads
        }
    }

    fun markAsRead(threadId: String) {
        viewModelScope.launch {
            val updated = _messageThreads.value.map { thread ->
                if (thread.id == threadId) {
                    thread.copy(
                        messages = thread.messages.map {
                            it.copy(isUnread = false)
                        }.toMutableList()
                    )
                } else thread
            }
            _messageThreads.value = updated
        }
    }

    // 🎯 SMART USER REPLY FORMATTING (VERY IMPORTANT UX)
    private fun formatUserReply(actionLabel: String): String {
        return when (actionLabel.lowercase()) {
            "yes" -> "Yes"
            "no" -> "No"
            "not sure" -> "Not sure"
            "mark as paid" -> "Payment completed"
            "acknowledge" -> "Acknowledged"
            else -> actionLabel
        }
    }

    private companion object {

        fun now(): String =
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm, MMM d"))

        fun defaultThreads(): List<MessageThread> {
            return listOf(

                MessageThread(
                    id = "finance_office_thread",
                    topic = "School Fees",
                    participantsLabel = "Finance Office",
                    messages = mutableListOf(
                        Message(
                            id = "msg_1",
                            title = "Second Term Fees",
                            sender = "Finance Office",
                            content = "Second term fees for S4 are due by next Friday. Please ensure payment is cleared to avoid service interruption.",
                            timestamp = now(),
                            isUnread = true,
                            actions = listOf(
                                MessageAction("Mark as Paid", "mark_paid"),
                                MessageAction("Need Help", "help")
                            )
                        ),
                        Message(
                            id = "msg_2",
                            title = "Payment Received",
                            sender = "Finance Office",
                            content = "We have received your payment for the transport fee. Thank you.",
                            timestamp = "10:30, Jul 1",
                            isUnread = false
                        )
                    )
                ),

                MessageThread(
                    id = "admin_office_thread",
                    topic = "General Announcements",
                    participantsLabel = "School Administration",
                    messages = mutableListOf(
                        Message(
                            id = "msg_3",
                            title = "General Assembly",
                            sender = "Head Teacher",
                            content = "The annual general assembly will take place on July 15th at 10:00 AM in the main hall.",
                            timestamp = now(),
                            isUnread = false,
                            actions = listOf(
                                MessageAction("Yes", "yes"),
                                MessageAction("No", "no"),
                                MessageAction("Not Sure", "not_sure")
                            )
                        )
                    )
                ),

                MessageThread(
                    id = "academic_office_thread",
                    topic = "Academic Updates",
                    participantsLabel = "Academic Office",
                    messages = mutableListOf(
                        Message(
                            id = "msg_4",
                            title = "Mock Exams",
                            sender = "Class Teacher",
                            content = "Mock exams for S4 students start on Monday.",
                            timestamp = now(),
                            isUnread = true
                        )
                    )
                )
            )
        }
    }
}