package com.schoolbridge.v2.domain.geo

import com.google.gson.annotations.SerializedName

/**
 * Represents a District as a core geographical entity within the application's domain.
 * It belongs to a specific Province.
 */
data class District(
    val id: String,
    val name: String,
    val provinceId: String // Reference to the ID of the parent Province
    // You could optionally add a 'val provinceName: String' if the backend consistently sends it
    // and your UI often needs to display "District X, Province Y" without extra lookups.
)