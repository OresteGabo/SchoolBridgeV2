package com.schoolbridge.v2.data.dto.user

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for **requesting a new role** for a user.
 *
 * This DTO is used when a user (or an admin on their behalf) wants to apply for a specific
 * role within the system, like "PARENT", "TEACHER", or "SCHOOL_ADMIN". Such requests
 * typically require justification and/or supporting documents for verification by an administrator.
 *
 * **Real-life Example (Parent Role Request in Rwandan context):**
 * A parent, after registering, wants to link to their child. They would submit this DTO
 * to request the "PARENT" role, providing their national ID, the child's national ID,
 * and perhaps a scanned family book/card.
 *
 * @property requestedRole The name of the role being requested (e.g., "PARENT", "TEACHER", "SCHOOL_ADMIN").
 * @property justification An optional explanation from the user for why they are requesting this role.
 * @property supportingDocumentsUrls A list of URLs to uploaded documents that support the request
 * (e.g., scanned ID, academic certificates, family book/card).
 *
 * **Specific fields for Parent Role Request (Rwandan Context):**
 * @property childStudentId Optional: The ID of the student this parent wishes to link to, if already known in the system.
 * @property childNationalId The child's national identification number, crucial for verification.
 * @property parentNationalId The parent's national identification number, for their own verification.
 * @property familyCardDocumentUrl A URL to a scanned copy of the family identification card/book,
 * a key document for family verification in Rwanda.
 */
data class RoleRequestDto(
    @SerializedName("requestedRole") val requestedRole: String,
    @SerializedName("justification") val justification: String?,
    @SerializedName("supportingDocumentsUrls") val supportingDocumentsUrls: List<String>?,

    // Specific fields for Parent Role Request (Rwandan Context)
    @SerializedName("childStudentId") val childStudentId: String?,
    @SerializedName("childNationalId") val childNationalId: String?,
    @SerializedName("parentNationalId") val parentNationalId: String?,
    @SerializedName("familyCardDocumentUrl") val familyCardDocumentUrl: String?
)