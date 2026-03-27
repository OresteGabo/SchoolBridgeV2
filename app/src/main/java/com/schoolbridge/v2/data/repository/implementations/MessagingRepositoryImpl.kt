package com.schoolbridge.v2.data.repository.implementations

import com.schoolbridge.v2.data.dto.message.MobileMessageThreadDto
import com.schoolbridge.v2.data.remote.MessageApiService
import com.schoolbridge.v2.data.repository.interfaces.MessagingRepository

class MessagingRepositoryImpl(
    private val messageApiService: MessageApiService
) : MessagingRepository {
    override suspend fun getMessageThreads(): List<MobileMessageThreadDto> {
        return messageApiService.getMessageThreads()
    }

    override suspend fun sendMessage(conversationId: Long, senderId: Long, content: String) {
        messageApiService.sendMessage(conversationId, senderId, content)
    }

    override suspend fun markMessageAsRead(messageId: Long, userId: Long) {
        messageApiService.markMessageAsRead(messageId, userId)
    }
}
