package com.schoolbridge.v2.domain.user

// File: com.schoolvridge.v2.domain.model.PunishmentType.kt (or similar path)
enum class PunishmentType {
    WARNING,
    DETENTION,
    SUSPENSION,
    EXPULSION;

    // Optional: Add a helper to convert from backend string if needed
    companion object {
        fun fromString(typeString: String): PunishmentType {
            return entries.firstOrNull { it.name.equals(typeString, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unknown PunishmentType: $typeString")
        }
    }
}