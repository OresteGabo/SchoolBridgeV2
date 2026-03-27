package com.schoolbridge.v2.domain.messaging

data class ThreadCallInfo(
    val type: ThreadCallType,
    val purpose: ThreadCallPurpose,
    val status: ThreadCallStatus,
    val hostLabel: String,
    val participantSummary: String,
    val scheduledLabel: String? = null,
    val durationLabel: String? = null,
    val note: String? = null
)

enum class ThreadCallType {
    AUDIO,
    VIDEO,
    LIVE_ANNOUNCEMENT
}

enum class ThreadCallPurpose {
    GENERAL,
    ROLE_VERIFICATION,
    FINANCE_ESCALATION,
    DISCIPLINE_ESCALATION,
    ANNOUNCEMENT
}

enum class ThreadCallStatus {
    REQUESTED,
    SCHEDULED,
    RINGING,
    ACTIVE,
    ENDED,
    MISSED,
    DECLINED,
    NEEDS_DOCUMENTS
}
