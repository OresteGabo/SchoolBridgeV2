package com.schoolbridge.v2.localization


import android.content.Context
import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

fun getTranslatedString(
    context: Context,
    @StringRes resourceId: Int,
    vararg formatArgs: Any,
    locale: String? = "en" //SessionManager.currentLocale
): String {
    val config = Configuration(context.resources.configuration)
    config.setLocale(Locale(locale ?: "en"))  // fallback to English
    val localizedContext = context.createConfigurationContext(config)
    return if (formatArgs.isNotEmpty()) {
        localizedContext.resources.getString(resourceId, *formatArgs)
    } else {
        localizedContext.resources.getString(resourceId)
    }
}

fun t(context: Context, @StringRes resId: Int, vararg formatArgs: Any): String {
    val locale = "en"//SessionManager.currentLocale ?: Locale.getDefault().language
    val config = Configuration(context.resources.configuration)
    config.setLocale(Locale(locale))
    val localizedContext = context.createConfigurationContext(config)
    return if (formatArgs.isNotEmpty()) {
        localizedContext.resources.getString(resId, *formatArgs)
    } else {
        localizedContext.resources.getString(resId)
    }
}


@Composable
fun t(@StringRes resId: Int, vararg formatArgs: Any): String {
    val context = LocalContext.current
    return t(context, resId, *formatArgs)
}