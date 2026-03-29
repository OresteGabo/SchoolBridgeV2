package com.schoolbridge.v2.domain.messaging

data class ConversationCallInfo(
    val type: ConversationCallType,
    val purpose: ConversationCallPurpose,
    val status: ConversationCallStatus,
    val hostLabel: String,
    val participantSummary: String,
    val scheduledLabel: String? = null,
    val durationLabel: String? = null,
    val note: String? = null
)

enum class ConversationCallType {
    AUDIO,
    VIDEO,
    LIVE_ANNOUNCEMENT
}

enum class ConversationCallPurpose {
    GENERAL,
    ROLE_VERIFICATION,
    FINANCE_ESCALATION,
    DISCIPLINE_ESCALATION,
    ANNOUNCEMENT
}

enum class ConversationCallStatus {
    REQUESTED,
    SCHEDULED,
    RINGING,
    ACTIVE,
    ENDED,
    MISSED,
    DECLINED,
    NEEDS_DOCUMENTS
}
