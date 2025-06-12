package com.schoolbridge.v2.domain.academic.evaluation

import com.schoolbridge.v2.domain.common.Attachment
import java.time.LocalDateTime

/**
 * Represents an Exercise assigned to students.
 * Exercises are typically smaller, practice-oriented tasks within a chapter or course.
 *
 * @property id Unique identifier for the exercise.
 * @property title The title of the exercise (e.g., "Chapter 3 Practice Problems").
 * @property description Detailed instructions or context for the exercise.
 * @property chapterId The ID of the [Chapter] this exercise is associated with.
 * @property maxScore The maximum score achievable for this exercise.
 * @property dueDate The date and time when the exercise is due.
 * @property attachments A list of [Attachment] domain models linked to this exercise (e.g., problem sheets, templates).
 *
 * Example Usage:
 * val mathExercise = Exercise(
 * id = "ex-ch3-prob1",
 * title = "Algebraic Expressions Practice",
 * description = "Solve problems 1-5 from page 78.",
 * chapterId = "math-ch-3",
 * maxScore = 20.0,
 * dueDate = LocalDateTime.of(2024, 10, 5, 23, 59),
 * attachments = emptyList()
 * )
 */
data class Exercise(
    val id: String,
    val title: String,
    val description: String?,
    val chapterId: String,
    val maxScore: Double,
    val dueDate: LocalDateTime?, // Made nullable as some exercises might not have strict due dates
    val attachments: List<Attachment>?
)