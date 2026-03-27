package com.schoolbridge.v2.domain.messaging

enum class ThreadMode {
    ANNOUNCEMENT,      // Read-only
    ACTION_REQUIRED,   // Read-only + action buttons
    CONVERSATION,      // Replies allowed
    DIRECT_CONTACT     // Private 1:1
}
