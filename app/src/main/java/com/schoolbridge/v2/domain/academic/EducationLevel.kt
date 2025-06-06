package com.schoolbridge.v2.domain.academic

/**
 * Represents a general Education Level offered by a school system.
 * This defines broad categories of education (e.g., Nursery, Primary, Secondary).
 *
 * @property id Unique identifier for the education level.
 * @property name The name of the education level (e.g., "Primary", "Junior Secondary", "University").
 * @property description A brief description of this education level.
 * @property order An integer representing the typical progression order (e.g., Primary=1, Secondary=2).
 *
 * Example Usage:
 * val primaryEdu = EducationLevel(
 * id = "edu-primary",
 * name = "Primary Education",
 * description = "Foundation education for younger children.",
 * order = 1
 * )
 */
data class EducationLevel(
    val id: String,
    val name: String,
    val description: String?,
    val order: Int
)