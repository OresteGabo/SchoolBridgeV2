package com.schoolbridge.v2.data.repository.interfaces

import com.schoolbridge.v2.data.dto.academic.TransferRequestDto
import com.schoolbridge.v2.data.dto.academic.WaitlistEntryDto

// This is an interface (a contract), not an implementation.
// The actual implementation (e.g., EnrollmentRepositoryImpl) would connect to your student management database.
// This is an interface (a contract), not an implementation.
// The actual implementation (e.g., EnrollmentRepositoryImpl) would connect to your student management database.

// You would need to import relevant DTOs, e.g.:
// import com.schoolvridge.v2.data.dto.StudentDto
// import com.schoolvridge.v2.data.dto.EnrollmentDto
// import com.schoolvridge.v2.data.dto.AcademicYearDto
// import com.schoolvridge.v2.data.dto.SchoolLevelOfferingDto
// import com.schoolvridge.v2.data.dto.SchoolSectionDto
// import com.schoolvridge.v2.data.dto.AdmissionApplicationDto
// import com.schoolvridge.v2.data.dto.WaitlistEntryDto // NEW
// import com.schoolvridge.v2.data.dto.TransferRequestDto // NEW

/**
 * Interface for the **Enrollment Data Repository**.
 *
 * This repository defines the contract for managing student enrollment processes,
 * including admissions applications, student placement into academic years, school levels, and sections,
 * as well as handling waitlists and student transfer requests.
 *
 * **Typical methods it would expose:**
 * -   Managing admissions applications.
 * -   Handling student waitlists.
 * -   Processing internal and external student transfer requests.
 * -   Enrolling new students.
 * -   Retrieving student enrollment status.
 * -   Assigning students to classes, levels, or sections.
 * -   Managing student withdrawals and re-enrollment.
 */
interface EnrollmentRepository {

    // --- Admissions Management ---
    suspend fun createAdmissionApplication(application: Any): Any // Replace Any with AdmissionApplicationDto
    suspend fun getAdmissionApplicationById(applicationId: String): Any? // Replace Any? with AdmissionApplicationDto
    suspend fun getAdmissionApplicationsByStatus(status: String, schoolId: String? = null, academicYearId: String? = null): List<Any> // Replace Any with AdmissionApplicationDto
    suspend fun updateAdmissionApplicationStatus(applicationId: String, newStatus: String): Any // Replace Any with AdmissionApplicationDto
    suspend fun getAdmissionApplicationsForSchoolLevel(schoolLevelOfferingId: String, academicYearId: String): List<Any> // Replace Any with AdmissionApplicationDto

    // --- Waitlist Management ---
    suspend fun addStudentToWaitlist(waitlistEntry: WaitlistEntryDto): WaitlistEntryDto
    suspend fun getWaitlistEntryById(entryId: String): WaitlistEntryDto?
    suspend fun getWaitlistEntriesForSchoolLevel(schoolLevelOfferingId: String, academicYearId: String, status: String? = null): List<WaitlistEntryDto>
    suspend fun getWaitlistEntriesByStudent(studentId: String): List<WaitlistEntryDto>
    suspend fun updateWaitlistEntryStatus(entryId: String, newStatus: String): WaitlistEntryDto
    suspend fun removeStudentFromWaitlist(entryId: String): Boolean
    // Method to convert a waitlist entry into an enrollment (if offer accepted)
    suspend fun convertWaitlistToEnrollment(entryId: String, enrollmentDetails: Any): Any // Replace Any with EnrollmentDto

    // --- Transfer Request Management ---
    suspend fun createTransferRequest(transferRequest: TransferRequestDto): TransferRequestDto
    suspend fun getTransferRequestById(requestId: String): TransferRequestDto?
    suspend fun getTransferRequestsByStudent(studentId: String): List<TransferRequestDto>
    suspend fun getTransferRequestsForSchool(schoolId: String, status: String? = null): List<TransferRequestDto>
    suspend fun updateTransferRequestStatus(requestId: String, newStatus: String, notes: String? = null): TransferRequestDto
    suspend fun approveTransferRequest(requestId: String, enrollmentDetails: Any?): Any // Replace Any with EnrollmentDto, if approval leads to auto-enrollment
    suspend fun rejectTransferRequest(requestId: String, reason: String): TransferRequestDto
    suspend fun cancelTransferRequest(requestId: String): TransferRequestDto

    // --- Core Enrollment Management ---
    suspend fun enrollStudent(studentId: String, academicYearId: String, schoolLevelOfferingId: String): Any // Replace Any with EnrollmentDto
    suspend fun getEnrollmentById(enrollmentId: String): Any? // Replace Any? with EnrollmentDto
    suspend fun getEnrollmentsByStudent(studentId: String): List<Any> // Replace Any with EnrollmentDto
    suspend fun getStudentsBySchoolLevel(schoolLevelId: String, academicYearId: String): List<Any> // Replace Any with StudentDto
    suspend fun getStudentsBySection(sectionId: String, academicYearId: String): List<Any> // Replace Any with StudentDto
    suspend fun updateEnrollmentStatus(enrollmentId: String, newStatus: String): Any // Replace Any with EnrollmentDto
    suspend fun withdrawStudent(enrollmentId: String): Boolean // Or return updated EnrollmentDto
    suspend fun assignStudentToSection(studentId: String, sectionId: String, academicYearId: String): Boolean

    // --- Re-enrollment ---
    suspend fun reEnrollStudent(studentId: String, academicYearId: String, schoolLevelOfferingId: String): Any // Replace Any with EnrollmentDto
}