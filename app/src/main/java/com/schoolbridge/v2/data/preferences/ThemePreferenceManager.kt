package com.schoolbridge.v2.data.preferences

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.schoolbridge.v2.ui.theme.AppPalette
import com.schoolbridge.v2.ui.theme.Contrast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/** Single-process DataStore instance tied to the application context */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

class ThemePreferenceManager(private val context: Context) {

    /* ─────────────────────── preference keys ──────────────────────────── */
    private object Keys {
        val DARK_MODE   = booleanPreferencesKey("dark_mode_enabled")
        val PALETTE     = stringPreferencesKey("palette_name")
        val CONTRAST    = stringPreferencesKey("contrast_level")
    }

    /* ─────────────────────────── flows ────────────────────────────────── */
    val isDarkModeFlow: Flow<Boolean> = context.dataStore.data
        .catch { e ->
            if (e is IOException) {
                Log.e("ThemePrefs", "DataStore read error", e)
                emit(emptyPreferences())
            } else throw e
        }
        .map { it[Keys.DARK_MODE] == true }            // default: light theme

    val paletteFlow: Flow<AppPalette> = context.dataStore.data
        .map { prefs ->
            prefs[Keys.PALETTE]
                ?.let { runCatching { AppPalette.valueOf(it) }.getOrNull() }
                ?: AppPalette.GOLDEN                      // default palette
        }

    val contrastFlow: Flow<Contrast> = context.dataStore.data
        .map { prefs ->
            prefs[Keys.CONTRAST]
                ?.let { runCatching { Contrast.valueOf(it) }.getOrNull() }
                ?: Contrast.NORMAL                       // default contrast
        }

    /* ───────────────────────── setters ────────────────────────────────── */
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DARK_MODE] = enabled }
    }

    suspend fun setPalette(palette: AppPalette) {
        context.dataStore.edit { it[Keys.PALETTE] = palette.name }
    }

    suspend fun setContrast(contrast: Contrast) {
        context.dataStore.edit { it[Keys.CONTRAST] = contrast.name }
    }
}
