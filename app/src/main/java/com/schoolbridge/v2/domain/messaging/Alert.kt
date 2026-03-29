package com.schoolbridge.v2.domain.messaging

import java.time.LocalDateTime

data class Alert(
    val id: String,
    val conversationId: String? = null,
    val latestMessageId: String? = null,
    val title: String,
    val message: String,
    val timestamp: LocalDateTime,
    val type: AlertType,
    val isRead: Boolean = false,
    val publisherName: String,
    val publisherType: AlertSourceType,
    val severity: AlertSeverity,
    val studentName: String? = null,
    val sourceOrganization: String? = null,
    val unreadMessageIds: List<String> = emptyList()
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
