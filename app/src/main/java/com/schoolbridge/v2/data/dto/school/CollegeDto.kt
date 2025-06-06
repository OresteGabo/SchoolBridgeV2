package com.schoolbridge.v2.data.dto.school

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for a College within a University.
 * This represents a major academic division (e.g., College of Business and Economics)
 * that can be located on a specific campus.
 *
 * @property id Unique identifier for the college.
 * @property name The full name of the college (e.g., "College of Business and Economics").
 * @property schoolId The ID of the parent university/school this college belongs to.
 * @property campusId The ID of the specific campus where this college is located.
 * @property description A brief description of the college.
 * @property deanUserId The user ID of the dean or head of this college.
 */
data class CollegeDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("school_id") val schoolId: String, // Which university/school does it belong to
    @SerializedName("campus_id") val campusId: String, // Which campus is it located on
    @SerializedName("description") val description: String?,
    @SerializedName("dean_user_id") val deanUserId: String? // ID of the user who is the dean
)