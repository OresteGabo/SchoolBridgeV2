package com.schoolbridge.v2.data.repository.implementations

import com.schoolbridge.v2.data.dto.message.MobileMessageDto
import com.schoolbridge.v2.data.dto.message.MobileMessageThreadDto
import com.schoolbridge.v2.data.repository.interfaces.AlertRepository
import com.schoolbridge.v2.data.repository.interfaces.MessagingRepository
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.domain.messaging.Alert
import com.schoolbridge.v2.domain.messaging.AlertSeverity
import com.schoolbridge.v2.domain.messaging.AlertSourceType
import com.schoolbridge.v2.domain.messaging.AlertType
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

class AlertRepositoryImpl(
    private val messagingRepository: MessagingRepository,
    private val userSessionManager: UserSessionManager
) : AlertRepository {

    override suspend fun getAlerts(): List<Alert> {
        return messagingRepository.getMessageThreads()
            .mapNotNull { it.toAlertOrNull() }
            .sortedByDescending { it.timestamp }
    }

    override suspend fun markAsRead(alert: Alert) {
        val currentUserId = userSessionManager.currentUser.value?.userId?.toLongOrNull() ?: return
        alert.unreadMessageIds.forEach { rawId ->
            rawId.toLongOrNull()?.let { messageId ->
                runCatching {
                    messagingRepository.markMessageAsRead(messageId = messageId, userId = currentUserId)
                }
            }
        }
    }

    override suspend fun markAllAsRead(alerts: List<Alert>) {
        alerts.filterNot(Alert::isRead).forEach { alert ->
            markAsRead(alert)
        }
    }
}

private fun MobileMessageThreadDto.toAlertOrNull(): Alert? {
    if (!conversationType.equals("SYSTEM", ignoreCase = true)) return null

    val latestMessage = messages.lastOrNull() ?: return null
    val unreadMessageIds = messages.filter { it.isUnread }.map { it.id }
    val sourceType = when {
        latestMessage.senderUserId == null -> AlertSourceType.SYSTEM
        else -> AlertSourceType.SCHOOL
    }

    return Alert(
        id = id,
        threadId = id,
        latestMessageId = latestMessage.id,
        title = latestMessage.title?.takeIf { it.isNotBlank() } ?: topic,
        message = latestMessage.content,
        timestamp = parseAlertTimestamp(lastUpdatedAt),
        type = latestMessage.toAlertType(topic = topic),
        isRead = unreadCount == 0 && unreadMessageIds.isEmpty(),
        publisherName = latestMessage.sender.ifBlank { "SchoolBridge" },
        publisherType = sourceType,
        severity = latestMessage.toAlertSeverity(topic = topic),
        sourceOrganization = participantsLabel.takeUnless { it.equals("System", ignoreCase = true) }
            ?: "SchoolBridge Platform",
        unreadMessageIds = unreadMessageIds
    )
}

private fun parseAlertTimestamp(raw: String): LocalDateTime = runCatching {
    OffsetDateTime.parse(raw)
        .atZoneSameInstant(ZoneId.systemDefault())
        .toLocalDateTime()
}.getOrElse {
    LocalDateTime.now()
}

private fun MobileMessageDto.toAlertType(topic: String): AlertType {
    val signal = listOfNotNull(title, content, topic).joinToString(" ").lowercase()
    return when {
        actions.isNotEmpty() -> AlertType.REMINDER
        "maintenance" in signal -> AlertType.WARNING
        "approved" in signal || "confirmed" in signal || "received" in signal -> AlertType.SUCCESS
        "late" in signal || "overdue" in signal || "urgent" in signal -> AlertType.WARNING
        "error" in signal || "failed" in signal || "rejected" in signal || "cancel" in signal -> AlertType.ERROR
        "notice" in signal -> AlertType.NOTICE
        else -> AlertType.ANNOUNCEMENT
    }
}

private fun MobileMessageDto.toAlertSeverity(topic: String): AlertSeverity {
    val signal = listOfNotNull(title, content, status, topic).joinToString(" ").lowercase()
    return when {
        "urgent" in signal || "failed" in signal || "late" in signal || "overdue" in signal -> AlertSeverity.HIGH
        actions.isNotEmpty() || "maintenance" in signal || "review" in signal || "warning" in signal -> AlertSeverity.MEDIUM
        else -> AlertSeverity.LOW
    }
}
