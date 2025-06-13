package com.schoolbridge.v2.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolbridge.v2.data.preferences.ThemePreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// Example in a Settings ViewModel
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themePreferenceManager: ThemePreferenceManager
) : ViewModel() {

    val isDarkModeEnabled: StateFlow<Boolean> = themePreferenceManager.isDarkMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false // Or collect current value
    )

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            themePreferenceManager.setIsDarkMode(enabled)
        }
    }
}