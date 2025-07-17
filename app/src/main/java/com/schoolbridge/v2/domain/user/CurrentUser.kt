package com.schoolbridge.v2.domain.user

import kotlinx.serialization.Serializable

@Serializable
data class CurrentUser(
    val userId: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val activeRoles: Set<UserRole>,
    val phoneNumber: String? = null,
    val nationalId: String? = null,
    val address: Address? = null,
    val profilePictureUrl: String? = null,
    var currentRole: UserRole? = null,
    val joinDate: String? = null,
    val linkedStudents: List<LinkedStudent> = emptyList(),
    val gender: Gender? = null,
    val isVerified: Boolean = false
) {
    @Serializable
    data class Address(
        val district: String? = null,
        val sector: String? = null,
        val cell: String? = null,
        val village: String? = null
    )

    @Serializable
    data class LinkedStudent(
        val id: String,
        val firstName: String,
        val lastName: String
    )

    fun isParent(): Boolean = activeRoles.contains(UserRole.PARENT)
    fun isStudent(): Boolean = activeRoles.contains(UserRole.STUDENT)
    fun isTeacher(): Boolean = activeRoles.contains(UserRole.TEACHER)
    fun isAdmin(): Boolean = activeRoles.contains(UserRole.SCHOOL_ADMIN)

    fun isCurrentParent(): Boolean = currentRole == UserRole.PARENT
    fun isCurrentStudent(): Boolean = currentRole == UserRole.STUDENT
    fun isCurrentTeacher(): Boolean = currentRole == UserRole.TEACHER
    fun isCurrentAdmin(): Boolean = currentRole == UserRole.SCHOOL_ADMIN
}
