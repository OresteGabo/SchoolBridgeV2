package com.schoolbridge.v2.mapper.geo

import com.schoolbridge.v2.data.dto.geo.SectorDto
import com.schoolbridge.v2.domain.geo.Sector

fun SectorDto.toDomain(): Sector {
    return Sector(
        id = this.id,
        name = this.name,
        districtId = this.districtId
    )
}