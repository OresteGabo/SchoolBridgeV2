package com.schoolbridge.v2.domain.academic.evaluation

import java.time.LocalDateTime

/**
 * Represents a student's Grade achieved on a specific [Evaluation].
 * This records the performance outcome for an assessment.
 *
 * @property id Unique identifier for the grade record.
 * @property evaluationId The ID of the [Evaluation] this grade is for.
 * @property studentId The ID of the [User] (specifically a student) who received this grade.
 * @property scoreAchieved The raw score the student achieved.
 * @property maxScorePossible The maximum possible score for the associated [Evaluation].
 * @property percentageScore The score achieved expressed as a percentage.
 * @property gradeLetter An optional letter grade (e.g., "A", "B+", "Pass").
 * @property comments Any optional comments from the teacher regarding the grade.
 * @property dateRecorded The date and time when the grade was officially recorded.
 * @property recordedByTeacherUserId The ID of the [User] (teacher) who recorded this grade.
 *
 * Example Usage:
 * val studentGrade = Grade(
 * id = "grade-std-123-eval-math-final",
 * evaluationId = "eval-math-final",
 * studentId = "user-student-id-abc",
 * scoreAchieved = 85.5,
 * maxScorePossible = 100.0,
 * percentageScore = 85.5,
 * gradeLetter = "A",
 * comments = "Excellent work on problem-solving!",
 * dateRecorded = LocalDateTime.of(2025, 5, 25, 14, 30),
 * recordedByTeacherUserId = "user-teacher-id-123"
 * )
 */
data class Grade(
    val id: String,
    val evaluationId: String,
    val studentId: String,
    val scoreAchieved: Double,
    val maxScorePossible: Double,
    val percentageScore: Double,
    val gradeLetter: String?,
    val comments: String?,
    val dateRecorded: LocalDateTime,
    val recordedByTeacherUserId: String
)