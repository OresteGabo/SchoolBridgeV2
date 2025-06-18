package com.schoolbridge.v2.domain.messaging


import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime
import java.util.UUID

/**
 * A conversation that groups messages by subject and participants.
 */
data class MessageThread(
    val id: String,
    val subject: String,
    val participants: List<String>, // Names or emails
    val messages: List<Message>,
    val lastSnippet: String,
    val lastDate: LocalDateTime,
    val isSystem: Boolean = false
) {
    val unreadCount: Int
        get() = messages.count { !it.isRead }

    val isUnread: Boolean
        get() = unreadCount > 0

    val lastMessage: Message?
        get() = messages.maxByOrNull { it.timestamp }
}

/**
 * A pending invitation to join a thread.
 */
data class ThreadInvite(
    val id: String,
    val subject: String,
    val from: String,
    val receivedAt: LocalDateTime
)

class MessageThreadRepository {

    /* ───── State flows ─────────────────────────────── */
    private val _threads = MutableStateFlow(generateSampleThreads())
    val threads: StateFlow<List<MessageThread>> = _threads

    private val _invites = MutableStateFlow(sampleInvites())
    val invites: StateFlow<List<ThreadInvite>> = _invites

    /* ───── Public API ──────────────────────────────── */

    suspend fun getThreadById(id: String): MessageThread? {
        return _threads.value.find { it.id == id }
    }

    fun markThreadAsRead(id: String) {
        _threads.value = _threads.value.map { thread ->
            if (thread.id != id) thread
            else thread.copy(messages = thread.messages.map { it.copy(isRead = true) })
        }
    }

    fun acceptInvite(inviteId: String, newThread: MessageThread) {
        _invites.value = _invites.value.filterNot { it.id == inviteId }
        _threads.value = _threads.value + newThread
    }

    fun declineInvite(inviteId: String) {
        _invites.value = _invites.value.filterNot { it.id == inviteId }
    }

    /* ───── Sample Data ─────────────────────────────── */

    private fun generateSampleThreads(): List<MessageThread> {
        val now = LocalDateTime.now()

        fun msg(sender: String, text: String, hoursAgo: Long, atts: List<String> = emptyList(), read: Boolean = false) =
            Message(sender = sender, content = text, timestamp = now.minusHours(hoursAgo), attachments = atts, isRead = read)

        val term2 = listOf(
            msg("Mr Bukuru (Math)", "Hello parents, find attached the marks for your children.", 27, listOf("Term2_Marks.pdf")),
            msg("Laura N.", "Thank you for the update, Mr Bukuru.", 25, read = true)
        )

        val umuganda = listOf(msg("Headmaster", "Reminder: School closes next Friday for Umuganda.", 55))

        val infirmary = listOf(
            msg("Nurse’s Office", "Your child visited the infirmary today with a mild headache.", 4),
            msg("You", "Thanks. Please keep me posted if anything changes.", 2, read = true),
            msg("Nurse’s Office", "Will do. He seems fine now.", 1),
        ).repeat(15)

        val library = listOf(
            msg("Librarian", "Two of your books are due tomorrow.", 20, read = true),
            msg("You", "I'll remind my child, thanks.", 18, read = true)
        ) + infirmary.take(12)

        val maintenance = listOf(
            Message(
                id = "sys-msg-1",
                sender = "System",
                content = "⚙️ Platform will be down for maintenance tonight from 22:00 to 01:00.",
                timestamp = now.minusHours(6),
                attachments = emptyList(),
                isRead = false
            )
        )

        fun thread(id: String, subject: String, who: List<String>, msgs: List<Message>, system: Boolean = false) = MessageThread(
            id = id,
            subject = subject,
            participants = who,
            messages = msgs,
            lastSnippet = msgs.last().content.take(60),
            lastDate = msgs.last().timestamp,
            isSystem = system
        )

        return listOf(
            thread("thread1", "Term 2 Marks", listOf("Mr Bukuru", "Parents S4 MCB"), term2),
            thread("thread2", "School Closure – Umuganda", listOf("Headmaster", "All Parents"), umuganda),
            thread("thread3", "Infirmary Visit", listOf("Nurse’s Office", "You"), infirmary),
            thread("thread4", "Library Due Date", listOf("Librarian", "You"), library),
            thread("sys1", "System Maintenance", listOf("System"), maintenance, system = true)
        )
    }

    private fun sampleInvites(): List<ThreadInvite> {
        val now = LocalDateTime.now()
        return listOf(
            ThreadInvite(
                id = "invite1",
                subject = "Disciplinary Committee Feedback",
                from = "Discipline Office",
                receivedAt = now.minusHours(3)
            ),
            ThreadInvite(
                id = "invite2",
                subject = "Parent Feedback on Club Day",
                from = "Mr. Uwizeye",
                receivedAt = now.minusDays(1)
            )
        )
    }

    /* ───── Helpers ─────────────────────────────────── */

    private fun <T> List<T>.repeat(n: Int): List<T> =
        List(n) { this }.flatten()
}
