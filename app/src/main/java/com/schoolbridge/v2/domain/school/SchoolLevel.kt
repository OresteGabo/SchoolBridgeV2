package com.schoolbridge.v2.domain.school

/**
 * Represents a generic school level like "Primary 1", "Senior 4", or "Year 2".
 * This is **not tied** to a specific school or year.
 */
sealed class SchoolLevel {
    abstract val id: String
    abstract val name: String // e.g., "Senior 4", "Primary 1"

    data class NurseryLevel(override val id: String, override val name: String) : SchoolLevel()
    data class PrimaryLevel(override val id: String, override val name: String) : SchoolLevel()
    data class OLevel(override val id: String, override val name: String) : SchoolLevel()
    data class ALevel(override val id: String, override val name: String, val section: SchoolSection?) : SchoolLevel()
    data class TVETLevel(override val id: String, override val name: String, val trade: String?) : SchoolLevel()
    data class UniversityLevel(override val id: String, override val name: String, val faculty: Faculty?) : SchoolLevel()
}