package com.schoolbridge.v2.ui.home.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.R
import com.schoolbridge.v2.domain.academic.TodayCourse
import com.schoolbridge.v2.localization.t
import com.schoolbridge.v2.ui.components.AppSubHeader


@Composable
fun TodayScheduleSection(
    onWeeklyViewClick: () -> Unit,
    modifier: Modifier = Modifier) {
    val dummySchedules = listOf(
        TodayCourse("Mathematics", "08:00", "09:40", "Mr. Kamali", "Room A1"),
        TodayCourse("Chemistry", "10:00", "11:40", "Ms. Uwase", "Lab 3"),
        TodayCourse("History", "13:00", "14:40", "Mr. Habimana", "Room B2")
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppSubHeader("📅 Today’s Schedule")
            TextButton(onClick = onWeeklyViewClick) {
                Text(
                    text = t(R.string.weekly_view),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            dummySchedules.forEach { course ->
                item {
                    TodayScheduleCard(course)
                }
            }
        }
    }
}