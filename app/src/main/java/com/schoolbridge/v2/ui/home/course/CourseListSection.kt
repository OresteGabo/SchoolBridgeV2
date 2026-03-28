package com.schoolbridge.v2.ui.home.course

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.schoolbridge.v2.data.remote.CourseApiServiceImpl
import com.schoolbridge.v2.data.repository.implementations.CourseRepositoryImpl
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.ui.common.FriendlyNetworkErrorCard
import com.schoolbridge.v2.ui.components.AppSubHeader
import com.schoolbridge.v2.ui.components.SpacerM
import com.schoolbridge.v2.ui.components.SpacerS

@Composable
fun CourseListSection(
    userSessionManager: UserSessionManager,
    modifier: Modifier = Modifier
) {
    val viewModel: CourseListViewModel = viewModel(
        factory = CourseListViewModelFactory(
            courseRepository = CourseRepositoryImpl(
                courseApiService = CourseApiServiceImpl(userSessionManager)
            )
        )
    )
    val uiState by viewModel.uiState.collectAsState()
    val visibleCourses = uiState.visibleCourses()

    Column(modifier = modifier.fillMaxWidth()) {
        AppSubHeader("📚 Courses")
        SpacerS()

        if (uiState.errorMessage != null) {
            FriendlyNetworkErrorCard(
                rawMessage = uiState.errorMessage,
                onRetry = viewModel::refresh,
                modifier = Modifier.fillMaxWidth()
            )
            return@Column
        }

        if (uiState.isLoading) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(3) { CourseLoadingCard() }
            }
            return@Column
        }

        if (uiState.students.size > 1) {
            StudentCourseFilterRow(
                students = uiState.students,
                selectedStudentId = uiState.selectedStudentId,
                onStudentSelected = viewModel::selectStudent
            )
            SpacerM()
        }

        if (visibleCourses.isEmpty()) {
            FriendlyNetworkErrorCard(
                rawMessage = "No courses are available for this account yet.",
                onRetry = viewModel::refresh,
                modifier = Modifier.fillMaxWidth()
            )
            return@Column
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            itemsIndexed(visibleCourses, key = { _, course -> course.id }) { _, course ->
                CourseCard(course)
            }
        }
    }
}

@Composable
private fun StudentCourseFilterRow(
    students: List<CourseStudentUi>,
    selectedStudentId: String?,
    onStudentSelected: (String?) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        itemsIndexed(students, key = { _, student -> student.id }) { _, student ->
            androidx.compose.material3.FilterChip(
                selected = selectedStudentId == student.id,
                onClick = { onStudentSelected(student.id) },
                label = { androidx.compose.material3.Text(student.name) }
            )
        }
    }
}

@Composable
private fun CourseLoadingCard() {
    androidx.compose.material3.Card(
        modifier = Modifier
            .width(240.dp)
            .height(280.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {}
}
