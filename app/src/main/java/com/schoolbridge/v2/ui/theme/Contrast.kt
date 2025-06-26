package com.schoolbridge.v2.ui.theme

import androidx.compose.material3.ColorScheme

/** User-visible choice in Settings */
enum class Contrast { NORMAL, MEDIUM, HIGH }

/** A pair of light + dark schemes */
data class LightDark(val light: ColorScheme, val dark: ColorScheme)

/** All three contrast variants for one palette (gold, blue, …). */
data class PaletteVariants(
    val normal : LightDark,
    val medium : LightDark,
    val high   : LightDark
) {
    fun scheme(isDark: Boolean, contrast: Contrast): ColorScheme = when (contrast) {
        Contrast.NORMAL -> if (isDark) normal .dark else normal .light
        Contrast.MEDIUM -> if (isDark) medium .dark else medium .light
        Contrast.HIGH   -> if (isDark) high   .dark else high   .light
    }
}
