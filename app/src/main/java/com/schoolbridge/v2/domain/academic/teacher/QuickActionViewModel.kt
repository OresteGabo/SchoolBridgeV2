package com.schoolbridge.v2.domain.academic.teacher

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.schoolbridge.v2.data.preferences.QuickActionPreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn


class QuickActionViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val prefs = QuickActionPreferenceManager(application)

    private val _selected = MutableStateFlow<Set<String>>(emptySet())
    val selected: StateFlow<Set<String>> = _selected.asStateFlow()

    init {
        prefs.actionsFlow.onEach { _selected.value = it }.launchIn(viewModelScope)
    }

    fun toggle(id: String) {
        val new = if (id in _selected.value) _selected.value - id else _selected.value + id
        _selected.value = new
        viewModelScope.launch { prefs.save(new) }
    }
}
