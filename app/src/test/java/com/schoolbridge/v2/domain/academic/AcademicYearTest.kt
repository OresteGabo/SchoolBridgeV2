package com.schoolbridge.v2.domain.academic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Test
import java.time.LocalDate

class AcademicYearTest {

    @Test
    fun `getAcademicYear returns same singleton for dates in same academic year`() {
        val first = AcademicYear.getAcademicYear(LocalDate.of(2026, 3, 28))
        val second = AcademicYear.getAcademicYear(LocalDate.of(2025, 9, 3))

        assertSame(first, second)
        assertEquals("AY2025-2026", first.id)
        assertEquals("2025–2026 Academic Year", first.name)
        assertEquals("2025–2026 Academic Year", first.toString())
    }

    @Test
    fun `getAcademicYear rejects august dates`() {
        try {
            AcademicYear.getAcademicYear(LocalDate.of(2026, 8, 10))
            fail("Expected IllegalArgumentException")
        } catch (exception: IllegalArgumentException) {
            assertEquals(
                "Date outside academic year range (August not supported)",
                exception.message
            )
        }
    }
}
