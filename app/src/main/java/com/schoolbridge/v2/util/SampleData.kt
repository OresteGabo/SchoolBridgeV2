package com.schoolbridge.v2.util

import com.schoolbridge.v2.domain.academic.*
import com.schoolbridge.v2.domain.finance.BankAccount
import com.schoolbridge.v2.domain.school.*
import java.time.LocalDate
val sampleSchool = School(
    id = "school-gha",
    name = "Green Hills Academy",
    abbrevName = "GHA",
    logoUrl = "https://cdn.schoolbridge.com/logos/gha-logo.png",
    educationLevelsOffered = listOf(
        EducationLevel(
            id = "edu-primary",
            name = "Primary",
            description = "Primary level education",
            order = 1
        ),
        EducationLevel(
            id = "edu-olevel",
            name = "O’Level",
            description = "Junior Secondary",
            order = 2
        ),
        EducationLevel(
            id = "edu-alevel",
            name = "A’Level",
            description = "Senior Secondary",
            order = 3
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
    sector = getSectorByDistringNameAndSectorName("Gasabo","Kacyiru")
)



val currentAcademicYear = AcademicYear.getAcademicYear(LocalDate.of(2024, 9, 1), isCurrent = true)

val sampleOfferings = listOf(

    // ─── Senior 1 A ───
    SchoolLevelOffering(
        school = sampleSchool,
        schoolLevel = SchoolLevel.OLevel(id = "s1", name = "Senior 1"),
        academicYear = currentAcademicYear,
        stream = "A",
        courses = listOf(
            Course(
                id = "math-s1a", name = "Mathematics S1",
                description = "Intro to algebra and geometry",
                subjectId = "math", academicYearId = currentAcademicYear.id,
                schoolLevelOfferingId = "slo-s1a",
                teacherUserIds = listOf("teacher-1"),
                startDate = LocalDate.of(2024, 9, 2),
                endDate = LocalDate.of(2025, 7, 15),
                isActive = true
            ),
            Course(
                id = "eng-s1a", name = "English S1",
                description = "Grammar and composition",
                subjectId = "eng", academicYearId = currentAcademicYear.id,
                schoolLevelOfferingId = "slo-s1a",
                teacherUserIds = listOf("teacher-2"),
                startDate = LocalDate.of(2024, 9, 2),
                endDate = LocalDate.of(2025, 7, 15),
                isActive = true
            )
        )
    ),

    // ─── Senior 4 Science ───
    SchoolLevelOffering(
        school = sampleSchool,
        schoolLevel = SchoolLevel.ALevel(id = "s4", name = "Senior 4", section = SchoolSection(abbrevName="MCB", name="Math-Chemistry-Biology")),
        academicYear = currentAcademicYear,
        stream = "Science",
        courses = listOf(
            Course(
                id = "phy-s4", name = "Physics S4",
                description = "Mechanics, waves, and electricity",
                subjectId = "phy", academicYearId = currentAcademicYear.id,
                schoolLevelOfferingId = "slo-s4sc",
                teacherUserIds = listOf("teacher-3"),
                startDate = LocalDate.of(2024, 9, 2),
                endDate = LocalDate.of(2025, 7, 15),
                isActive = true
            ),
            Course(
                id = "chem-s4", name = "Chemistry S4",
                description = "Atomic structure and reactions",
                subjectId = "chem", academicYearId = currentAcademicYear.id,
                schoolLevelOfferingId = "slo-s4sc",
                teacherUserIds = listOf("teacher-4"),
                startDate = LocalDate.of(2024, 9, 2),
                endDate = LocalDate.of(2025, 7, 15),
                isActive = true
            ),
            Course(
                id = "bio-s4", name = "Biology S4",
                description = "Cell biology and genetics",
                subjectId = "bio", academicYearId = currentAcademicYear.id,
                schoolLevelOfferingId = "slo-s4sc",
                teacherUserIds = listOf("teacher-5"),
                startDate = LocalDate.of(2024, 9, 2),
                endDate = LocalDate.of(2025, 7, 15),
                isActive = true
            )
        )
    ),

    // ─── Primary 6 ───
    SchoolLevelOffering(
        school = sampleSchool,
        schoolLevel = SchoolLevel.PrimaryLevel(id = "p6", name = "Primary 6"),
        academicYear = currentAcademicYear,
        stream = "B",
        courses = listOf(
            Course(
                id = "math-p6", name = "Mathematics P6",
                description = "Fractions, geometry, and problem solving",
                subjectId = "math", academicYearId = currentAcademicYear.id,
                schoolLevelOfferingId = "slo-p6b",
                teacherUserIds = listOf("teacher-6"),
                startDate = LocalDate.of(2024, 9, 2),
                endDate = LocalDate.of(2025, 7, 15),
                isActive = true
            ),
            Course(
                id = "kiny-p6", name = "Kinyarwanda P6",
                description = "Reading and comprehension",
                subjectId = "kiny", academicYearId = currentAcademicYear.id,
                schoolLevelOfferingId = "slo-p6b",
                teacherUserIds = listOf("teacher-7"),
                startDate = LocalDate.of(2024, 9, 2),
                endDate = LocalDate.of(2025, 7, 15),
                isActive = true
            )
        )
    )
)

// Dummy data: Map teacherUserIds to names for display (in real app this would come from user repository)
val dummyTeacherNames = mapOf(
    "teacher1" to "Mr. Kamali",
    "teacher2" to "Ms. Uwase",
    "teacher3" to "Mrs. Mukeshimana",
    "teacher4" to "Mr. Habimana"
)

val dummyCourses = listOf(
    TodayCourse("Mathematics", "08:00", "09:40", "Mr. Kamali", "Room A1"),
    TodayCourse("French",      "10:00", "11:40", "Mme. Mukamana", "Room B1"),
    TodayCourse("Physics",     "13:00", "14:40", "Mr. Nkurunziza", "Lab 1"),
)


