package com.schoolbridge.v2.domain.academic

enum class CourseStatus {
    VALIDATED,           // Student completed and passed
    NOT_VALIDATED,       // Student failed
    IN_PROGRESS,         // Ongoing
    RETAKE_REQUIRED,     // Failed and required to retake
    AWAITING_RESULTS     // Finished but not yet validated
}