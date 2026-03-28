package com.schoolbridge.v2.ui.academic.school

import com.schoolbridge.v2.domain.finance.BankAccount
import com.schoolbridge.v2.domain.school.School
import com.schoolbridge.v2.domain.school.SchoolLevel
import com.schoolbridge.v2.domain.school.SchoolSection
import com.schoolbridge.v2.util.getDistrictByName
import com.schoolbridge.v2.util.getSectorByDistringNameAndSectorName

val previewSchoolProfile = School(
    id = "school-gha",
    name = "Green Hills Academy",
    abbrevName = "GHA",
    logoUrl = "https://cdn.schoolbridge.com/logos/gha-logo.png",
    educationLevelsOffered = listOf(
        SchoolLevel.ALevel(
            name = "",
            id = "",
            section = SchoolSection("MCB", "MCB")
        ),
        SchoolLevel.OLevel(
            id = "O level",
            name = "O level"
        )
    ),
    hasBoarding = true,
    contactPhone = "+250788123456",
    contactEmail = "info@greenhillsacademy.rw",
    websiteUrl = "https://greenhillsacademy.rw",
    bankAccounts = listOf(
        BankAccount(bankName = "Bank of Kigali", accountNumber = "000123456789")
    ),
    district = getDistrictByName("Gasabo"),
    sector = getSectorByDistringNameAndSectorName("Gasabo", "Kacyiru"),
    establishedYear = 1888,
    rating = 3.1,
    studentsCount = 766,
    type = "public"
)
