package com.schoolbridge.v2.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Construction
import com.schoolbridge.v2.domain.academic.*
import com.schoolbridge.v2.domain.finance.BankAccount
import com.schoolbridge.v2.domain.school.*
import com.schoolbridge.v2.ui.settings.about.VersionInfo
import com.schoolbridge.v2.ui.settings.help.FAQCategory
import com.schoolbridge.v2.ui.settings.help.FAQItem
import java.time.LocalDate
val sampleSchool = School(
    id = "school-gha",
    name = "Green Hills Academy",
    abbrevName = "GHA",
    logoUrl = "https://cdn.schoolbridge.com/logos/gha-logo.png",
   
    educationLevelsOffered = listOf(
        SchoolLevel.ALevel(
            name = "",
            id = "",
            section = SchoolSection("MCB", "MCB"),
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

// Version metadata
val versions = listOf(
    VersionInfo(
        title = "V1: Umusingi",
        meaning = "“Umusingi” means foundation, the solid base of our journey.",
        releaseDate = "May 2025",
        focus = "Core infrastructure, identity, and onboarding",
        future = "Stability & groundwork for AI & automation",
        icon = Icons.Default.Construction,
        isCurrent = true
    ),
    /*VersionInfo(
        title = "V2: Intambwe",
        meaning = "“Intambwe” means step — progress toward reports and live updates.",
        releaseDate = "Planned: Late 2025",
        focus = "Performance, real-time sync, and academic reports",
        future = "Multi-student insights, offline mode",
        icon = Icons.AutoMirrored.Filled.TrendingUp,
        isCurrent = false
    ),
    VersionInfo(
        title = "V3: Urumuri",
        meaning = "“Urumuri” means light — guidance via AI and smart feedback.",
        releaseDate = "Planned: Early 2026",
        focus = "AI assistance, guidance, personalization",
        future = "Adaptive learning and parent-teacher analytics",
        icon = Icons.Default.LightMode,
        isCurrent = false
    )*/
)


fun generateFAQs(): List<FAQCategory> {
    return listOf(
        FAQCategory(
            "Linking & Identity", listOf(
                FAQItem(
                    question = "Where can I get a linking code?",
                    answer = "When you don’t have a linking code, request one via the app or contact the school. Once validated, your link is approved. It may also be rejected or require more info.",
                    tags = listOf("link", "code", "linking", "request", "identity", "connect", "parent", "child", "school")
                ),
                FAQItem(
                    question = "How does the app verify my identity?",
                    answer = "We use MoMo API to check if your submitted names match your registered MoMo names.",
                    tags = listOf("identity", "verify", "verification", "momo", "names", "security")
                ),
                FAQItem(
                    question = "Why do you request my ID number?",
                    answer = "Some schools use it to confirm your identity as a parent.",
                    tags = listOf("id", "identity", "verification", "parent", "linking", "security")
                ),
                FAQItem(
                    question = "What if my child is already linked to another parent?",
                    answer = "The school reviews and approves additional parent link requests manually.",
                    tags = listOf("linked", "parent", "child", "request", "manual", "approval")
                ),
                FAQItem(
                    question = "Can I link more than one child?",
                    answer = "Yes. You can link to multiple children, even across different schools.",
                    tags = listOf("link", "children", "multiple", "schools", "parent", "family")
                ),
                FAQItem(
                    question = "What does 'link pending' mean?",
                    answer = "The school received your request but hasn’t approved or rejected it yet.",
                    tags = listOf("pending", "link", "request", "status", "school")
                ),
                FAQItem(
                    question = "How long does it take to approve a link request?",
                    answer = "This varies by school. Most review within 1–3 days.",
                    tags = listOf("link", "request", "approval", "school", "time", "waiting")
                ),
                FAQItem(
                    question = "Can guardians or relatives also link to a child?",
                    answer = "Yes, if the school allows. You’ll need to request and explain your relationship.",
                    tags = listOf("guardian", "relative", "parent", "link", "child", "request")
                ),
                FAQItem(
                    question = "What if I entered the wrong details during linking?",
                    answer = "Cancel the request and submit again. Or contact the school for corrections.",
                    tags = listOf("wrong", "error", "details", "linking", "fix", "correction")
                ),
                FAQItem(
                    question = "Can a school deny my linking request?",
                    answer = "Yes. They can deny or ask for more info if they can’t confirm your relationship.",
                    tags = listOf("deny", "rejected", "linking", "school", "request", "relationship")
                )
            )
        ),
        FAQCategory(
            "Payments", listOf(
                FAQItem(
                    question = "If I pay with the bank, how does it appear in the app?",
                    answer = "Take the bank receipt, copy the reference number, and submit it in the app. The transaction stays pending until confirmed by the school.",
                    tags = listOf("bank", "payment", "money", "finance", "receipt", "reference", "pending", "transaction")
                ),
                FAQItem(
                    question = "What if I pay with MoMo?",
                    answer = "Once linked to a student, their school appears in your SchoolBridge contacts. You’ll find MoMo codes and account details there.",
                    tags = listOf("momo", "payment", "money", "school", "contact", "account", "transfer")
                ),
                FAQItem(
                    question = "What if I enter the wrong reference number?",
                    answer = "The school may ask for more info, such as a photo of the bank receipt.",
                    tags = listOf("reference", "error", "receipt", "bank", "finance", "fix", "mistake", "money")
                ),
                FAQItem(
                    question = "Is there a deadline to confirm pending payments?",
                    answer = "Each school sets its own timeframe. If it takes too long, contact the finance office.",
                    tags = listOf("deadline", "pending", "payment", "money", "finance", "school", "time")
                ),
                FAQItem(
                    question = "Can I get a receipt in the app?",
                    answer = "Yes, you can download receipts for confirmed payments.",
                    tags = listOf("receipt", "download", "payment", "money", "proof", "finance")
                ),
                FAQItem(
                    question = "Can I schedule future payments?",
                    answer = "Not yet. For now, all payments must be made manually when due.",
                    tags = listOf("schedule", "future", "payment", "finance", "plan", "money", "manual")
                ),
                FAQItem(
                    question = "What happens if my payment is delayed?",
                    answer = "The school may apply late fees or restrict services. Always communicate with the finance office.",
                    tags = listOf("late", "delay", "fees", "payment", "finance", "money", "school", "penalty")
                ),
                FAQItem(
                    question = "Will I receive a confirmation after payment?",
                    answer = "Yes. Once confirmed by the school, a receipt appears in the app.",
                    tags = listOf("confirmation", "receipt", "payment", "money", "status", "finance")
                ),
                FAQItem(
                    question = "Can I pay part of the school fees?",
                    answer = "It depends on the school’s policy. Some allow installments, others require full payment.",
                    tags = listOf("installment", "partial", "payment", "school fees", "money", "finance", "plan")
                )
            )
        ),
        FAQCategory(
            "Messages", listOf(
                FAQItem(
                    question = "Why can’t I find past messages?",
                    answer = "Closed threads are deleted after 300 days unless favorited under a premium plan.",
                    tags = listOf("messages", "deleted", "archived", "history", "premium", "thread")
                ),
                FAQItem(
                    question = "Can I reply to school announcements?",
                    answer = "No. Announcements are one-way. You can only reply in open threads.",
                    tags = listOf("reply", "announcement", "school", "communication", "messages", "thread")
                ),
                FAQItem(
                    question = "How do I know if my message was read?",
                    answer = "Read status may not appear. For urgent matters, create a new thread or contact the school.",
                    tags = listOf("read", "seen", "message", "status", "communication", "thread")
                )
            )
        ),
        FAQCategory(
            "Account & Usage", listOf(
                FAQItem(
                    question = "Can I use my account on multiple phones?",
                    answer = "Yes, but you may need to re-verify your identity.",
                    tags = listOf("account", "multiple", "phones", "login", "access", "device")
                ),
                FAQItem(
                    question = "What happens if I change my phone number?",
                    answer = "Update it in the Profile screen. Your new number will be verified again.",
                    tags = listOf("phone number", "change", "account", "update", "verify", "profile")
                ),
                FAQItem(
                    question = "Can I remove a linked child?",
                    answer = "This must be done by the school. Contact them to request unlinking.",
                    tags = listOf("unlink", "remove", "child", "account", "school", "linking")
                ),
                FAQItem(
                    question = "Can I delete my SchoolBridge account?",
                    answer = "Yes. Go to Settings → Profile and request account deletion. Note: unlinking must be handled by the school.",
                    tags = listOf("delete", "account", "remove", "profile", "schoolbridge", "settings")
                ),
                FAQItem(
                    question = "Why do I need to re-verify on new phones?",
                    answer = "For security. It ensures you’re the true account owner.",
                    tags = listOf("verify", "phone", "security", "login", "access", "identity")
                ),
                FAQItem(
                    question = "What happens if I uninstall the app?",
                    answer = "Your data remains safe. Reinstall and sign in to access it again.",
                    tags = listOf("uninstall", "data", "account", "access", "login", "restore")
                ),
                FAQItem(
                    question = "Can I receive notifications without opening the app?",
                    answer = "Yes, if you allow notifications. You’ll get updates about messages, links, and finance.",
                    tags = listOf("notifications", "updates", "messages", "link", "finance", "app")
                )
            )
        ),
        FAQCategory(
            "Privacy & Security", listOf(
                FAQItem(
                    question = "Is my data sold to third parties?",
                    answer = "No. We don’t sell personal data.",
                    tags = listOf("data", "privacy", "third party", "sell", "security")
                ),
                FAQItem(
                    question = "Who can access my data?",
                    answer = "Only the government, and only when formally requested. You’ll be notified if this happens.",
                    tags = listOf("data", "access", "government", "privacy", "notification", "security")
                ),
                FAQItem(
                    question = "Is my payment data secure?",
                    answer = "Yes. All data is encrypted and securely stored.",
                    tags = listOf("secure", "payment", "data", "encryption", "money", "finance")
                ),
                FAQItem(
                    question = "How is my data stored?",
                    answer = "Your data is securely stored on servers with restricted access.",
                    tags = listOf("data", "storage", "security", "privacy", "server")
                ),
                // ─── BridgeLock™ section ───
                FAQItem(
                    question = "What is BridgeLock™?",
                    answer = "BridgeLock™ is our internal security layer that ensures only verified SchoolBridge apps can scan or access protected QR codes.",
                    tags = listOf("BridgeLock", "security", "qr code", "protection", "verified")
                ),
                FAQItem(
                    question = "Can other apps scan my SchoolBridge QR code?",
                    answer = "No. QR codes protected by BridgeLock™ are encrypted and readable only by official SchoolBridge apps.",
                    tags = listOf("BridgeLock", "qr", "scan", "privacy", "protection", "security")
                ),
                FAQItem(
                    question = "What does the BridgeLock™ badge mean?",
                    answer = "When you see the BridgeLock™ badge, it means your data is encrypted, app-restricted, and protected according to our privacy policies.",
                    tags = listOf("BridgeLock", "badge", "verified", "security", "privacy")
                ),
                FAQItem(
                    question = "What else does BridgeLock™ protect?",
                    answer = "For now, BridgeLock™ secures QR codes and identity verification. We're working to expand it to login, shared profiles, and synced data.",
                    tags = listOf("BridgeLock", "future", "security", "identity", "login", "privacy")
                )
            )
        ),
        FAQCategory(
            "BridgeLock™ Security", listOf(
                FAQItem(
                    question = "What is BridgeLock™?",
                    answer = "BridgeLock™ is our proprietary security system that ensures only verified SchoolBridge apps can access sensitive data like your QR code or profile.",
                    tags = listOf("BridgeLock", "security", "protection", "qr code", "verified")
                ),
                FAQItem(
                    question = "Can other apps scan my QR code?",
                    answer = "No. QR codes protected by BridgeLock™ are encrypted and can only be decrypted by official SchoolBridge apps.",
                    tags = listOf("BridgeLock", "qr", "scan", "protection", "security", "privacy")
                ),
                FAQItem(
                    question = "Is my QR code safe in Google Wallet?",
                    answer = "Yes. When supported, BridgeLock™-secured codes are added in a read-only, non-payment format, with no personal info exposed to third-party apps.",
                    tags = listOf("BridgeLock", "qr", "wallet", "google pay", "safety", "privacy")
                ),
                FAQItem(
                    question = "What else does BridgeLock™ protect?",
                    answer = "Currently, BridgeLock™ protects your QR code and identity verification. Future updates may include secure login, encrypted sync, and shared data vaults.",
                    tags = listOf("BridgeLock", "security", "future", "features", "protection", "data")
                ),
                FAQItem(
                    question = "How do I know data is BridgeLock™ protected?",
                    answer = "Whenever you see the BridgeLock™ badge, it means your data is encrypted, app-restricted, and privacy-compliant.",
                    tags = listOf("BridgeLock", "badge", "icon", "trust", "security", "privacy")
                ),
            )
        ),
        FAQCategory(
            "Premium & Plans", listOf(
                FAQItem(
                    question = "Why can't I favorite messages yet?",
                    answer = "That feature will be available under a future premium plan.",
                    tags = listOf("favorite", "messages", "premium", "plan", "future", "feature")
                ),
                FAQItem(
                    question = "How can I get a premium plan?",
                    answer = "It’s not available yet. Stay tuned!",
                    tags = listOf("premium", "plan", "upgrade", "subscription", "feature")
                ),
                FAQItem(
                    question = "What are SchoolBridge Contacts?",
                    answer = "They are school profiles linked to your student. They include bank and MoMo details.",
                    tags = listOf("contacts", "school", "profile", "momo", "bank", "money", "payment")
                )
            )
        ),
        FAQCategory(
            "International & Digital Payments", listOf(
                FAQItem(
                    question = "Can I pay from outside Rwanda?",
                    answer = "Yes. Use MoMo Global or a bank that supports international transfers to Rwandan accounts.",
                    tags = listOf("international", "payment", "momo", "bank", "transfer", "foreign", "global", "money")
                ),
                FAQItem(
                    question = "Do you accept PayPal or crypto payments?",
                    answer = "Not at this time. Only bank and MoMo payments are accepted.",
                    tags = listOf("paypal", "crypto", "bitcoin", "money", "payment", "digital", "currency")
                ),
                FAQItem(
                    question = "Do I need a Rwandan phone number for MoMo payments?",
                    answer = "Yes, typically. For international numbers, contact the school or MoMo support.",
                    tags = listOf("phone", "momo", "payment", "international", "number", "money")
                ),
                FAQItem(
                    question = "Do you accept other currencies for payment?",
                    answer = "Yes, if the method supports it. MoMo Global and certain bank transfers may allow payments in other currencies.",
                    tags = listOf("currency", "exchange", "money", "payment", "finance", "bank", "momo")
                ),
                FAQItem(
                    question = "Do you accept Visa or credit card payments?",
                    answer = "We do not accept Visa or credit cards directly on our platform. However, if MoMo or your bank allows it, your payment will still be processed.",
                    tags = listOf("visa", "credit card", "payment", "money", "bank", "finance", "momo")
                )
            )
        )
    )
}