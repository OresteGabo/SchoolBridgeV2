package com.schoolbridge.v2.domain.geo

import org.junit.Assert.assertEquals
import org.junit.Test

class SectorTest {

    @Test
    fun `id combines district and sector codes`() {
        val sector = Sector(
            code = 'B',
            title = "Gikondo"
        )

        assertEquals("KB", sector.id('K'))
    }
}
