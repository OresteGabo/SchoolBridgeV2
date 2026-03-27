package com.schoolbridge.v2.usecase.messaging

import com.schoolbridge.v2.data.repository.interfaces.MessagingRepository
import com.schoolbridge.v2.domain.messaging.Message
import com.schoolbridge.v2.mapper.messaging.toDomain
import com.schoolbridge.v2.mapper.messaging.toDto

class SendMessageUseCase(
    private val messagingRepository: MessagingRepository
) {
    suspend operator fun invoke(threadId: String, message: Message): Message {
        return messagingRepository
            .sendMessage(threadId = threadId, message = message.toDto())
            .toDomain()
    }
}
