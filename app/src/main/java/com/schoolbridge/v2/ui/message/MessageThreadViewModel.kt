package com.schoolbridge.v2.ui.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolbridge.v2.domain.messaging.Message
import com.schoolbridge.v2.domain.messaging.MessageAction
import com.schoolbridge.v2.domain.messaging.MessageThread
import com.schoolbridge.v2.domain.messaging.ThreadMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class MessageThreadViewModel : ViewModel() {

    private val _messageThreads = MutableStateFlow(defaultThreads())
    val messageThreads: StateFlow<List<MessageThread>> = _messageThreads

    fun addUserMessage(
        threadId: String,
        content: String,
        replyToId: String? = null,
        replyToContent: String? = null,
        replyToSender: String? = null
    ) {
        viewModelScope.launch {
            val newMessage = Message(
                id = UUID.randomUUID().toString(),
                sender = "You",
                content = formatUserReply(content),
                timestamp = now(),
                title = null,
                status = null,
                actions = emptyList(),
                isUnread = false,
                // These are the magic fields for the WhatsApp effect
                replyToId = replyToId,
                replyToContent = replyToContent,
                replyToSender = replyToSender
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

    /**
     * IMPROVED ACTION HANDLING
     * Note: We don't call addUserMessage here anymore because it's handled
     * by the UI navigation shell to ensure proper context passing.
     */
    fun performAction(threadId: String, messageId: String, actionId: String) {
        viewModelScope.launch {
            val updatedThreads = _messageThreads.value.map { thread ->
                if (thread.id == threadId) {
                    val updatedMessages = thread.messages.map { msg ->
                        if (msg.id == messageId) {
                            // Determine the new status based on actionId
                            val newStatus = when (actionId) {
                                "mark_paid" -> "Marked as Paid"
                                "acknowledge" -> "Acknowledged"
                                "yes" -> "Confirmed"
                                "no" -> "Declined"
                                "not_sure" -> "Pending"
                                "pay_bill" -> msg.status // Don't change status yet; wait for payment success
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

    private fun formatUserReply(actionLabel: String): String {
        return when (actionLabel.lowercase()) {
            "yes" -> "Yes, I'll be there"
            "no" -> "No, I can't make it"
            "not sure" -> "I am not sure yet"
            "mark as paid" -> "I've completed the payment"
            "acknowledge" -> "I have seen this notice"
            else -> actionLabel
        }
    }

    private companion object {
        // UPDATED: Now accepts an optional long to subtract minutes for variety
        fun now(minusMinutes: Long = 0): String =
            LocalDateTime.now()
                .minusMinutes(minusMinutes)
                .format(DateTimeFormatter.ofPattern("HH:mm, MMM d"))

        fun defaultThreads(): List<MessageThread> {
            val threads = mutableListOf<MessageThread>()

            // 1. FINANCE: The "Payment & Invoice" Scenario
            threads.add(MessageThread(
                id = "fin_001",
                topic = "Tuition & Transport",
                participantsLabel = "Finance Office",
                mode = ThreadMode.ACTION_REQUIRED,
                messages = mutableListOf(
                    Message(
                        id = "m1",
                        title = "Quarterly Invoice",
                        sender = "Finance Office",
                        content = "Invoice #882 for Term 3 is ready. Total: 250,000 RWF.",
                        timestamp = now(1440),
                        actions = listOf(
                            // UPDATED: Generic label, specific "pay_bill" ID for the UI logic
                            MessageAction("Pay Fees", "pay_bill"),
                            MessageAction("Already Paid", "mark_paid")
                        )
                    ),
                    Message(
                        id = "m2",
                        sender = "You",
                        content = "I've completed the payment via Momo.",
                        timestamp = now(1300), // ~21 hours ago
                        replyToId = "m1",
                        replyToContent = "Invoice #882 for Term 3 is ready...",
                        replyToSender = "Finance Office",
                        status = "Confirmed"
                    ),
                    Message(
                        id = "m3",
                        title = "Receipt Confirmation",
                        sender = "Finance Office",
                        content = "Payment received for Invoice #882. Thank you!",
                        timestamp = now(1200)
                    )
                )
            ))

            // 2. LOGISTICS: The "Field Trip Permission" Scenario
            threads.add(MessageThread(
                id = "log_002",
                topic = "School Trip: Akagera Park",
                participantsLabel = "Grade 5 Coordinator",
                mode = ThreadMode.ANNOUNCEMENT,
                messages = mutableListOf(
                    Message(
                        id = "trip_1",
                        title = "Permission Required",
                        sender = "Grade 5 Coordinator",
                        content = "We are visiting Akagera National Park this Friday. Does your child have permission to join the bus?",
                        timestamp = now(600),
                        actions = listOf(
                            MessageAction("Yes, Permission Granted", "yes"),
                            MessageAction("No, Stay Home", "no")
                        )
                    )
                )
            ))

            // 3. DISCIPLINE: The "Attendance Alert" Scenario
            threads.add(MessageThread(
                id = "disc_003",
                topic = "Attendance Notification",
                participantsLabel = "Dean of Students",
                mode = ThreadMode.ACTION_REQUIRED,
                messages = mutableListOf(
                    Message(
                        id = "att_1",
                        title = "Late Arrival",
                        sender = "System",
                        content = "Your child arrived at 8:45 AM today (30 mins late). Please acknowledge this notice.",
                        timestamp = now(120),
                        actions = listOf(MessageAction("Acknowledge", "acknowledge"))
                    )
                )
            ))

            // 4. GENERATING 30+ RANDOM NOTICES FOR SCROLL TESTING
            for (i in 1..30) {
                val dept = listOf("Sports", "Library", "Cafeteria", "IT Lab").random()
                threads.add(MessageThread(
                    id = "gen_$i",
                    topic = "$dept Update #$i",
                    participantsLabel = "$dept Dept",
                    mode = ThreadMode.CONVERSATION,
                    messages = mutableListOf(
                        Message(
                            id = "msg_gen_$i",
                            sender = "Staff",
                            content = "This is a routine update regarding $dept equipment and schedules for the upcoming week.",
                            timestamp = now((i * 45).toLong()), // Spaced out by 45 mins each
                            isUnread = i < 5
                        )
                    )
                ))
            }

            return threads
        }
    }

}
