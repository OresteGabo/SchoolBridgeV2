package com.schoolbridge.v2.domain.geo

import com.google.gson.annotations.SerializedName

/**
 * Represents a Sector as a core geographical entity within the application's domain.
 * It belongs to a specific District.
 */
data class Sector(
    val code: Char,
    val title: String
) {
    fun id(districtCode: Char): String {
        return "${districtCode}$code"
    }
}
