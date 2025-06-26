package com.schoolbridge.v2.domain.academic.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolbridge.v2.data.repository.interfaces.TodayScheduleRepository
import com.schoolbridge.v2.data.repository.impl.TodayScheduleRepositoryImpl
import com.schoolbridge.v2.domain.academic.TodayCourse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TodayScheduleViewModel(
    private val repository: TodayScheduleRepository = TodayScheduleRepositoryImpl()
) : ViewModel() {

    private val _courses = MutableStateFlow<List<TodayCourse>>(emptyList())
    val courses: StateFlow<List<TodayCourse>> = _courses.asStateFlow()

    init {
        // Collect once and update hot StateFlow
        viewModelScope.launch {
            repository.getTodayCourses().collect { list ->
                _courses.value = list
            }
        }
    }
}
