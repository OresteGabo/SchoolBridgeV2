package com.schoolbridge.v2.localization

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.schoolbridge.v2.data.preferences.AppPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*

/**
 * Get a localized string using a specific locale.
 */
fun getTranslatedString(
    context: Context,
    @StringRes resourceId: Int,
    vararg formatArgs: Any,
    localeCode: String = "en"
): String {
    val config = Configuration(context.resources.configuration)
    config.setLocale(Locale(localeCode))
    val localizedContext = context.createConfigurationContext(config)
    return if (formatArgs.isNotEmpty()) {
        localizedContext.resources.getString(resourceId, *formatArgs)
    } else {
        localizedContext.resources.getString(resourceId)
    }
}

/**
 * Top-level translation function using stored app language (blocking).
 */
fun t(context: Context, @StringRes resId: Int, vararg formatArgs: Any): String {
    val lang = runBlocking { AppPreferences.getLanguage(context).first() ?: "en" }
    return getTranslatedString(context, resId, *formatArgs, localeCode = lang)
}

/**
 * Composable version of [t] that automatically uses LocalContext.
 */
@Composable
fun t(@StringRes resId: Int, vararg formatArgs: Any): String {
    val context = LocalContext.current
    val lang by AppPreferences.getLanguage(context).collectAsState(initial = "en")
    return getTranslatedString(context, resId, *formatArgs, localeCode = lang)
}
