package com.schoolbridge.v2.domain.geo

import com.google.gson.annotations.SerializedName

/**
 * Represents a Sector as a core geographical entity within the application's domain.
 * It belongs to a specific District.
 */
data class Sector(
    val id: String,
    val name: String,
    val districtId: String // Reference to the ID of the parent District
    // You could optionally add 'val districtName: String' for display convenience.
)