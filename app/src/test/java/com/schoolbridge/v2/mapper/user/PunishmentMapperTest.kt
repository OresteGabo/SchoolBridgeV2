package com.schoolbridge.v2.mapper.user

import com.schoolbridge.v2.data.dto.user.PunishmentDto
import com.schoolbridge.v2.domain.user.Punishment
import com.schoolbridge.v2.domain.user.PunishmentType
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class PunishmentMapperTest {

    @Test
    fun `punishment dto toDomain converts type and date`() {
        val dto = PunishmentDto(
            id = "pun-1",
            type = "detention",
            reason = "Repeated disruption in class",
            dateIssued = "2026-03-21",
            durationDays = 2,
            teacherId = "teacher-7",
            acknowledgedByParent = true
        )

        val domain = dto.toDomain()

        assertEquals("pun-1", domain.id)
        assertEquals(PunishmentType.DETENTION, domain.type)
        assertEquals(LocalDate.of(2026, 3, 21), domain.dateIssued)
        assertEquals(2, domain.durationDays)
        assertEquals("teacher-7", domain.teacherId)
        assertEquals(true, domain.acknowledgedByParent)
    }

    @Test
    fun `punishment toDto converts enum and date back to raw values`() {
        val domain = Punishment(
            id = "pun-2",
            type = PunishmentType.WARNING,
            reason = "Late assignment",
            dateIssued = LocalDate.of(2026, 3, 25),
            durationDays = null,
            teacherId = null,
            acknowledgedByParent = false
        )

        val dto = domain.toDto()

        assertEquals("pun-2", dto.id)
        assertEquals("WARNING", dto.type)
        assertEquals("2026-03-25", dto.dateIssued)
        assertEquals(null, dto.durationDays)
        assertEquals(null, dto.teacherId)
        assertEquals(false, dto.acknowledgedByParent)
    }
}
