package com.schoolbridge.v2.data.preferences


import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.appPreferences by preferencesDataStore(name = "app_settings")

object AppPreferences {
    private val LANGUAGE_KEY = stringPreferencesKey("language")

    suspend fun setLanguage(context: Context, languageCode: String) {
        Log.d("AppPreferences", "Setting language to $languageCode")
        context.appPreferences.edit { prefs ->
            prefs[LANGUAGE_KEY] = languageCode
        }
    }

    fun getLanguage(context: Context): Flow<String> {
        val returnedValue = context.appPreferences.data.map { prefs ->
            prefs[LANGUAGE_KEY] ?: "en"  // default to English
        }
        Log.d("AppPreferences", "Getting language $returnedValue")
        return returnedValue
    }
}