package com.schoolbridge.v2.ui.home.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.schoolbridge.v2.data.dto.academic.MobileCourseDto
import com.schoolbridge.v2.data.dto.academic.MobileCourseFeedDto
import com.schoolbridge.v2.data.repository.interfaces.CourseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CourseStudentUi(
    val id: String,
    val name: String
)

data class CourseCardUi(
    val id: String,
    val title: String,
    val description: String?,
    val teacherName: String?,
    val room: String?,
    val scheduleLabel: String?,
    val studentId: String?,
    val studentName: String?
)

data class CourseListUiState(
    val isLoading: Boolean = false,
    val audience: String = "GENERAL",
    val scopeLabel: String? = null,
    val students: List<CourseStudentUi> = emptyList(),
    val selectedStudentId: String? = null,
    val courses: List<CourseCardUi> = emptyList(),
    val errorMessage: String? = null
) {
    fun visibleCourses(): List<CourseCardUi> =
        selectedStudentId?.let { studentId ->
            courses.filter { it.studentId == null || it.studentId == studentId }
        } ?: courses
}

class CourseListViewModel(
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CourseListUiState(isLoading = true))
    val uiState: StateFlow<CourseListUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            runCatching { courseRepository.getCourses() }
                .onSuccess { response ->
                    _uiState.value = response.toUiState()
                }
                .onFailure { throwable ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Could not load courses."
                    )
                }
        }
    }

    fun selectStudent(studentId: String?) {
        _uiState.value = _uiState.value.copy(selectedStudentId = studentId)
    }
}

class CourseListViewModelFactory(
    private val courseRepository: CourseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CourseListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CourseListViewModel(courseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

private fun MobileCourseFeedDto.toUiState(): CourseListUiState = CourseListUiState(
    isLoading = false,
    audience = audience,
    scopeLabel = scopeLabel,
    students = students.map { CourseStudentUi(id = it.id, name = it.name) },
    selectedStudentId = selectedStudentId ?: students.firstOrNull()?.id,
    courses = courses.map { it.toUi() },
    errorMessage = null
)

private fun MobileCourseDto.toUi(): CourseCardUi = CourseCardUi(
    id = id,
    title = title,
    description = description,
    teacherName = teacherName,
    room = room,
    scheduleLabel = buildString {
        dayOfWeek?.takeIf { it.isNotBlank() }?.let { append(it.lowercase().replaceFirstChar(Char::titlecase)) }
        if (!startTime.isNullOrBlank() && !endTime.isNullOrBlank()) {
            if (isNotBlank()) append(" • ")
            append(startTime.take(5))
            append(" - ")
            append(endTime.take(5))
        }
    }.ifBlank { null },
    studentId = studentId,
    studentName = studentName
)
