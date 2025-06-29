package com.schoolbridge.v2.domain.geo


/**
 * Represents a District as a core geographical entity within the application's domain.
 * It belongs to a specific Province.
 */
data class District(
    val code: Char,
    val title: String,
    val sectors: List<Sector> = emptyList()
)