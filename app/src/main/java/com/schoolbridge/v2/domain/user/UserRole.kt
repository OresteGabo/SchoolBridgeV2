package com.schoolbridge.v2.domain.user

//package com.schoolbridge.v2.data.enums // Adjust your package as needed

import com.google.gson.annotations.SerializedName // Keep if you're using Gson for serialization

/**
 * Defines the various roles a user can have within the school system,
 * along with a descriptive overview of what each role entails.
 */
enum class UserRole(
    val humanLabel: String, // This is the new property for the human-readable label
    @SerializedName("value") val backendValue: String, // Optional: if you need a specific backend value string
    val description: String // This is the new property for the description
) {
    STUDENT(
        "Student",
        "STUDENT",
        "Access your courses, timetable, assignments, and school alerts. Take ownership of your education."
    ),
    TEACHER(
        "Teacher",
        "TEACHER",
        "Manage your subjects, share materials, track attendance, and communicate with students and parents."
    ),
    PARENT(
        "Parent",
        "PARENT",
        "Monitor your children’s grades, attendance, messages, and financial status. Stay informed and involved."
    ),
    SCHOOL_ADMIN(
        "School admin",
        "SCHOOL_ADMIN",
        "Oversee school operations, manage staff and student data, and monitor academic and financial records."
    ),
    GUEST(
        "Guest",
        "GUEST",
        "Access limited public content and explore the system before registration." // Added description for GUEST
    );

    // Optional: Companion object for utility functions, similar to your UserVerificationStatus enum
    companion object {
        fun fromBackendValue(backendValue: String): UserRole {
            return entries.firstOrNull { it.backendValue == backendValue }
                ?: throw IllegalArgumentException("Unknown UserRole backend value: $backendValue")
        }
    }
}