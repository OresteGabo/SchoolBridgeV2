package com.schoolbridge.v2.data.repository.interfaces

import com.schoolbridge.v2.data.dto.message.MobileMessageConversationDto

interface MessagingRepository {
    suspend fun getMessageConversations(): List<MobileMessageConversationDto>
    suspend fun sendMessage(conversationId: Long, senderId: Long, content: String)
    suspend fun markMessageAsRead(messageId: Long, userId: Long)
}
