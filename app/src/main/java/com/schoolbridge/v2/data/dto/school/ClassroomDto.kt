package com.schoolbridge.v2.data.dto.school

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for a **Classroom** within a School or Building.
 *
 * This class represents a specific physical space used for teaching, learning, or other school activities.
 *
 * **When to use this class:**
 * You'll use `ClassroomDto` for scheduling classes, managing room bookings, or providing information
 * about school facilities.
 *
 * **Example Situations:**
 * - **Timetabling:** Assigning specific classes to specific rooms for a given time slot.
 * - **Room Booking System:** Allowing staff to book classrooms for meetings or events.
 * - **Campus Maps:** Displaying room locations on a digital map of the school or building.
 * - **Resource Management:** Tracking equipment available in a specific lab or classroom.
 *
 * @property id A unique identifier for the classroom. Example: "ROOM101"
 * @property name The common name or number of the classroom (e.g., "Room 101", "Computer Lab A", "Lecture Hall 3").
 * @property schoolId The unique ID of the school this classroom belongs to. This is a mandatory link.
 * Example: "SCH001"
 * @property buildingId An optional unique ID of the building this classroom is located within.
 * This is used when a school has multiple buildings. Nullable if the classroom is not part of a specific named building
 * or if building details aren't tracked. Example: "BUILD_MAIN"
 * @property capacity The maximum number of people (students, teachers) the classroom can comfortably accommodate.
 * Nullable if capacity is not defined or tracked. Example: 30
 * @property type The type of classroom (e.g., "LECTURE_HALL", "LAB", "STANDARD_CLASSROOM", "ART_STUDIO", "GYM").
 * Nullable if not categorized. This could also be represented by an enum for strict types. Example: "STANDARD_CLASSROOM"
 */
data class ClassroomDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("school_id") val schoolId: String,
    @SerializedName("building_id") val buildingId: String?,
    @SerializedName("capacity") val capacity: Int?,
    @SerializedName("type") val type: String?
)