package com.schoolbridge.v2.usecase.messaging

import com.schoolbridge.v2.data.repository.interfaces.MessagingRepository
import com.schoolbridge.v2.domain.messaging.MessageThread
import com.schoolbridge.v2.mapper.messaging.toDomain

class GetMessagesUseCase(
    private val messagingRepository: MessagingRepository
) {
    suspend operator fun invoke(
        userId: String,
        onlyActive: Boolean = true
    ): List<MessageThread> {
        return messagingRepository
            .getMessageThreadsForUser(userId = userId, isActive = onlyActive)
            .map { it.toDomain() }
    }
}
