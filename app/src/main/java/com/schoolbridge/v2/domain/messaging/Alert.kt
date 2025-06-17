package com.schoolbridge.v2.domain.messaging

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.schoolbridge.v2.ui.AlertRepository
import java.time.LocalDateTime

data class Alert(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: LocalDateTime,
    val type: AlertType,
    val isRead: Boolean = false,
    val publisherName: String,
    val publisherType: AlertSourceType,
    val severity: AlertSeverity,
    val studentName: String? = null,
    val sourceOrganization: String? = null
) {
    val source: String
        get() = when {
            publisherType == AlertSourceType.SCHOOL && studentName != null && sourceOrganization != null ->
                "$publisherName of $studentName at $sourceOrganization"
            publisherType == AlertSourceType.SCHOOL && sourceOrganization != null ->
                "$publisherName at $sourceOrganization"
            sourceOrganization != null ->
                "$publisherName from $sourceOrganization"
            else -> publisherName
        }
}



/**
 * Who generated the alert.
 */
enum class AlertSourceType { SCHOOL, MINISTRY, CLUB, SYSTEM }

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
    fun markAsUnread(id: String) = repository.markAsUnread(id)
    fun markAllAsRead()      = repository.markAllAsRead()
}

fun getRandomAlertType(): AlertType {
    val values = AlertType.entries.toTypedArray()
    return values.random()

}


