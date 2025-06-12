package com.schoolbridge.v2.domain.geo

/**
 * Represents a Province as a core geographical entity within the application's domain.
 * This model is independent of API or database specifics.
 */
data class Province(
    val id: String,
    val name: String
)