package com.schoolbridge.v2.domain.academic.evaluation

import com.schoolbridge.v2.domain.common.Attachment
import java.time.LocalDateTime

/**
 * Represents an academic Evaluation (e.g., Exam, Quiz, Homework).
 * This defines an assessment given to students within a course.
 *
 * @property id Unique identifier for the evaluation.
 * @property title The title of the evaluation (e.g., "Mid-Term Exam", "Chapter 5 Quiz").
 * @property description A detailed description or instructions for the evaluation.
 * @property courseId The ID of the [Course] this evaluation belongs to.
 * @property evaluationType The type of evaluation (e.g., [EvaluationType.EXAM], [EvaluationType.QUIZ]).
 * @property evaluationFormat The format of the evaluation (e.g., [EvaluationFormat.MULTIPLE_CHOICE], [EvaluationFormat.ESSAY]).
 * @property maxScore The maximum possible score achievable on this evaluation.
 * @property weightInGrade The percentage weight this evaluation contributes to the final grade for the course.
 * @property dueDate The date and time when the evaluation is due.
 * @property teacherUserId The ID of the [User] (teacher) who created or is responsible for this evaluation.
 * @property isPublished A boolean indicating if the evaluation has been published to students.
 * @property attachments A list of [Attachment] domain models linked to this evaluation (e.g., problem sets, rubrics).
 *
 * Example Usage:
 * val finalExam = Evaluation(
 * id = "eval-math-final",
 * title = "Math Final Exam",
 * description = "Comprehensive assessment of all topics.",
 * courseId = "math-g10-2024",
 * evaluationType = EvaluationType.EXAM,
 * evaluationFormat = EvaluationFormat.MULTIPLE_CHOICE,
 * maxScore = 100.0,
 * weightInGrade = 0.40,
 * dueDate = LocalDateTime.of(2025, 5, 20, 10, 0),
 * teacherUserId = "user-teacher-id-123",
 * isPublished = true,
 * attachments = emptyList()
 * )
 */
data class Evaluation(
    val id: String,
    val title: String,
    val description: String?,
    val courseId: String,
    val evaluationType: EvaluationType,
    val evaluationFormat: EvaluationFormat,
    val maxScore: Double,
    val weightInGrade: Double,
    val dueDate: LocalDateTime,
    val teacherUserId: String,
    val isPublished: Boolean,
    val attachments: List<Attachment>?
)