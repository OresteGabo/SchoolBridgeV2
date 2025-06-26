package com.schoolbridge.v2.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.*

class QuickActionPreferenceManager(context: Context) {

    private val dataStore = context.appPreferences

    val actionsFlow: Flow<Set<String>> = dataStore.data
        .map { preferences ->
            preferences[ACTION_KEYS]?.split(",")?.toSet() ?: emptySet()
        }

    suspend fun save(actions: Set<String>) {
        dataStore.edit { preferences ->
            preferences[ACTION_KEYS] = actions.joinToString(",")
        }
    }

    companion object {
        private val ACTION_KEYS = stringPreferencesKey("teacher_quick_actions")
    }
}

