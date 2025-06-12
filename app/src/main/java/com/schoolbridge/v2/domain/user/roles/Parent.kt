package com.schoolbridge.v2.domain.user.roles

import com.schoolbridge.v2.domain.user.RelationshipType

/**
 * Client-side domain model representing a Parent's specific profile data.
 * This class holds data directly relevant to a user when they act as a parent.
 * It does NOT contain methods for parent actions (e.g., approving requests),
 * as those are handled by backend API calls.
 *
 * @property userId The ID of the main [User] object this parent profile belongs to.
 * @property parentId A unique identifier for this parent profile, which might be the same as [userId]
 * or a distinct system-generated ID.
 * @property linkedChildren A list of [LinkedChildInfo] objects, providing summary details
 * of the children officially linked to this parent's account.
 * This is crucial for parents to view their children's progress.
 *
 * Example Usage:
 * val parentData = user.parentDetails // Assuming user is a parent
 * if (parentData != null) {
 * println("Parent ${parentData.userId} has ${parentData.linkedChildren.size} linked children.")
 * parentData.linkedChildren.forEach { child ->
 * println("- ${child.studentFirstName} (${child.relationshipType})")
 * }
 * }
 */
data class Parent(
    val userId: String,
    val parentId: String,
    val linkedChildren: List<LinkedChildInfo>
)

/**
 * A nested data class within the parent's domain, providing a concise summary of a linked child.
 * This is used to display essential child information without needing to fetch full student profiles for every child.
 *
 * @property studentId The unique ID of the linked student.
 * @property studentFirstName The first name of the linked student.
 * @property studentLastName The last name of the linked student.
 * @property studentProfilePictureUrl An optional URL to the child's profile picture.
 * @property relationshipType The [RelationshipType] defining how the parent is related to this specific child (e.g., Mother, Father).
 * @property currentSchoolName The name of the school the child is currently enrolled in, for quick context.
 */
data class LinkedChildInfo(
    val studentId: String,
    val studentFirstName: String,
    val studentLastName: String,
    val studentProfilePictureUrl: String?,
    val relationshipType: RelationshipType,
    val currentSchoolName: String?
)