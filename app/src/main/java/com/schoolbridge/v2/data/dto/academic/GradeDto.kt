package com.schoolbridge.v2.data.dto.academic

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for a **Grade** record.
 *
 * This DTO represents an individual student's score or result for a specific [EvaluationDto].
 * It contains the achieved score, calculated percentage, optional letter grade, and comments.
 *
 * **Real-life Example:**
 * - **Student's Report Card:** A student's online report card would display a list of these
 * `GradeDto` objects for each evaluation they took.
 * - **Teacher's Grade Entry:** A teacher enters a student's score for a quiz, and this DTO
 * is used to send that grade information to the backend.
 *
 * @property id A unique identifier for this grade record. Example: "GRD-ST001-EVAL005"
 * @property evaluationId The ID of the [EvaluationDto] this grade belongs to. Example: "EVAL-MATH101-QUIZ1"
 * @property studentId The ID of the student who received this grade. Example: "ST0054"
 * @property scoreAchieved The raw score the student achieved on the evaluation. Example: 85.5
 * @property maxScorePossible The maximum possible score for the evaluation. While potentially
 * redundant with `EvaluationDto.maxScore`, it's useful to include for immediate client display
 * or if the evaluation's max score can vary per student. Example: 100.0
 * @property percentageScore The calculated percentage score achieved by the student.
 * Example: 85.5 (if scoreAchieved is 85.5 and maxScorePossible is 100.0)
 * @property gradeLetter An optional letter grade assigned (e.g., "A", "B+", "Pass", "Fail").
 * Nullable if the grading system doesn't use letter grades or if not yet assigned.
 * @property comments Optional textual comments from the teacher regarding the grade or performance.
 * Example: "Excellent understanding of core concepts, needs improvement in problem-solving."
 * @property dateRecorded The date and time when this grade was recorded, as an ISO 8601 date-time string.
 * Example: "2024-05-30T10:30:00Z"
 * @property recordedByTeacherId The ID of the teacher who recorded this grade. Example: "TCHR001"
 */
data class GradeDto(
    val id: String,
    @SerializedName("evaluation_id") val evaluationId: String,
    @SerializedName("student_id") val studentId: String,
    @SerializedName("score_achieved") val scoreAchieved: Double,
    @SerializedName("max_score_possible") val maxScorePossible: Double,
    @SerializedName("percentage_score") val percentageScore: Double,
    @SerializedName("grade_letter") val gradeLetter: String?,
    val comments: String?,
    @SerializedName("date_recorded") val dateRecorded: String,
    @SerializedName("recorded_by_teacher_id") val recordedByTeacherId: String
)