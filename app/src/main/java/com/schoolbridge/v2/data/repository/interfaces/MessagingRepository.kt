package com.schoolbridge.v2.data.repository.interfaces

import com.schoolbridge.v2.data.dto.message.MobileMessageThreadDto

interface MessagingRepository {
    suspend fun getMessageThreads(): List<MobileMessageThreadDto>
    suspend fun sendMessage(conversationId: Long, senderId: Long, content: String)
    suspend fun markMessageAsRead(messageId: Long, userId: Long)
}
