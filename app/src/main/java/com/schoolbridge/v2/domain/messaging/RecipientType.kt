package com.schoolbridge.v2.domain.messaging

enum class RecipientType {
    ALL_USERS,
    ALL_PARENTS,
    ALL_STUDENTS,
    ALL_STAFF,
    CLASSROOM,
    INDIVIDUAL;

    companion object {
        fun fromRaw(raw: String?): RecipientType =
            entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: ALL_USERS
    }
}
