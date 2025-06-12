package com.schoolbridge.v2.data.repository.interfaces

import com.schoolbridge.v2.data.dto.messaging.MessageInThreadDto
import com.schoolbridge.v2.data.dto.messaging.MessageThreadDto

// You would need to import your DTOs here, e.g.:
// import com.schoolvridge.v2.data.dto.MessageThreadDto
// import com.schoolvridge.v2.data.dto.MessageInThreadDto

/**
 * Interface for the **Messaging Data Repository**.
 *
 * This repository defines the contract for accessing and managing message threads
 * and individual messages within those threads. It is designed to support a system
 * where conversations are topic-based and may have defined lifespans.
 *
 * **Typical methods it would expose:**
 * -   Retrieving lists of message threads for a user.
 * -   Fetching all messages within a specific thread.
 * -   Creating new message threads.
 * -   Sending individual messages within a thread.
 * -   Marking messages/threads as read or archived.
 */
interface MessagingRepository {

    // Message Threads
    suspend fun getMessageThreadById(threadId: String): MessageThreadDto?
    suspend fun getMessageThreadsForUser(userId: String, isActive: Boolean? = true): List<MessageThreadDto>
    suspend fun createMessageThread(newThread: MessageThreadDto): MessageThreadDto // For initiating a new topic
    suspend fun updateMessageThread(thread: MessageThreadDto): MessageThreadDto // For archiving, updating subject etc.
    suspend fun deleteMessageThread(threadId: String): Boolean // Soft delete/mark for auto-deletion

    // Messages within a Thread
    suspend fun getMessageInThreadById(messageId: String): MessageInThreadDto?
    suspend fun getMessagesInThread(threadId: String, limit: Int = 50, offset: Int = 0): List<MessageInThreadDto>
    suspend fun sendMessage(threadId: String, message: MessageInThreadDto): MessageInThreadDto
    suspend fun markMessageAsRead(messageId: String, userId: String): Boolean
    suspend fun markThreadAsRead(threadId: String, userId: String): Boolean
    // No direct update/delete for individual messages to maintain thread integrity, but you might have 'editMessage' or 'hideMessage' if supported by your rules.
}