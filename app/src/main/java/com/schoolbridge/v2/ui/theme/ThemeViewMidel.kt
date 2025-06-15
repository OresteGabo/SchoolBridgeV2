package com.schoolbridge.v2.ui.theme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.schoolbridge.v2.data.preferences.ThemePreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val themePreferenceManager = ThemePreferenceManager(application.applicationContext)

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> get() = _isDarkTheme

    init {
        themePreferenceManager.isDarkModeFlow
            .onEach { enabled ->
                _isDarkTheme.value = enabled
            }
            .launchIn(viewModelScope)
    }

    fun toggleTheme(enabled: Boolean) {
        viewModelScope.launch {
            themePreferenceManager.setDarkMode(enabled)
        }
    }
}
