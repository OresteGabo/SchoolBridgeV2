package com.schoolbridge.v2.data.repository.implementations

import com.schoolbridge.v2.data.dto.message.MobileMessageConversationDto
import com.schoolbridge.v2.data.remote.MessageApiService
import com.schoolbridge.v2.data.repository.interfaces.MessagingRepository

class MessagingRepositoryImpl(
    private val messageApiService: MessageApiService
) : MessagingRepository {
    override suspend fun getMessageConversations(): List<MobileMessageConversationDto> {
        return messageApiService.getMessageConversations()
    }

    override suspend fun sendMessage(conversationId: Long, senderId: Long, content: String) {
        messageApiService.sendMessage(conversationId, senderId, content)
    }

    override suspend fun markMessageAsRead(messageId: Long, userId: Long) {
        messageApiService.markMessageAsRead(messageId, userId)
    }
}
