package com.schoolbridge.v2.ui.home.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.schoolbridge.v2.R
import com.schoolbridge.v2.domain.academic.TodayCourse
import com.schoolbridge.v2.domain.academic.schedule.TodayScheduleViewModel
import com.schoolbridge.v2.localization.t
import com.schoolbridge.v2.ui.components.AppSubHeader
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

@Composable
fun TodayScheduleSection(
    onWeeklyViewClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TodayScheduleViewModel = viewModel()
) {
    val courses by viewModel.courses.collectAsState()

    Column(modifier = modifier.fillMaxWidth()) {

        /* Header row */
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

        /* Schedule list */
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (courses.isEmpty()) {
                item {
                    Text(
                        text = t(R.string.no_courses_today),           // add to strings.xml
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(courses.size) { index ->
                    TodayScheduleCard(courses[index])
                }
            }
        }
    }
}
