package com.schoolbridge.v2.domain.academic

enum class AttendanceStatus {
    PRESENT,
    ABSENT,
    PENDING,           // Marked but undecided (e.g., verification in progress)
    UNMARKED           // Not marked yet
}