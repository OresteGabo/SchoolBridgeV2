package com.schoolbridge.v2.data.dto.user

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for **updating a user's profile**.
 *
 * This request DTO allows for partial updates to a user's general profile information.
 * All fields are nullable because the user might only update one or a few fields at a time.
 * For example, a user might only update their phone number and profile picture, leaving other
 * fields unchanged. The backend should handle null values by keeping existing data.
 *
 * **Real-life Example:**
 * When a user goes to their "Edit Profile" screen and changes their phone number and uploads
 * a new profile picture, this DTO is sent to the `/users/me` (PUT) endpoint with only those
 * updated fields populated.
 *
 * @property firstName The user's updated first name, nullable if not being changed.
 * @property lastName The user's updated last name, nullable if not being changed.
 * @property phoneNumber The user's updated phone number, nullable if not being changed.
 * @property gender The user's updated gender, nullable if not being changed.
 * @property dateOfBirth The user's updated date of birth as an ISO 8601 date string, nullable
 * if not being changed.
 * @property profilePictureUrl The updated URL for the user's profile picture, nullable if not
 * being changed. This could be a new URL after a successful image upload.
 */
data class UpdateUserRequestDto(
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("phone_number") val phoneNumber: String?,
    @SerializedName("gender") val gender: String?,
    @SerializedName("date_of_birth") val dateOfBirth: String?,
    @SerializedName("profile_picture_url") val profilePictureUrl: String?
)