// src/main/java/com/schoolbridge.v2/data/preferences/ThemePreferenceManager.kt
package com.schoolbridge.v2.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.catch // Import catch operator
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log // Import Log

class ThemePreferenceManager(private val context: Context) {

    private val Context.dataStore by preferencesDataStore("settings")

    companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode_enabled")
    }

    val isDarkModeFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs ->
            prefs[DARK_MODE_KEY] == true // default to light theme
        }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = enabled
        }
    }
}