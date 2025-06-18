package com.schoolbridge.v2.domain.messaging


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime
import java.util.UUID

/**
 * A conversation that groups messages by subject / participants.
 */
data class MessageThread(
    val id: String,
    val subject: String,
    val participants: List<String>, // names or emails
    val messages: List<Message>,
    val lastSnippet: String,
    val lastDate: LocalDateTime
) {
    val unreadCount: Int
        get() = messages.count { !it.isRead }

    val lastMessage: Message?
        get() = messages.maxByOrNull { it.timestamp }

    val isUnread: Boolean
        get() = unreadCount > 0
}

/* ---------- IN‑MEMORY REPOSITORY ---------------------------------------- */

class ThreadRepository {

    /* single source of truth */
    private val _threads = MutableStateFlow(generateSampleThreads())
    val threads: StateFlow<List<MessageThread>> = _threads  // expose read‑only

    /* --------------------------------------------------------------------- */
    fun getThread(id: String): MessageThread? =
        _threads.value.firstOrNull { it.id == id }

    fun markThreadAsRead(id: String) {
        _threads.value = _threads.value.map { thread ->
            if (thread.id != id) thread
            else thread.copy(messages = thread.messages.map { it.copy(isRead = true) })
        }
    }

    /* --------------------------------------------------------------------- */
    private fun generateSampleThreads(): List<MessageThread> {
        val now = LocalDateTime.now()

        fun msg(
            sender: String,
            text: String,
            hoursAgo: Long,
            attachments: List<String> = emptyList(),
            read: Boolean = false
        ) = Message(
            sender = sender,
            content = text,
            timestamp = now.minusHours(hoursAgo),
            attachments = attachments,
            isRead = read
        )

        val t1Msgs = listOf(
            msg("Mr Bukuru (Math)", "Hello parents, find attached the marks for your children.",
                27, attachments = listOf("Term2_Marks.pdf")),
            msg("Laura N.", "Thank you for the update, Mr Bukuru.", 25, read = true)
        )

        val t2Msgs = listOf(
            msg("Headmaster", "Reminder: School closed next Friday for Umuganda.", 55)
        )

        val t3Msgs = listOf(
            msg("Nurse’s Office", "Your child visited the infirmary today with a mild headache.", 4),
            msg("You", "Thanks. Please keep me posted if anything changes.", 2, read = true),
            msg("Nurse’s Office", "Will do. He seems fine now.", 1)
        )

        /* NEW sample threads ------------------------------------------------ */

        val t4Msgs = listOf(
            msg("Librarian", "Two of your books are due tomorrow.", 20, read = true),
            msg("You", "I’ll remind my child, thanks.", 18, read = true),
            msg("Librarian", "Two of your books are due tomorrow.", 20, read = true),
            msg("You", "I’ll remind my child, thanks.", 18, read = true),
            msg("Librarian", "Two of your books are due tomorrow.", 20, read = true),
            msg("You", "I’ll remind my child, thanks.", 18, read = true)
        )

        val t5Msgs = listOf(
            msg("PE Dept.", "Inter‑school sports day schedule attached.",
                10, attachments = listOf("SportsDay_Schedule.pdf")),
            msg("Coach M.", "Please ensure students have proper gear.", 8)
        )

        fun thread(
            subject: String,
            participants: List<String>,
            msgs: List<Message>
        ) = MessageThread(
            id = UUID.randomUUID().toString(),
            subject = subject,
            participants = participants,
            messages = msgs,
            lastSnippet = msgs.last().content.take(60),
            lastDate = msgs.last().timestamp
        )

        return listOf(
            thread("Term 2 Marks", listOf("Mr Bukuru", "Parents of S4 MCB"), t1Msgs),
            thread("School Closure – Umuganda", listOf("Headmaster", "All Parents"), t2Msgs),
            thread("Infirmary Visit", listOf("Nurse’s Office", "You"), t3Msgs),
            thread("Library Due Date", listOf("Librarian", "You"), t4Msgs),
            thread("Inter‑school Sports Day", listOf("PE Dept.", "Coach M.", "You"), t5Msgs)
        )
    }
}