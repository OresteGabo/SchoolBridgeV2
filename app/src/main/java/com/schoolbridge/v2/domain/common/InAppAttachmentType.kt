package com.schoolbridge.v2.domain.common

/**
 * Defines the types of entities that can be attached within the application.
 * These are "in-app" attachments, meaning they reference other structured data
 * within the system rather than external files like PDFs or images.
 */
enum class InAppAttachmentType {
    CHAPTER,
    COURSE,
    EXERCISE,
    EVALUATION, // Reference to a specific evaluation
    USER_PROFILE, // Reference to a user's profile (e.g., Teacher, Student, Parent)
    SCHOOL_ANNOUNCEMENT, // Reference to a school-wide announcement
    // TODO: Add more types as needed (e.g., ATTENDANCE_RECORD, PAYMENT_RECEIPT)
}