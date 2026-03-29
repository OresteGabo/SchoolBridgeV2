package com.schoolbridge.v2.ui.home.schedule

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.schoolbridge.v2.R
import com.schoolbridge.v2.data.remote.MessageApiServiceImpl
import com.schoolbridge.v2.data.remote.TimetableApiServiceImpl
import com.schoolbridge.v2.data.repository.implementations.MessagingRepositoryImpl
import com.schoolbridge.v2.data.repository.implementations.TimetableRepositoryImpl
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.localization.t
import com.schoolbridge.v2.ui.common.tutorial.CoachMarkTargetRegistry
import com.schoolbridge.v2.ui.common.tutorial.coachMarkTarget
import com.schoolbridge.v2.ui.components.AppSubHeader
import com.schoolbridge.v2.ui.home.timetable.TimetableViewModel
import com.schoolbridge.v2.ui.home.timetable.TimetableViewModelFactory

@Composable
fun TodayScheduleSection(
    userSessionManager: UserSessionManager,
    onWeeklyViewClick: () -> Unit,
    modifier: Modifier = Modifier,
    weeklyViewModifier: Modifier = Modifier,
    viewModel: TimetableViewModel = viewModel(
        factory = TimetableViewModelFactory(
            timetableRepository = TimetableRepositoryImpl(TimetableApiServiceImpl(userSessionManager)),
            messagingRepository = MessagingRepositoryImpl(MessageApiServiceImpl(userSessionManager))
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val courses = uiState.todayCourses()

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppSubHeader("📅 Today’s Schedule")
            TextButton(
                onClick = onWeeklyViewClick,
                modifier = weeklyViewModifier
            ) {
                Text(
                    text = t(R.string.weekly_view),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        if (uiState.students.size > 1) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = { viewModel.selectAllStudents() },
                    label = { Text("All children") }
                )
                uiState.students.forEach { student ->
                    AssistChip(
                        onClick = { viewModel.toggleStudentSelection(student.id) },
                        label = { Text(student.name) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

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
                items(courses) { course ->
                    TodayScheduleCard(course)
                }
            }
        }
    }
}
