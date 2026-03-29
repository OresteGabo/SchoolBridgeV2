package com.schoolbridge.v2.ui.academic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.schoolbridge.v2.data.remote.CourseApiServiceImpl
import com.schoolbridge.v2.data.repository.implementations.CourseRepositoryImpl
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.domain.academic.GradeSummary
import com.schoolbridge.v2.ui.common.FriendlyNetworkErrorCard
import com.schoolbridge.v2.ui.home.course.CourseCard
import com.schoolbridge.v2.ui.home.course.CourseListUiState
import com.schoolbridge.v2.ui.home.course.CourseListViewModel
import com.schoolbridge.v2.ui.home.course.CourseListViewModelFactory
import com.schoolbridge.v2.ui.home.grade.GradeCardCompact

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesListScreen(
    userSessionManager: UserSessionManager,
    onBack: () -> Unit
) {
    val viewModel: CourseListViewModel = viewModel(
        factory = remember(userSessionManager) {
            CourseListViewModelFactory(
                courseRepository = CourseRepositoryImpl(
                    courseApiService = CourseApiServiceImpl(userSessionManager)
                )
            )
        }
    )
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Column {
                        Text("Courses", fontWeight = FontWeight.SemiBold)
                        Text(
                            text = "See every subject in one place instead of only the home preview.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        CourseFeedContent(
            uiState = uiState,
            onBack = onBack,
            onRetry = viewModel::refresh,
            onStudentSelected = viewModel::selectStudent,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@Composable
private fun CourseFeedContent(
    uiState: CourseListUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onStudentSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val visibleCourses = uiState.visibleCourses()
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AcademicHeroCard(
                title = "Course overview",
                subtitle = "Subjects, teachers, rooms, and weekly rhythm stay together so learners and families can understand the school load quickly.",
                icon = Icons.Default.MenuBook
            )
        }

        if (uiState.students.size > 1) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    uiState.students.forEach { student ->
                        FilterChip(
                            selected = uiState.selectedStudentId == student.id,
                            onClick = { onStudentSelected(student.id) },
                            label = { Text(student.name) }
                        )
                    }
                }
            }
        }

        if (uiState.errorMessage != null) {
            item {
                FriendlyNetworkErrorCard(
                    rawMessage = uiState.errorMessage,
                    onRetry = onRetry,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            return@LazyColumn
        }

        if (uiState.isLoading) {
            item {
                Text(
                    text = "Loading courses from SchoolBridge...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            return@LazyColumn
        }

        if (visibleCourses.isEmpty()) {
            item {
                AcademicEmptyState(
                    title = "No courses available yet",
                    body = "As soon as this account is linked to live course data, the full subject list will show up here."
                )
            }
            return@LazyColumn
        }

        item {
            Text(
                text = "${visibleCourses.size} courses available",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                state = rememberLazyListState()
            ) {
                itemsIndexed(visibleCourses, key = { _, course -> course.id }) { _, course ->
                    CourseCard(course = course)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradesListScreen(onBack: () -> Unit) {
    val grades = remember {
        listOf(
            GradeSummary("Mathematics", 87, "Mr. Kamali"),
            GradeSummary("Biology", 61, "Ms. Uwase"),
            GradeSummary("English", 45, "Mrs. Mukeshimana"),
            GradeSummary("Chemistry", 74, "Mr. Nshimiyimana"),
            GradeSummary("History", 81, "Ms. Umutoni")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Column {
                        Text("Grades", fontWeight = FontWeight.SemiBold)
                        Text(
                            text = "Recent academic signals, grouped in one calm progress space.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AcademicHeroCard(
                    title = "Recent grade picture",
                    subtitle = "This space keeps the latest subject performance visible without forcing people to decode every class report first.",
                    icon = Icons.Default.PieChart
                )
            }
            item {
                GradeOverviewCard(grades = grades)
            }
            itemsIndexed(grades, key = { index, grade -> "${grade.subject}-$index" }) { index, grade ->
                GradeCardCompact(grade = grade, index = index)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicPlaceholderDetailScreen(
    title: String,
    body: String,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text(title, fontWeight = FontWeight.SemiBold) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            AcademicEmptyState(
                title = title,
                body = body
            )
        }
    }
}

@Composable
private fun AcademicHeroCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun GradeOverviewCard(grades: List<GradeSummary>) {
    val average = grades.map { it.score }.average()
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(
                    text = average.toInt().toString(),
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Current average",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Across ${grades.size} recent subjects",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AcademicEmptyState(
    title: String,
    body: String
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
