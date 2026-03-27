package com.schoolbridge.v2.ui.home.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.schoolbridge.v2.data.dto.academic.MobileTimetableEntryDto
import com.schoolbridge.v2.data.dto.academic.MobileTimetableResponseDto
import com.schoolbridge.v2.data.repository.interfaces.TimetableRepository
import com.schoolbridge.v2.domain.academic.AttendanceStatus
import com.schoolbridge.v2.domain.academic.TodayCourse
import com.schoolbridge.v2.domain.academic.TimetableEntry
import com.schoolbridge.v2.domain.academic.TimetableEntryType
import com.schoolbridge.v2.domain.academic.TimetableTemplateEntry
import com.schoolbridge.v2.domain.academic.toTimetableEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

data class TimetableStudent(
    val id: String,
    val name: String
)

data class TimetableUiState(
    val isLoading: Boolean = false,
    val audience: String = "GENERAL",
    val scopeLabel: String? = null,
    val students: List<TimetableStudent> = emptyList(),
    val selectedStudentId: String? = null,
    val templates: List<TimetableTemplateEntry> = emptyList(),
    val errorMessage: String? = null
) {
    fun weeklyEntries(startOfWeek: LocalDate): List<TimetableEntry> =
        filteredTemplates().map { it.toTimetableEntry(startOfWeek) }.sortedBy { it.start }

    fun dailyEntries(date: LocalDate): List<TimetableEntry> =
        filteredTemplates()
            .filter { it.dayOfWeek == date.dayOfWeek }
            .map { template ->
                TimetableEntry(
                    id = template.id.hashCode(),
                    start = date.atTime(template.startTime),
                    end = date.atTime(template.endTime),
                    title = template.title,
                    room = template.room,
                    teacher = template.teacher,
                    type = template.type,
                    studentId = template.studentId,
                    studentName = template.studentName,
                    note = template.note
                )
            }
            .sortedBy { it.start }

    fun todayCourses(date: LocalDate = LocalDate.now()): List<TodayCourse> =
        dailyEntries(date).map { entry ->
            TodayCourse(
                subject = entry.title,
                startTime = entry.start.toLocalTime().toString().take(5),
                endTime = entry.end.toLocalTime().toString().take(5),
                teacher = entry.teacher.ifBlank { "School team" },
                location = entry.room,
                attendanceStatus = AttendanceStatus.UNMARKED,
                studentId = entry.studentId,
                studentName = entry.studentName
            )
        }

    fun nextDateWithEvent(fromDate: LocalDate): LocalDate? =
        (0..30)
            .map { fromDate.plusDays(it.toLong()) }
            .firstOrNull { date -> filteredTemplates().any { it.dayOfWeek == date.dayOfWeek } }

    private fun filteredTemplates(): List<TimetableTemplateEntry> =
        selectedStudentId?.let { studentId ->
            templates.filter { it.studentId == null || it.studentId == studentId }
        } ?: templates
}

class TimetableViewModel(
    private val timetableRepository: TimetableRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimetableUiState(isLoading = true))
    val uiState: StateFlow<TimetableUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            runCatching { timetableRepository.getTimetable() }
                .onSuccess { response ->
                    _uiState.value = response.toUiState()
                }
                .onFailure { throwable ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Could not load timetable"
                    )
                }
        }
    }

    fun selectStudent(studentId: String?) {
        _uiState.value = _uiState.value.copy(selectedStudentId = studentId)
    }
}

class TimetableViewModelFactory(
    private val timetableRepository: TimetableRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimetableViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimetableViewModel(timetableRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

private fun MobileTimetableResponseDto.toUiState(): TimetableUiState = TimetableUiState(
    isLoading = false,
    audience = audience,
    scopeLabel = scopeLabel,
    students = students.map { TimetableStudent(id = it.id, name = it.name) },
    selectedStudentId = selectedStudentId ?: students.firstOrNull()?.id,
    templates = entries.map { it.toTemplateEntry() },
    errorMessage = null
)

private fun MobileTimetableEntryDto.toTemplateEntry(): TimetableTemplateEntry = TimetableTemplateEntry(
    id = id,
    dayOfWeek = runCatching { DayOfWeek.valueOf(dayOfWeek) }.getOrDefault(DayOfWeek.MONDAY),
    startTime = LocalTime.parse(startTime),
    endTime = LocalTime.parse(endTime),
    title = title,
    room = room.orEmpty(),
    teacher = teacher.orEmpty(),
    type = runCatching { TimetableEntryType.valueOf(type) }.getOrDefault(TimetableEntryType.LECTURE),
    studentId = studentId,
    studentName = studentName,
    note = note
)
