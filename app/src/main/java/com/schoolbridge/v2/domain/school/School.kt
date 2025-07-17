package com.schoolbridge.v2.domain.school

import com.schoolbridge.v2.domain.academic.EducationLevel
import com.schoolbridge.v2.domain.finance.BankAccount
import com.schoolbridge.v2.domain.geo.District
import com.schoolbridge.v2.domain.geo.Sector

/**
 * Represents a school entity with general information and administrative data.
 *
 * This class captures the essential data to identify and describe a school,
 * including its location, contact information, education levels offered, and financial details.
 *
 * Example:
 * ```
 * val school = School(
 *   id = "sch_001",
 *   name = "Green Hills Academy",
 *   abbrevName = "GHA",
 *   logoUrl = "https://cdn.schoolbridge.com/schools/gha-logo.png",
 *   educationLevelsOffered = listOf(EducationLevel.PRIMARY, EducationLevel.JUNIOR_SECONDARY),
 *   hasBoarding = true,
 *   contactPhone = "+250788123456",
 *   contactEmail = "info@gha.rw",
 *   websiteUrl = "https://greenhills.rw",
 *   bankAccounts = listOf(BankAccount(bankName = "BK", accountNumber = "123456789")),
 *   district = District(id = "d01", name = "Kigali"),
 *   sector = Sector(id = "s01", name = "Kacyiru")
 * )
 * ```
 *
 * @property id Unique identifier of the school.
 * @property name Full official name of the school.
 * @property abbrevName Short or abbreviated name used for display.
 * @property logoUrl URL or path to the school's logo image.
 * @property educationLevelsOffered List of education levels offered by this school.
 * Examples include PRIMARY, JUNIOR_SECONDARY, SENIOR_SECONDARY.
 * @property hasBoarding Whether the school offers boarding facilities.
 * @property contactPhone Primary phone number for school contact.
 * @property contactEmail Optional email address for general inquiries.
 * @property websiteUrl Optional official website URL of the school.
 * @property bankAccounts List of bank accounts used by the school for fee payments.
 * @property district District where the school is physically located.
 * @property sector Sector within the district where the school is located.
 */
data class School(
    val id: String,
    val name: String,
    val abbrevName: String,
    val logoUrl: String,
    val educationLevelsOffered: List<SchoolLevel>,
    val hasBoarding: Boolean,
    val contactPhone: String,
    val contactEmail: String?,
    val websiteUrl: String?,
    val bankAccounts: List<BankAccount>,
    val district: District,
    val sector: Sector,
    val establishedYear: Int,
    val rating: Double,
    val studentsCount: Int,
    val type: String
) {

}
