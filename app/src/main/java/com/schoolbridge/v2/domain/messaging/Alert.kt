package com.schoolbridge.v2.domain.messaging

import java.time.LocalDateTime

data class Alert(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: LocalDateTime,
    val isRead: Boolean = getRandomBoolean(),
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
fun getRandomBoolean(): Boolean {
    return (0..1).random() == 1
}
