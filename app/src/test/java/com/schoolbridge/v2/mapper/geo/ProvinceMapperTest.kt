package com.schoolbridge.v2.mapper.geo

import com.schoolbridge.v2.data.dto.geo.ProvinceDto
import org.junit.Assert.assertEquals
import org.junit.Test

class ProvinceMapperTest {

    @Test
    fun `province dto maps directly to domain`() {
        val dto = ProvinceDto(
            id = "KGL",
            name = "Kigali City"
        )

        val domain = dto.toDomain()

        assertEquals("KGL", domain.id)
        assertEquals("Kigali City", domain.name)
    }
}
