package com.schoolbridge.v2.mapper.user



import com.schoolbridge.v2.data.dto.user.PunishmentDto
import com.schoolbridge.v2.domain.user.Punishment
import com.schoolbridge.v2.domain.user.PunishmentType
import java.time.LocalDate

/**
 * Provides mapping functions to convert between [PunishmentDto] (data layer)
 * and [Punishment] (domain layer).
 */
fun PunishmentDto.toDomain(): Punishment {
    return Punishment(
        id = this.id,
        type = PunishmentType.fromString(this.type), // Convert String to enum
        reason = this.reason,
        dateIssued = LocalDate.parse(this.dateIssued), // Convert String to LocalDate
        durationDays = this.durationDays,
        teacherId = this.teacherId,
        acknowledgedByParent = this.acknowledgedByParent
    )
}

/**
 * Provides mapping functions to convert between [Punishment] (domain layer)
 * and [PunishmentDto] (data layer).
 */
fun Punishment.toDto(): PunishmentDto {
    return PunishmentDto(
        id = this.id,
        type = this.type.name, // Convert enum to String
        reason = this.reason,
        dateIssued = this.dateIssued.toString(), // Convert LocalDate to String
        durationDays = this.durationDays,
        teacherId = this.teacherId,
        acknowledgedByParent = this.acknowledgedByParent
    )
}