package com.schoolbridge.v2.domain.messaging


import android.util.Log
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
    val lastDate: LocalDateTime,
    val isSystem: Boolean? = false
) {
    val unreadCount: Int
        get() = messages.count { !it.isRead }

    val lastMessage: Message?
        get() = messages.maxByOrNull { it.timestamp }

    val isUnread: Boolean
        get() = unreadCount > 0
}

/* ---------- IN‑MEMORY REPOSITORY ---------------------------------------- */

class MessageThreadRepository {

    /* Single source of truth */
    private val _threads = MutableStateFlow(generateSampleThreads())
    val threads: StateFlow<List<MessageThread>> = _threads  // expose read‑only

    suspend fun getThreadById(id: String): MessageThread? {
        for (thread in _threads.value) {
            if (thread.id == id) {
                return thread
            } else {
                Log.d("MESSAGE_THREAD", "${thread.id} is different from $id")
            }
        }
        return null
    }

    fun markThreadAsRead(id: String) {
        _threads.value = _threads.value.map { thread ->
            if (thread.id != id) thread
            else thread.copy(messages = thread.messages.map { it.copy(isRead = true) })
        }
    }

    private fun generateSampleThreads(): List<MessageThread> {
        val now = LocalDateTime.now()
        fun msg(sender: String, text: String, hoursAgo: Long,
                atts: List<String> = emptyList(), read: Boolean = false) =
            Message(sender = sender, content = text, timestamp = now.minusHours(hoursAgo),
                attachments = atts, isRead = read)

        /* regular threads ---------------------------------------------------- */
        val term2  = listOf(
            msg("Mr Bukuru (Math)", "Hello parents, find attached the marks for your children.", 27,
                atts = listOf("Term2_Marks.pdf")),
            msg("Laura N.", "Thank you for the update, Mr Bukuru.", 25, read = true)
        )
        val umuganda  = listOf(msg("Headmaster",
            "Reminder: School closes next Friday for Umuganda.", 55))
        val infirmary = listOf(
            msg("Nurse’s Office",
                "Your child visited the infirmary today with a mild headache.", 4),
            msg("You", "Thanks. Please keep me posted if anything changes.", 2, read = true),
            msg("Nurse’s Office", "Will do. He seems fine now.", 1)
        )
        val library   = listOf(
            msg("Librarian", "Two of your books are due tomorrow.", 20, read = true),
            msg("You", "I'll remind my child, thanks.", 18, read = true)
        )

        /* system‑only thread -------------------------------------------------- */
        val maintenance = listOf(
            Message(
                id       = "sys‑msg‑1",
                sender   = "System",
                content  = "⚙️  The platform will be down for maintenance tonight "
                        + "from 22:00 to 01:00.",
                timestamp = now.minusHours(6),
                attachments = emptyList(),
                isRead = false
            )
        )

        /* helper */
        fun thread(id: String, subject: String, who: List<String>, msgs: List<Message>,
                   system: Boolean = false) = MessageThread(
            id = id,
            subject = subject,
            participants = who,
            messages = msgs,
            lastSnippet = msgs.last().content.take(60),
            lastDate    = msgs.last().timestamp,
            isSystem    = system
        )

        return listOf(
            thread("thread1", "Term 2 Marks",        listOf("Mr Bukuru", "Parents S4 MCB"), term2),
            thread("thread2", "School Closure – Umuganda", listOf("Headmaster", "All Parents"), umuganda),
            thread("thread3", "Infirmary Visit",     listOf("Nurse’s Office", "You"), infirmary),
            thread("thread4", "Library Due Date",    listOf("Librarian", "You"), library),
            thread("sys1",   "System Maintenance",   listOf("System"), maintenance, system = true)
        )
    }

}

fun randomBool(): Boolean {
    return (0..1).random() == 1
}