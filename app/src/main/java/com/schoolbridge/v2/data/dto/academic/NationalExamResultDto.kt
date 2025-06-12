package com.schoolbridge.v2.data.dto.academic

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for a **National Exam Result**.
 *
 * This DTO captures the results of major national or standardized examinations for a student.
 * It's typically used for high-stakes tests that determine progression or qualification.
 *
 * **Real-life Example:**
 * - **Student's Academic Transcript:** A student's official transcript might include their
 * national exam results, which would be retrieved using this DTO.
 * - **Admissions Process:** Universities or colleges might fetch these results when
 * evaluating a prospective student's application.
 *
 * @property id A unique identifier for this national exam result record. Example: "NEX-ST001-NESO2024"
 * @property studentId The ID of the student associated with these results. Example: "ST0054"
 * @property academicYearId The ID of the academic year in which the exam was taken.
 * Example: "AY-2023-2024"
 * @property examName The official name of the national examination.
 * Example: "National Examination for Secondary Education (NESO 2024)", "A-Level Exams"
 * @property subjectResults A map where keys are subject names (e.g., "Mathematics", "Biology")
 * and values are the scores obtained in those subjects. Example: `{"Mathematics": 85, "Biology": 92}`
 * @property totalScore The student's overall total score across all subjects in the exam. Example: 540
 * @property grade The overall grade or classification achieved in the exam (e.g., "Division 1", "Distinction").
 * Nullable if not applicable or not yet assigned.
 * @property resultDate The date when the exam results were officially released, as an ISO 8601 date string.
 * Example: "2024-08-01"
 */
data class NationalExamResultDto(
    val id: String,
    @SerializedName("studentId") val studentId: String,
    @SerializedName("academicYearId") val academicYearId: String,
    @SerializedName("examName") val examName: String,
    @SerializedName("subjectResults") val subjectResults: Map<String, Int>,
    @SerializedName("totalScore") val totalScore: Int,
    val grade: String?,
    @SerializedName("resultDate") val resultDate: String
)