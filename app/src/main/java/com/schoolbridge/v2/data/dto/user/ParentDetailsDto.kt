package com.schoolbridge.v2.data.dto.user

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for **parent-specific details** within a User's profile.
 *
 * This class is designed to hold all information relevant to a user who possesses the "PARENT" role.
 * It primarily serves to manage and display the children linked to this parent's account,
 * along with the verification status of those linkages.
 *
 * **When to use this class:**
 * You'll use `ParentDetailsDto` when fetching or updating a user's profile where the user
 * has the "PARENT" role. It's essential for displaying a parent's dashboard, showing their
 * linked children, and managing their relationships to students.
 *
 * **How it works:**
 * This DTO is typically nested within the main [UserDto]. When a user logs in and their
 * `activeRoles` list contains "PARENT", the `parentDetails` field in their [UserDto] will be
 * populated with an instance of this class. The `linkedChildren` list within this DTO
 * provides a summary of each child associated with the parent's account, including the
 * crucial linkage verification status for each child.
 *
 * **Real-life Example:**
 * On a parent's mobile app dashboard, after they log in, the app fetches their [UserDto].
 * If `userDto.parentDetails` is not null, the app can then use the `parentDetails.linkedChildren`
 * list to display all the children associated with their account, showing each child's name,
 * profile picture, and whether their parental link is verified (e.g., using icons and colors).
 *
 * @property linkedChildren A list of [LinkedChildInfoDto] objects, each representing a child
 * linked to this parent's account. This list will contain essential details about each child
 * and, critically, the verification status of the parent's relationship to that specific child.
 * This list will be empty if the parent has no children linked yet.
 * @property nationalId The parent's national identification number, if collected separately
 * or as part of parent-specific verification, complementing the general user identity. Nullable.
 * @property familyCardDocumentUrl An optional URL to a document like a family card, which might be
 * used to verify parent-child linkages, particularly in regions where these documents are common.
 * Nullable.
 * @property numberOfChildren The total count of children currently linked to this parent's account.
 * This can be derived from `linkedChildren.size` but might be a pre-calculated field from the API. Nullable.
 */
data class ParentDetailsDto(
    @SerializedName("linkedChildren") val linkedChildren: List<LinkedChildInfoDto>,
    @SerializedName("nationalId") val nationalId: String?,
    @SerializedName("familyCardDocumentUrl") val familyCardDocumentUrl: String?,
    @SerializedName("numberOfChildren") val numberOfChildren: Int?
)