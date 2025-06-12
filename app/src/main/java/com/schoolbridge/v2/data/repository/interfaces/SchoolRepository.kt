package com.schoolbridge.v2.data.repository.interfaces

import com.schoolbridge.v2.data.dto.messaging.SchoolAlertDto
import com.schoolbridge.v2.data.dto.messaging.SchoolEventDto
import com.schoolbridge.v2.data.dto.messaging.SchoolMessageDto
import com.schoolbridge.v2.data.dto.school.JuniorSecondarySchoolDto
import com.schoolbridge.v2.data.dto.school.NurserySchoolDto
import com.schoolbridge.v2.data.dto.school.PrimarySchoolDto
import com.schoolbridge.v2.data.dto.school.SchoolLevelDto
import com.schoolbridge.v2.data.dto.school.SchoolLevelOfferingDto
import com.schoolbridge.v2.data.dto.school.SchoolSectionDto
import com.schoolbridge.v2.data.dto.school.SeniorSecondarySchoolDto
import com.schoolbridge.v2.data.dto.school.UniversityDto
import com.schoolbridge.v2.data.dto.settings.SchoolSettingsDto

// You would need to import your DTOs here, e.g.:
// import com.schoolvridge.v2.data.dto.SchoolDto // Assuming this basic DTO exists
// import com.schoolvridge.v2.data.dto.SchoolSettingsDto
// import com.schoolvridge.v2.data.dto.SchoolLevelDto
// import com.schoolvridge.v2.data.dto.SchoolLevelOfferingDto
// import com.schoolvridge.v2.data.dto.NurserySchoolDto
// import com.schoolvridge.v2.data.dto.PrimarySchoolDto
// import com.schoolvridge.v2.data.dto.JuniorSecondarySchoolDto
// import com.schoolvridge.v2.data.dto.SeniorSecondarySchoolDto
// import com.schoolvridge.v2.data.dto.UniversityDto
// import com.schoolvridge.v2.data.dto.SchoolSectionDto
// import com.schoolvridge.v2.data.dto.SchoolAlertDto
// import com.schoolvridge.v2.data.dto.SchoolMessageDto
// import com.schoolvridge.v2.data.dto.SchoolEventDto

/**
 * Interface for the **School Data Repository**.
 *
 * This repository defines the contract for accessing and managing overarching school information,
 * including its profile, settings, educational levels offered, internal sections, and broadcast
 * communications like alerts, messages, and events.
 *
 * **Typical methods it would expose:**
 * -   Retrieving and updating school profiles and settings.
 * -   Managing educational levels and sections offered by a school.
 * -   Handling school-wide alerts, messages, and events.
 */
interface SchoolRepository {

    // School Profile
    suspend fun getSchoolById(schoolId: String): Any? // Replace Any with your SchoolDto
    suspend fun updateSchoolProfile(school: Any): Any // Replace Any with your SchoolDto
    suspend fun createSchool(school: Any): Any // Replace Any with your SchoolDto

    // School Settings
    suspend fun getSchoolSettings(schoolId: String): SchoolSettingsDto?
    suspend fun updateSchoolSettings(settings: SchoolSettingsDto): SchoolSettingsDto

    // School Levels (Definitions) - If these are managed dynamically
    suspend fun getSchoolLevelById(levelId: String): SchoolLevelDto?
    suspend fun getAllSchoolLevels(): List<SchoolLevelDto>

    // School Level Offerings (Instances for a specific school)
    suspend fun getSchoolLevelOfferingById(offeringId: String): SchoolLevelOfferingDto?
    suspend fun getSchoolLevelOfferingsBySchool(schoolId: String): List<SchoolLevelOfferingDto>
    suspend fun createNurseryOffering(offering: NurserySchoolDto): NurserySchoolDto
    suspend fun updateNurseryOffering(offering: NurserySchoolDto): NurserySchoolDto
    suspend fun createPrimaryOffering(offering: PrimarySchoolDto): PrimarySchoolDto
    suspend fun updatePrimaryOffering(offering: PrimarySchoolDto): PrimarySchoolDto
    suspend fun createJuniorSecondaryOffering(offering: JuniorSecondarySchoolDto): JuniorSecondarySchoolDto
    suspend fun updateJuniorSecondaryOffering(offering: JuniorSecondarySchoolDto): JuniorSecondarySchoolDto
    suspend fun createSeniorSecondaryOffering(offering: SeniorSecondarySchoolDto): SeniorSecondarySchoolDto
    suspend fun updateSeniorSecondaryOffering(offering: SeniorSecondarySchoolDto): SeniorSecondarySchoolDto
    suspend fun createUniversityOffering(offering: UniversityDto): UniversityDto
    suspend fun updateUniversityOffering(offering: UniversityDto): UniversityDto


    // School Sections
    suspend fun getSchoolSectionById(sectionId: String): SchoolSectionDto?
    suspend fun getSchoolSectionsByLevelOffering(offeringId: String): List<SchoolSectionDto>
    suspend fun createSchoolSection(section: SchoolSectionDto): SchoolSectionDto
    suspend fun updateSchoolSection(section: SchoolSectionDto): SchoolSectionDto
    suspend fun deleteSchoolSection(sectionId: String): Boolean

    // School Alerts
    suspend fun getSchoolAlertById(alertId: String): SchoolAlertDto?
    suspend fun getActiveSchoolAlerts(schoolId: String): List<SchoolAlertDto>
    suspend fun createSchoolAlert(alert: SchoolAlertDto): SchoolAlertDto
    suspend fun updateSchoolAlert(alert: SchoolAlertDto): SchoolAlertDto
    suspend fun deleteSchoolAlert(alertId: String): Boolean

    // School Messages (Announcements)
    suspend fun getSchoolMessageById(messageId: String): SchoolMessageDto?
    suspend fun getRecentSchoolMessages(schoolId: String, limit: Int = 10): List<SchoolMessageDto>
    suspend fun createSchoolMessage(message: SchoolMessageDto): SchoolMessageDto
    suspend fun updateSchoolMessage(message: SchoolMessageDto): SchoolMessageDto
    suspend fun deleteSchoolMessage(messageId: String): Boolean

    // School Events
    suspend fun getSchoolEventById(eventId: String): SchoolEventDto?
    suspend fun getSchoolEventsByDateRange(schoolId: String, startDate: String, endDate: String): List<SchoolEventDto>
    suspend fun createSchoolEvent(event: SchoolEventDto): SchoolEventDto
    suspend fun updateSchoolEvent(event: SchoolEventDto): SchoolEventDto
    suspend fun deleteSchoolEvent(eventId: String): Boolean
}