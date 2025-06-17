package com.schoolbridge.v2.domain.messaging

import androidx.lifecycle.ViewModel
import com.schoolbridge.v2.ui.AlertRepository
import java.time.LocalDateTime

data class Alert(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: LocalDateTime,
    val isRead: Boolean = false,
    val type: AlertType = AlertType.INFO,
    val severity: AlertSeverity = getRandomAlertSeverity()
)

enum class AlertSeverity {
    LOW,       // Minor alerts, informational only
    MEDIUM,    // Moderate alerts, should be noticed
    HIGH       // Critical alerts, urgent attention needed
}


fun getRandomAlertSeverity(): AlertSeverity {
    val values = AlertSeverity.entries.toTypedArray()
    return values.random()
}

class AlertsViewModel(
    private val repository: AlertRepository
) : ViewModel() {

    // secondary no‑arg ctor that delegates to the main one
    constructor() : this(AlertRepository())

    val alerts = repository.alerts            // StateFlow<List<Alert>>
    fun markAsRead(id: String) = repository.markAsRead(id)
    fun markAllAsRead()      = repository.markAllAsRead()
}



