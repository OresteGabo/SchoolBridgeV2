package com.schoolbridge.v2.mapper.geo

import com.schoolbridge.v2.data.dto.geo.ProvinceDto
import com.schoolbridge.v2.domain.geo.Province

fun ProvinceDto.toDomain(): Province {
    return Province(
        id = this.id,
        name = this.name
    )
}