package com.schoolbridge.v2.data.repository.interfaces

import com.schoolbridge.v2.domain.messaging.Alert

interface AlertRepository {
    suspend fun getAlerts(): List<Alert>
    suspend fun markAsRead(alert: Alert)
    suspend fun markAllAsRead(alerts: List<Alert>)
}
