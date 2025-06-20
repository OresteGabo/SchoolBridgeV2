package com.schoolbridge.v2.domain.academic

import java.time.LocalDate

/**
 * Represents a globally unique academic year in the school system.
 *
 * This class models the time period during which academic activities occur,
 * such as classes, exams, enrollments, and promotions. The academic year
 * is considered to start on September 1st of a given year and end on July 31st
 * of the following year.
 *
 * To ensure consistency across the system, this class is implemented as a
 * singleton per academic year, identified by its start year (the calendar year
 * of September 1st). All dates falling within the range September 1st (year X)
 * through July 31st (year X+1) are mapped to the same `AcademicYear` instance.
 *
 * Use [getAcademicYear] with any date within the academic year to obtain or create
 * the singleton instance. This prevents multiple inconsistent representations of
 * the same academic year.
 *
 * Equality and hash codes are based on the academic year ID, which is derived
 * from the academic year start year.
 *
 * @property startDate The official start date of the academic year (September 1st).
 * @property endDate The official end date of the academic year (July 31st of the next year).
 * @property isCurrent Whether this academic year is currently active.
 * @property id Unique identifier of the academic year, e.g. "AY2024-2025".
 * @property name A human-readable name for the year, e.g. "2024–2025 Academic Year".
 *
 * Example:
 * ```
 * val ay = AcademicYear.getAcademicYear(LocalDate.of(2024, 9, 1), isCurrent = true)
 * println(ay.id)    // AY2024-2025
 * println(ay.name)  // 2024–2025 Academic Year
 * ```
 */
class AcademicYear private constructor(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val isCurrent: Boolean
) {

    /**
     * Unique ID for this academic year, e.g. "AY2024-2025".
     */
    val id: String
        get() = "AY${startDate.year}-${startDate.year + 1}"

    /**
     * Human-readable name, e.g. "2024–2025 Academic Year".
     */
    val name: String
        get() = "${startDate.year}–${startDate.year + 1} Academic Year"

    override fun equals(other: Any?): Boolean {
        return other is AcademicYear && this.id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    /**
     * Returns [name] when this object is printed or logged.
     */
    override fun toString(): String = name

    companion object {
        private val cache = mutableMapOf<String, AcademicYear>()

        /**
         * Given any [date], returns the singleton AcademicYear instance for
         * the academic year it falls into. The academic year starts September 1st
         * of a given year and ends July 31st of the next year.
         *
         * @param date A date within the academic year range.
         * @param isCurrent Whether this academic year is currently active (default false).
         * @throws IllegalArgumentException if the date is outside September–July range.
         */
        fun getAcademicYear(date: LocalDate, isCurrent: Boolean = false): AcademicYear {
            val startYear = date.academicYearStart()
            val key = "AY$startYear-${startYear + 1}"
            return cache.getOrPut(key) {
                val startDate = LocalDate.of(startYear, 9, 1)
                val endDate = LocalDate.of(startYear + 1, 7, 31)
                AcademicYear(startDate, endDate, isCurrent)
            }
        }

        /**
         * Helper extension function to get the academic year start year from any date.
         * Returns the calendar year corresponding to September–December dates,
         * or the previous calendar year for January–July dates.
         *
         * @throws IllegalArgumentException if the month is August (outside academic year).
         */
        private fun LocalDate.academicYearStart(): Int = when (this.monthValue) {
            in 9..12 -> this.year
            in 1..7 -> this.year - 1
            else -> throw IllegalArgumentException("Date outside academic year range (August not supported)")
        }
    }
}
