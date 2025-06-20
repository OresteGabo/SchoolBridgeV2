package com.schoolbridge.v2.ui.home.course

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.domain.academic.Course
import com.schoolbridge.v2.domain.academic.CourseStatus
import com.schoolbridge.v2.domain.academic.CourseWithStatus
import com.schoolbridge.v2.ui.common.components.AppSubHeader
import com.schoolbridge.v2.ui.common.components.SpacerS
import java.time.LocalDate

@Composable
fun CourseListSection(
    modifier: Modifier = Modifier
) {
    val dummyCourses = listOf(
        CourseWithStatus(
            course = Course(
                id = "c1",
                name = "Mathematics Grade 10",
                description = "Comprehensive study of algebra and geometry.",
                subjectId = "subj-math",
                academicYearId = "AY2024-2025",
                schoolLevelOfferingId = "slo-g10-sci",
                teacherUserIds = listOf("teacher1"),
                startDate = LocalDate.of(2024, 9, 1),
                endDate = LocalDate.of(2025, 5, 30),
                isActive = false
            ),
            status = CourseStatus.VALIDATED
        ),
        CourseWithStatus(
            course = Course(
                id = "c2",
                name = "Biology S3",
                description = "Detailed study of living organisms and life processes.",
                subjectId = "subj-bio",
                academicYearId = "AY2024-2025",
                schoolLevelOfferingId = "slo-s3-sci",
                teacherUserIds = listOf("teacher2"),
                startDate = LocalDate.of(2024, 9, 1),
                endDate = LocalDate.of(2025, 5, 30),
                isActive = true
            ),
            status = CourseStatus.IN_PROGRESS
        ),
        CourseWithStatus(
            course = Course(
                id = "c3",
                name = "English Literature",
                description = "Exploration of classic and modern literary works.",
                subjectId = "subj-eng",
                academicYearId = "AY2024-2025",
                schoolLevelOfferingId = "slo-g10-art",
                teacherUserIds = listOf("teacher3"),
                startDate = LocalDate.of(2024, 9, 1),
                endDate = LocalDate.of(2025, 5, 30),
                isActive = false
            ),
            status = CourseStatus.RETAKE_REQUIRED
        ),
        CourseWithStatus(
            course = Course(
                id = "c4",
                name = "History Grade 10",
                description = "A survey of world history with focus on Africa.",
                subjectId = "subj-hist",
                academicYearId = "AY2024-2025",
                schoolLevelOfferingId = "slo-g10-art",
                teacherUserIds = listOf("teacher4"),
                startDate = LocalDate.of(2024, 9, 1),
                endDate = LocalDate.of(2025, 5, 30),
                isActive = false
            ),
            status = CourseStatus.NOT_VALIDATED
        ),
        CourseWithStatus(
            course = Course(
                id = "c5",
                name = "Physics S5",
                description = "Advanced mechanics, electricity, and waves.",
                subjectId = "subj-phys",
                academicYearId = "AY2024-2025",
                schoolLevelOfferingId = "slo-s5-sci",
                teacherUserIds = listOf("teacher2"),
                startDate = LocalDate.of(2024, 9, 1),
                endDate = LocalDate.of(2025, 5, 30),
                isActive = false
            ),
            status = CourseStatus.AWAITING_RESULTS
        )
    )


    Row(modifier = modifier.fillMaxWidth()) {
        AppSubHeader("📚 " + "Courses")
    }
    SpacerS()
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        itemsIndexed(dummyCourses) { _, courseWithStatus ->
            CourseCard(courseWithStatus)
        }
    }
}