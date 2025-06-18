package com.schoolbridge.v2.domain.messaging

import com.schoolbridge.v2.domain.messaging.Message
import java.time.LocalDateTime

/**
 * A conversation that groups messages by subject / participants.
 */
data class MessageThread(
    val id: String,
    val subject: String,
    val participants: List<String>,          // names or e‑mails
    //val messages: List<Message>,
    val lastSnippet: String,
    val lastDate: LocalDateTime,
    val unreadCount: Int                     // 0 ⇒ all read
)
/* quick fake repository; replace with DB / network later */
object ThreadRepository {
    fun getThreads(): List<MessageThread> = listOf(
        MessageThread(
            "t1", "Holiday Homework", listOf("Mr. Habimana", "You"),
            "Please remember to …", LocalDateTime.now().minusHours(2), 2
        ),
        MessageThread(
            "t2", "Fee Receipt – Term 2", listOf("Finance Dept.", "You"),
            "Attached is your receipt …", LocalDateTime.now().minusDays(1), 0
        ),
        MessageThread(
            "t3", "Science‑Fair Volunteers", listOf("Mme. Uwase", "You", "Parent Group"),
            "Can we meet Tuesday?", LocalDateTime.now().minusDays(6), 5
        )
    )
}