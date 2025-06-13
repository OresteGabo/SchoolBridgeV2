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

val Context.appThemeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_prefs")

@Singleton
class ThemePreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    }

    val isDarkMode: Flow<Boolean> = context.appThemeDataStore.data
        .map { preferences ->
            val value = preferences[PreferencesKeys.IS_DARK_MODE] == true
            Log.d("ThemePrefManager", "isDarkMode collected, value: $value")
            value
        }
        .catch { e -> // Add catch to log any errors during collection
            Log.e("ThemePrefManager", "Error collecting isDarkMode: ${e.message}", e)
            emit(false) // Emit a default value to prevent the Flow from crashing
        }

    suspend fun setIsDarkMode(isDark: Boolean) {
        try {
            context.appThemeDataStore.edit { preferences ->
                preferences[PreferencesKeys.IS_DARK_MODE] = isDark
            }
            Log.d("ThemePrefManager", "setIsDarkMode: saved $isDark")
        } catch (e: Exception) {
            Log.e("ThemePrefManager", "Error saving isDarkMode: ${e.message}", e)
        }
    }
}