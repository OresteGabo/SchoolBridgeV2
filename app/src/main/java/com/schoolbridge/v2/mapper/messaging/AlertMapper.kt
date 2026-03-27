package com.schoolbridge.v2.mapper.messaging

import com.schoolbridge.v2.data.dto.messaging.AlertDto
import com.schoolbridge.v2.domain.messaging.Alert
import com.schoolbridge.v2.domain.messaging.AlertSeverity
import com.schoolbridge.v2.domain.messaging.AlertSourceType
import com.schoolbridge.v2.domain.messaging.AlertType
import java.time.LocalDateTime

fun AlertDto.toDomain(): Alert = Alert(
    id = id,
    title = title,
    message = content,
    timestamp = parseDate(sentDate),
    type = AlertType.entries.firstOrNull { it.name.equals(type, ignoreCase = true) } ?: AlertType.INFO,
    isRead = isRead,
    publisherName = "SchoolBridge",
    publisherType = AlertSourceType.SYSTEM,
    severity = AlertSeverity.MEDIUM
)

private fun parseDate(raw: String): LocalDateTime = try {
    LocalDateTime.parse(raw)
} catch (_: Exception) {
    LocalDateTime.now()
}
