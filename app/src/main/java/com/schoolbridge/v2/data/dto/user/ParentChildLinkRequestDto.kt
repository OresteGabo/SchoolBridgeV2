package com.schoolbridge.v2.data.dto.user

import com.google.gson.annotations.SerializedName
import com.schoolbridge.v2.data.enums.VerificationMethod

/**
 * Nested Data Transfer Object (DTO) for a **summary of a child linked to a parent**, as received from the API.
 *
 * This DTO provides essential, concise information about a child that is linked to a parent's
 * account. It's designed to be lightweight, typically used in lists where full student details
 * aren't immediately necessary.
 *
 * **Real-life Example:**
 * On a parent's dashboard, a list of their children might be displayed. Each item in that list
 * would use data from this `LinkedChildInfoDto` to show the child's name, profile picture,
 * and current school, along with the verification status of the parent's relationship to that child.
 *
 * @property studentId The unique identifier of the linked student. Example: "ST0054"
 * @property studentFirstName The first name of the linked student. Example: "Kamali"
 * @property studentLastName The last name of the linked student. Example: "Mukiza"
 * @property studentProfilePictureUrl An optional URL to the linked student's profile picture.
 * @property relationshipType The parent's relationship to the child (e.g., "MOTHER", "FATHER", "GUARDIAN").
 * @property currentSchoolName The name of the school the linked child is currently attending.
 * Nullable if the child is not currently enrolled or school info is not available.
 * Example: "Green Hills Academy"
 * @property linkageVerifiedByUserId The ID of the user who performed the verification for this
 * specific parent-child linkage. This could be a school administrator or another verified parent
 * if delegation is allowed for this link. Null if verified through an automated process without
 * a specific user.
 * @property linkageVerifiedByMethod The method used to verify this parent-child linkage (e.g.,
 * `IN_PERSON_SCHOOL_ADMIN`, `DELEGATED_BY_VERIFIED_USER`). Null if the linkage is not yet verified.
 */
data class LinkedChildInfoDto(
    @SerializedName("studentIid") val studentId: String,
    @SerializedName("studentFirstName") val studentFirstName: String,
    @SerializedName("studentLastName") val studentLastName: String,
    @SerializedName("studentProfilePictureUrl") val studentProfilePictureUrl: String?,
    @SerializedName("relationshipType") val relationshipType: String,
    @SerializedName("currentSchoolName") val currentSchoolName: String?,
    @SerializedName("linkageVerifiedByUserId") val linkageVerifiedByUserId: String?,
    @SerializedName("linkageVerifiedByMethod") val linkageVerifiedByMethod: VerificationMethod?
)