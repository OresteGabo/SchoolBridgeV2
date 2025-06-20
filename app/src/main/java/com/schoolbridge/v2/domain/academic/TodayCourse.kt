package com.schoolbridge.v2.domain.academic

data class TodayCourse(
    val subject: String,
    val startTime: String,
    val endTime: String,
    val teacher: String,
    val location: String,
    val attendanceStatus: AttendanceStatus = AttendanceStatus.UNMARKED
)