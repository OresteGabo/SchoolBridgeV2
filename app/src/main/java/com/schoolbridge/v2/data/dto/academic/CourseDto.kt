package com.schoolbridge.v2.data.dto.academic

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) for a **Course**.
 *
 * This DTO represents an academic course offered within a school. It contains all
 * essential details about the course, including its subject, academic period,
 * and associated teachers.
 *
 * **Real-life Example:**
 * - **Course Catalog:** When a student browses available courses to register, each course
 * displayed would be represented by this [CourseDto].
 * - **Teacher's Schedule:** A teacher's profile might list their `assignedCourses`,
 * each coming from this DTO.
 *
 * @property id A unique identifier for the course. Example: "CRS-MATH101-2024"
 * @property name The official name of the course. Example: "Advanced Algebra"
 * @property description A brief explanation of the course content and objectives.
 * Example: "Covers complex numbers, matrices, and abstract algebraic structures."
 * @property subjectId The ID of the overarching subject category this course belongs to.
 * Example: "SUBJ-MATH"
 * @property subjectName The human-readable name of the subject. Example: "Mathematics"
 * @property academicYearId The ID of the academic year this course is offered in.
 * Example: "AY-2024-2025"
 * @property schoolLevelOfferingId The ID representing the specific school level, section, or
 * combination for which this course is offered (e.g., "S3 Science section", "Year 1 Comp Sci").
 * This links the course to a specific program or stream. Example: "SCHL-OFFER-S3SCI"
 * @property teacherIds A list of user IDs for all teachers assigned to teach this course.
 * Example: `["TCHR001", "TCHR005"]`
 * @property startDate The official start date of the course, as an ISO 8601 date string.
 * Example: "2024-09-01"
 * @property endDate The official end date of the course, as an ISO 8601 date string.
 * Example: "2025-06-30"
 * @property active A boolean indicating if the course is currently active and available.
 * Example: `true` for a current course, `false` for an archived or inactive one.
 */
/*
@Serializable
data class CourseDto(
    val id: Long,
    val name: String,
    val description: String,
    @SerializedName("subject_id") val subjectId: Long,
    @SerializedName("name") val subjectName: String,
    @SerializedName("academic_year_id") val academicYearId: Long,
    @SerializedName("school_level_offering_id") val schoolLevelOfferingId: Long,
    @SerializedName("teacher_ids") val teacherIds: List<Long>,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("end_date") val endDate: String,
    val active: Boolean
)
*/
@Serializable
data class CourseDto(
    val id: Long,
    val name: String,
    val description: String
)
