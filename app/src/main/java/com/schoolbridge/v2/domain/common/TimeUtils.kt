package com.schoolbridge.v2.domain.common

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * An object providing utility functions for working with dates and times within the domain layer.
 * This ensures consistent date handling and formatting across the application.
 */
object TimeUtils {

    /**
     * Formats a [LocalDate] into a standard "yyyy-MM-dd" string.
     * @param date The LocalDate to format.
     * @return The formatted date string.
     * Example: `formatDate(LocalDate.of(2025, 6, 5))` returns "2025-06-05"
     */
    fun formatDate(date: LocalDate): String {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE) // Example: "2025-06-05"
    }

    /**
     * Formats a [LocalDateTime] into a standard "yyyy-MM-dd'T'HH:mm:ss" string (ISO 8601).
     * @param dateTime The LocalDateTime to format.
     * @return The formatted date-time string.
     * Example: `formatDateTime(LocalDateTime.of(2025, 6, 5, 14, 30))` returns "2025-06-05T14:30:00"
     */
    fun formatDateTime(dateTime: LocalDateTime): String {
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) // Example: "2025-06-05T14:30:00"
    }

    /**
     * Parses a date string (e.g., "2025-06-05") into a [LocalDate].
     * @param dateString The date string to parse.
     * @return The parsed LocalDate, or null if parsing fails.
     * Example: `parseDate("2025-06-05")` returns `LocalDate.of(2025, 6, 5)`
     */
    fun parseDate(dateString: String): LocalDate? {
        return try {
            LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: DateTimeParseException) {
            null
        }
    }

    /**
     * Parses a date-time string (e.g., "2025-06-05T14:30:00") into a [LocalDateTime].
     * @param dateTimeString The date-time string to parse.
     * @return The parsed LocalDateTime, or null if parsing fails.
     * Example: `parseDateTime("2025-06-05T14:30:00")` returns `LocalDateTime.of(2025, 6, 5, 14, 30)`
     */
    fun parseDateTime(dateTimeString: String): LocalDateTime? {
        return try {
            LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        } catch (e: DateTimeParseException) {
            null
        }
    }

    /**
     * Checks if a given date is today's date.
     * @param date The LocalDate to check.
     * @return True if the date is today, false otherwise.
     * Example: `isToday(LocalDate.now())` returns `true`
     */
    fun isToday(date: LocalDate): Boolean {
        return date == LocalDate.now()
    }

    /**
     * Calculates the age based on a birth date.
     * @param birthDate The birth date [LocalDate].
     * @return The age in years.
     * Example: `calculateAge(LocalDate.of(2000, 1, 1))` for `2025-06-05` returns 25.
     */
    fun calculateAge(birthDate: LocalDate): Int {
        return birthDate.until(LocalDate.now()).years
    }
}