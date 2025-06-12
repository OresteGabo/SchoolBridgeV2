package com.schoolbridge.v2.data.dto.school

import com.google.gson.annotations.SerializedName
import com.schoolbridge.v2.data.dto.common.AddressDto

/**
 * Data Transfer Object (DTO) for a **general School**.
 *
 * This DTO includes core information applicable to any type of educational institution,
 * whether it's a primary school, high school, or university. It serves as the base
 * data model for representing any school in the system.
 *
 * **Real-life Example:**
 * When a user searches for schools, or when displaying information about a student's
 * or teacher's affiliated school, this DTO would be used to carry the basic school data.
 *
 * @property id A unique identifier for the school. Example: "SCHL_UNIRW"
 * @property name The official name of the school. Example: "University of Rwanda"
 * @property schoolType The type of school, represented as a string (e.g., "PRIMARY_SCHOOL",
 * "HIGH_SCHOOL", "UNIVERSITY"). This helps differentiate rules or UI elements.
 * @property code A unique short code or abbreviation for the school. Example: "UR", "KGLHS"
 * @property address The [AddressDto] containing the physical location details of the school.
 * @property phoneNumber The main contact phone number for the school, nullable.
 * @property email The main contact email address for the school, nullable.
 * @property websiteUrl The official website URL of the school, nullable.
 * @property logoUrl An optional URL to the school's official logo.
 * @property establishedDate The date when the school was established, as an ISO 8601 date string.
 * Nullable if not provided. Example: "1964-11-09"
 * @property enrollmentCount The total number of students currently enrolled in the school.
 * Nullable if not tracked or available via this endpoint.
 * @property status The current operational status of the school (e.g., "ACTIVE", "CLOSED", "PENDING_REGISTRATION").
 * Nullable if not tracked.
 */
data class SchoolDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("schoolType") val schoolType: String,
    @SerializedName("code") val code: String,
    @SerializedName("address") val address: AddressDto,
    @SerializedName("phoneNumber") val phoneNumber: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("websiteUrl") val websiteUrl: String?,
    @SerializedName("logoUrl") val logoUrl: String?,
    @SerializedName("establishedDate") val establishedDate: String?,
    @SerializedName("enrollmentCount") val enrollmentCount: Int?,
    @SerializedName("status") val status: String?
)