package com.schoolbridge.v2.domain.academic

import java.time.LocalDate

/**
 * Represents an Academic Year in the school system.
 * This domain model defines the period during which academic activities occur.
 *
 * @property id Unique identifier for the academic year (e.g., "AY2024-2025").
 * @property name A human-readable name for the academic year (e.g., "2024-2025 Academic Year").
 * @property startDate The date when the academic year officially begins.
 * @property endDate The date when the academic year officially ends.
 * @property isCurrent A boolean indicating if this is the currently active academic year.
 *
 * Example Usage:
 * val currentAcademicYear = AcademicYear(
 * id = "AY2024-2025",
 * name = "2024-2025 Academic Year",
 * startDate = LocalDate.of(2024, 9, 1),
 * endDate = LocalDate.of(2025, 6, 30),
 * isCurrent = true
 * )
 */
data class AcademicYear(
    val id: String,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val isCurrent: Boolean
)