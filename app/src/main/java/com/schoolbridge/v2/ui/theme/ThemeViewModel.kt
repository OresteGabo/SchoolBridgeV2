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

    private val prefs = ThemePreferenceManager(application)

    private val _palette  = MutableStateFlow(AppPalette.GOLDEN)
    private val _contrast = MutableStateFlow(Contrast.NORMAL)
    private val _dark     = MutableStateFlow(false)

    val palette  : StateFlow<AppPalette> = _palette
    val contrast : StateFlow<Contrast>   = _contrast
    val isDark   : StateFlow<Boolean>    = _dark

    init {
        prefs.isDarkModeFlow        .onEach { _dark.value     = it }.launchIn(viewModelScope)
        prefs.paletteFlow           .onEach { _palette.value  = it }.launchIn(viewModelScope)
        prefs.contrastFlow          .onEach { _contrast.value = it }.launchIn(viewModelScope)
    }

    /* setters – save to DataStore then update state */
    fun toggleDark(enable: Boolean) = viewModelScope.launch { prefs.setDarkMode(enable) }
    fun setPalette(p: AppPalette)   = viewModelScope.launch { prefs.setPalette(p) }
    fun setContrast(c: Contrast)    = viewModelScope.launch { prefs.setContrast(c) }
}

