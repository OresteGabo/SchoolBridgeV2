package com.schoolbridge.v2.data.dto.common

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for a **generic Address**.
 *
 * This DTO is a reusable component designed to represent postal or physical addresses
 * across various parts of your application. It can be embedded within other DTOs
 * like [UserDto] (for a user's address), [SchoolDto] (for a school's location),
 * or any other entity that requires address details.
 *
 * **Real-life Example:**
 * - **School Registration:** When registering a new school, you'd provide its main address using this DTO.
 * `SchoolDto(..., address = AddressDto(street="KN 123 St", city="Kigali", country="Rwanda"))`
 * - **User Profile:** A user might update their residential address in their profile settings.
 * `UpdateUserRequestDto(..., address = AddressDto(street="KG 456 Ave", city="Gasabo", stateProvince="Kigali City", country="Rwanda"))`
 *
 * @property street The street name and number, nullable if not detailed or unavailable. Example: "KN 123 St"
 * @property city The city name. Example: "Kigali", "Butare"
 * @property stateProvince The state or province name, nullable if not applicable (e.g., for countries without states).
 * Example: "Kigali City", "Southern Province"
 * @property postalCode The postal or ZIP code, nullable if not applicable. Example: "00001"
 * @property country The country name. Example: "Rwanda"
 */
data class AddressDto(
    @SerializedName("street") val street: String?,
    @SerializedName("city") val city: String,
    @SerializedName("state_province") val stateProvince: String?,
    @SerializedName("postal_code") val postalCode: String?,
    @SerializedName("country") val country: String
)