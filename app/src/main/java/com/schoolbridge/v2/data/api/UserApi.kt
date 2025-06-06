// Ensure you have these imports at the top of your UserApi.kt file
package com.schoolbridge.v2.data.api

import com.schoolbridge.v2.data.dto.auth.ForgotPasswordRequestDto
import com.schoolbridge.v2.data.dto.auth.LoginRequestDto
import com.schoolbridge.v2.data.dto.auth.LoginResponseDto
import com.schoolbridge.v2.data.dto.auth.RegisterRequestDto
import com.schoolbridge.v2.data.dto.auth.ResendOtpRequestDto
import com.schoolbridge.v2.data.dto.auth.ResetPasswordRequestDto
import com.schoolbridge.v2.data.dto.auth.VerifyOtpRequestDto
import com.schoolbridge.v2.data.dto.user.RoleRequestDto // New DTO for role requests
import com.schoolbridge.v2.data.dto.user.UpdateUserRequestDto
import com.schoolbridge.v2.data.dto.user.UserDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query // Potentially for admin filtering/searching

/**
 * Retrofit API interface for **User-related operations**.
 *
 * This interface defines the endpoints for comprehensive user management, including
 * authentication, profile management, self-service account actions (deletion, suspension),
 * and dynamic role assignment/revocation. It serves as the primary contract for all
 * user-centric interactions with the backend.
 */
interface UserApi {

    // --- Authentication & Account Setup ---

    /**
     * Authenticates a user with provided credentials.
     *
     * This is the primary endpoint for user login, exchanging credentials for authentication tokens.
     *
     * @param request The [LoginRequestDto] containing the user's username/email and password.
     * @return A [LoginResponseDto] which includes the `authToken`, `refreshToken`, and
     * basic user information.
     *
     * **Example Use:**
     * ```kotlin
     * try {
     * val response = userApi.login(LoginRequestDto("john.doe@example.com", "MySecureP@ssw0rd"))
     * // Save response.authToken and response.refreshToken for subsequent requests
     * println("Logged in successfully! User ID: ${response.userId}")
     * } catch (e: Exception) {
     * println("Login failed: ${e.message}")
     * }
     * ```
     */
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto

    /**
     * Registers a new user account.
     *
     * This endpoint is used for new user sign-ups, creating a basic user profile.
     * Further verification (e.g., email/phone OTP) might follow this step.
     *
     * @param request The [RegisterRequestDto] containing the new user's initial details
     * (first name, last name, email, phone number, password).
     * @return A [UserDto] representing the newly created user's profile.
     *
     * **Example Use:**
     * ```kotlin
     * try {
     * val newUser = RegisterRequestDto(
     * "Jane", "Doe", "jane.doe@example.com", "+250788123456", "StrongP@ss1", "StrongP@ss1"
     * )
     * val createdUser = userApi.register(newUser)
     * println("User registered with ID: ${createdUser.id}")
     * } catch (e: Exception) {
     * println("Registration failed: ${e.message}")
     * }
     * ```
     */
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): UserDto

    /**
     * Initiates the password reset process by requesting a reset link or OTP.
     *
     * The backend will send a verification code (e.g., to email or phone) associated
     * with the provided `emailOrPhone`.
     *
     * @param request The [ForgotPasswordRequestDto] identifying the user by email or phone.
     * @return A [Unit] (empty response) upon successful request, indicating the email/SMS was sent.
     *
     * **Example Use:**
     * ```kotlin
     * try {
     * userApi.forgotPassword(ForgotPasswordRequestDto("user@example.com"))
     * println("Password reset instructions sent to email.")
     * } catch (e: Exception) {
     * println("Forgot password failed: ${e.message}")
     * }
     * ```
     */
    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequestDto)

    /**
     * Completes the password reset process using a verification token and new password.
     *
     * This is the final step after a user has received and validated a password reset token/OTP.
     *
     * @param request The [ResetPasswordRequestDto] containing the verification token
     * and the new password details.
     * @return A [Unit] (empty response) upon successful password reset.
     *
     * **Example Use:**
     * ```kotlin
     * try {
     * // Assume 'token' was received via email/URL
     * userApi.resetPassword(ResetPasswordRequestDto(
     * token = "your-verification-token",
     * newPassword = "NewStrongP@ss2",
     * confirmNewPassword = "NewStrongP@ss2"
     * ))
     * println("Password reset successfully.")
     * } catch (e: Exception) {
     * println("Password reset failed: ${e.message}")
     * }
     * ```
     */
    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequestDto)

    /**
     * Verifies a One-Time Password (OTP) for various purposes (e.g., email/phone verification, 2FA).
     *
     * @param request The [VerifyOtpRequestDto] containing the identifier, the OTP code, and its purpose.
     * @return A [Unit] (empty response) upon successful OTP verification.
     *
     * **Example Use:**
     * ```kotlin
     * try {
     * userApi.verifyOtp(VerifyOtpRequestDto(
     * emailOrPhone = "+250788123456",
     * otpCode = "123456",
     * purpose = OtpPurpose.PHONE_VERIFICATION // Using the enum for type safety
     * ))
     * println("Phone number verified successfully.")
     * } catch (e: Exception) {
     * println("OTP verification failed: ${e.message}")
     * }
     * ```
     */
    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequestDto)

    /**
     * Requests a new One-Time Password (OTP) to be sent for a given purpose.
     *
     * This is useful if a user didn't receive the first OTP or if it expired.
     *
     * @param request The [ResendOtpRequestDto] specifying the identifier and the purpose for the OTP.
     * @return A [Unit] (empty response) upon successful resend request.
     *
     * **Example Use:**
     * ```kotlin
     * try {
     * userApi.resendOtp(ResendOtpRequestDto(
     * emailOrPhone = "user@example.com",
     * purpose = OtpPurpose.EMAIL_VERIFICATION
     * ))
     * println("New OTP sent to email.")
     * } catch (e: Exception) {
     * println("Failed to resend OTP: ${e.message}")
     * }
     * ```
     */
    @POST("auth/resend-otp")
    suspend fun resendOtp(@Body request: ResendOtpRequestDto)

    // --- User Profile Management ---

    /**
     * Fetches the profile of the currently authenticated user.
     *
     * This endpoint provides comprehensive details of the logged-in user, including
     * their general information and any role-specific data (e.g., student, teacher details).
     *
     * @return A [UserDto] containing the detailed profile of the logged-in user.
     *
     * **Example Use:**
     * ```kotlin
     * try {
     * val myProfile = userApi.getUserProfile()
     * println("Welcome, ${myProfile.firstName} ${myProfile.lastName}!")
     * if (myProfile.activeRoles.contains("STUDENT")) {
     * println("Student ID: ${myProfile.studentDetails?.studentId}")
     * }
     * } catch (e: Exception) {
     * println("Failed to load profile: ${e.message}")
     * }
     * ```
     */
    @GET("users/me")
    suspend fun getUserProfile(): UserDto

    /**
     * Fetches a user's profile by their ID.
     *
     * This endpoint is typically used by administrators or users with specific permissions
     * to view other users' profiles.
     *
     * @param userId The ID of the user whose profile is to be fetched.
     * @return A [UserDto] for the specified user.
     *
     * **Example Use (Admin Use Case):**
     * ```kotlin
     * try {
     * val studentProfile = userApi.getUserById("STU_12345")
     * println("Viewing profile for: ${studentProfile.firstName} ${studentProfile.lastName}")
     * } catch (e: Exception) {
     * println("Failed to fetch user: ${e.message}")
     * }
     * ```
     */
    @GET("users/{userId}")
    suspend fun getUserById(@Path("userId") userId: String): UserDto

    /**
     * Updates the profile of the currently authenticated user.
     *
     * This endpoint allows a logged-in user to modify their own general profile information.
     *
     * @param request The [UpdateUserRequestDto] containing the fields to update. Only provided
     * (non-null) fields will be considered for the update.
     * @return The updated [UserDto] profile after the changes have been applied.
     *
     * **Example Use:**
     * ```kotlin
     * try {
     * val updateRequest = UpdateUserRequestDto(
     * phoneNumber = "+250788999888",
     * profilePictureUrl = "[https://new.profile.pic/url.jpg](https://new.profile.pic/url.jpg)"
     * )
     * val updatedProfile = userApi.updateMyProfile(updateRequest)
     * println("Profile updated. New phone: ${updatedProfile.phoneNumber}")
     * } catch (e: Exception) {
     * println("Failed to update profile: ${e.message}")
     * }
     * ```
     */
    @PUT("users/me")
    suspend fun updateMyProfile(@Body request: UpdateUserRequestDto): UserDto

    /**
     * Allows the currently authenticated user to **temporarily suspend their account**.
     *
     * A suspended account might retain its data but be inactive, preventing login until reactivated.
     * This is useful for users taking a break or pausing their activity.
     *
     * @return A [Unit] (empty response) upon successful account suspension.
     *
     * **Example Use:**
     * ```kotlin
     * try {
     * // User confirms they want to suspend their account
     * userApi.suspendMyAccount()
     * println("Account suspended successfully. You can reactivate it later.")
     * } catch (e: Exception) {
     * println("Failed to suspend account: ${e.message}")
     * }
     * ```
     */
    @POST("users/me/suspend") // Or PUT if updating a status field
    suspend fun suspendMyAccount()

    /**
     * Allows the currently authenticated user to **permanently delete their account and associated data**.
     *
     * This action is usually irreversible and requires strong confirmation from the user (e.g., password re-entry, OTP).
     *
     * @return A [Unit] (empty response) upon successful account deletion.
     *
     * **Example Use:**
     * ```kotlin
     * try {
     * // User confirms with password/OTP
     * userApi.deleteMyProfile()
     * println("Account deleted successfully. Sorry to see you go!")
     * // Log out user locally after this
     * } catch (e: Exception) {
     * println("Failed to delete account: ${e.message}")
     * }
     * ```
     */
    @DELETE("users/me")
    suspend fun deleteMyProfile()

    /**
     * Allows the currently authenticated user to **request an export of their personal data**.
     *
     * This adheres to data privacy regulations (e.g., GDPR, local equivalents) allowing users
     * to obtain a copy of the data the platform holds about them. The data might be sent via email
     * or made available for download.
     *
     * @return A [Unit] (empty response) upon successful data export request initiation.
     *
     * **Example Use:**
     * ```kotlin
     * try {
     * userApi.requestDataExport()
     * println("Your data export request has been submitted. You will receive an email shortly.")
     * } catch (e: Exception) {
     * println("Failed to request data export: ${e.message}")
     * }
     * ```
     */
    @POST("users/me/data-export")
    suspend fun requestDataExport()

    // --- Admin-level User Management ---

    /**
     * Allows an administrator to **deactivate a user's account**.
     *
     * This is typically used by the company or school administrators to block a user's access
     * due to policy violations, suspicious activities, or administrative reasons.
     * The account remains in the system but cannot be logged into.
     *
     * @param userId The ID of the user whose account is to be deactivated.
     * @param reason An optional string explaining the reason for deactivation (for auditing).
     * @return A [Unit] (empty response) upon successful user deactivation.
     *
     * **Example Use (Admin Action):**
     * ```kotlin
     * try {
     * userApi.deactivateUserByAdmin("USR_98765", "Repeated scam attempts.")
     * println("User USR_98765 deactivated.")
     * } catch (e: Exception) {
     * println("Failed to deactivate user: ${e.message}")
     * }
     * ```
     */
    @POST("admin/users/{userId}/deactivate") // Or PUT if updating a status field on the user resource
    suspend fun deactivateUserByAdmin(
        @Path("userId") userId: String,
        @Body reason: String? = null // Simple example, could be a more complex DTO
    )

    // --- Role Management ---

    /**
     * Allows a user to **request a specific role** (e.g., "TEACHER", "PARENT", "SCHOOL_ADMIN").
     *
     * This initiates a workflow where the request needs to be reviewed and approved by an
     * authorized party (e.g., school admin, system admin). It often involves providing
     * supporting documentation or verification details.
     *
     * @param request The [RoleRequestDto] containing the requested role and supporting details.
     * @return A [Unit] (empty response) upon successful role request submission.
     *
     * **Example Use (User requesting parent role):**
     * ```kotlin
     * // Need a RoleRequestDto definition
     * // For Rwandan context: parent_national_id, child_student_id, family_card_document_url
     * val parentRoleRequest = RoleRequestDto(
     * requestedRole = "PARENT",
     * justification = "I am the parent of student STU_12345.",
     * supportingDocumentsUrls = listOf("[https://docs.school.com/national_id_parent.jpg](https://docs.school.com/national_id_parent.jpg)")
     * // ... add specific parent verification fields here
     * )
     * try {
     * userApi.requestRole(parentRoleRequest)
     * println("Parent role request submitted for verification.")
     * } catch (e: Exception) {
     * println("Role request failed: ${e.message}")
     * }
     * ```
     */
    @POST("users/me/roles/request")
    suspend fun requestRole(@Body request: RoleRequestDto)

    /**
     * Allows an administrator to **revoke a specific role** from a user.
     *
     * This endpoint is used to remove a user's privileges for a certain role,
     * e.g., if a teacher leaves the school, or a parent's linkage is deemed invalid.
     *
     * @param userId The ID of the user from whom the role is being revoked.
     * @param roleName The name of the role to revoke (e.g., "TEACHER", "PARENT").
     * @param reason An optional string explaining the reason for revocation (for auditing).
     * @return A [Unit] (empty response) upon successful role revocation.
     *
     * **Example Use (Admin revoking role):**
     * ```kotlin
     * try {
     * userApi.revokeRoleByAdmin("TCHR_67890", "TEACHER", "Teacher left the school.")
     * println("TEACHER role revoked from TCHR_67890.")
     * } catch (e: Exception) {
     * println("Role revocation failed: ${e.message}")
     * }
     * ```
     */
    @DELETE("admin/users/{userId}/roles/{roleName}") // Or a PUT to update roles via a list if more complex
    suspend fun revokeRoleByAdmin(
        @Path("userId") userId: String,
        @Path("roleName") roleName: String,
        @Query("reason") reason: String? = null // Simple example, could be a body with more details
    )
}