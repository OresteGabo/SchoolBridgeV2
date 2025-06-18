package com.schoolbridge.v2.domain.messaging

enum class AlertType {
    INFO,
    WARNING,
    ERROR,
    SUCCESS,
    ANNOUNCEMENT,
    REMINDER,
    NOTICE
}

fun AlertType.prettyName(): String = when (this) {
    AlertType.ANNOUNCEMENT -> "Announcement"
    AlertType.REMINDER     -> "Reminder"
    AlertType.WARNING      -> "Warning"
    AlertType.NOTICE       -> "Notice"
    AlertType.ERROR        -> "Error"
    AlertType.INFO         -> "Info"
    AlertType.SUCCESS      -> "Success"
}
