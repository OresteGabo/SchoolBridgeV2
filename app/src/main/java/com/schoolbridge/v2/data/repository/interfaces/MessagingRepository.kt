package com.schoolbridge.v2.data.repository.interfaces

import com.schoolbridge.v2.data.dto.message.MobileMessageThreadDto

interface MessagingRepository {
    suspend fun getMessageThreads(): List<MobileMessageThreadDto>
}
