package com.schoolbridge.v2.data.dto.user

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for **School Administrator-specific details** as part of a [UserDto].
 *
 * This DTO contains information relevant only to a user who has the "SCHOOL_ADMIN" role.
 * It's typically nested within a [UserDto] when fetching a full user profile.
 *
 * **Real-life Example:**
 * When a user with administrator privileges logs in, their [UserDto] will include this
 * `SchoolAdminDetailsDto`, allowing the app to determine which school(s) they manage and
 * what their specific role is (e.g., "Registrar", "Head of IT").
 *
 * @property adminId A unique identifier for the school administrator. Example: "ADM007"
 * @property adminRoleTitle The specific title or role of the administrator within the school.
 * Example: "Head of Admissions", "IT Manager", "Academic Registrar"
 * @property managesSchoolId The ID of the school that this administrator manages. A single
 * admin might manage multiple schools, or this could be a primary school ID.
 * Example: "SCHL_UNIRW"
 */
data class SchoolAdminDetailsDto(
    @SerializedName("adminId") val adminId: String,
    @SerializedName("adminRoleTitle") val adminRoleTitle: String,
    @SerializedName("managesSchoolId") val managesSchoolId: String
)