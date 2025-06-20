package com.schoolbridge.v2.ui.home.grade

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.schoolbridge.v2.domain.academic.GradeSummary
import com.schoolbridge.v2.ui.common.components.AppSubHeader
import com.schoolbridge.v2.ui.common.components.SpacerS

@Composable
fun GradesSummarySection(modifier: Modifier = Modifier) {
    val dummyGrades = listOf(
        GradeSummary("Mathematics", 87, "Mr. Kamali"),
        GradeSummary("Biology", 61, "Ms. Uwase"),
        GradeSummary("English", 45, "Mrs. Mukeshimana")
    )

    Column(modifier = modifier.fillMaxWidth()) {
        AppSubHeader("📊 Recent Grades")
        SpacerS()
        dummyGrades.forEachIndexed { index, grade ->
            GradeCardCompact(grade, index)
        }
    }
}