package com.schoolbridge.v2.domain.user

data class NationalExamResult(
    val id: String,
    val studentId: String,
    val academicYearId: String,
    val examName: String, // e.g., "Primary Leaving Exam", "National Exam"
    val subjectResults: Map<String, Int>, // Subject name to score
    val totalScore: Int,
    val grade: String?, // e.g., "Division One"
    val resultDate: String // Or LocalDate
)