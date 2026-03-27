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
}
