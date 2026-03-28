package com.schoolbridge.v2.domain.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class TimeUtilsTest {

    @Test
    fun `format and parse date round trip`() {
        val date = LocalDate.of(2026, 3, 28)

        val formatted = TimeUtils.formatDate(date)
        val parsed = TimeUtils.parseDate(formatted)

        assertEquals("2026-03-28", formatted)
        assertEquals(date, parsed)
    }

    @Test
    fun `format and parse datetime round trip`() {
        val dateTime = LocalDateTime.of(2026, 3, 28, 14, 35, 10)

        val formatted = TimeUtils.formatDateTime(dateTime)
        val parsed = TimeUtils.parseDateTime(formatted)

        assertEquals("2026-03-28T14:35:10", formatted)
        assertEquals(dateTime, parsed)
    }

    @Test
    fun `parse helpers return null for invalid strings`() {
        assertNull(TimeUtils.parseDate("28-03-2026"))
        assertNull(TimeUtils.parseDateTime("March 28, 2026"))
    }

    @Test
    fun `today and age helpers return sensible results`() {
        assertTrue(TimeUtils.isToday(LocalDate.now()))
        assertNotNull(TimeUtils.calculateAge(LocalDate.now().minusYears(20)))
    }
}
