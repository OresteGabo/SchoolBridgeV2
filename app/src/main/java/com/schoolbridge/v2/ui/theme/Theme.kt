package com.schoolbridge.v2.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.schoolbridge.v2.ui.theme.colors.golden.backgroundDark
import com.schoolbridge.v2.ui.theme.colors.golden.backgroundDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.backgroundDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.backgroundLight
import com.schoolbridge.v2.ui.theme.colors.golden.backgroundLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.backgroundLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.errorContainerDark
import com.schoolbridge.v2.ui.theme.colors.golden.errorContainerDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.errorContainerDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.errorContainerLight
import com.schoolbridge.v2.ui.theme.colors.golden.errorContainerLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.errorContainerLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.errorDark
import com.schoolbridge.v2.ui.theme.colors.golden.errorDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.errorDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.errorLight
import com.schoolbridge.v2.ui.theme.colors.golden.errorLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.errorLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.inverseOnSurfaceDark
import com.schoolbridge.v2.ui.theme.colors.golden.inverseOnSurfaceDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.inverseOnSurfaceDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.inverseOnSurfaceLight
import com.schoolbridge.v2.ui.theme.colors.golden.inverseOnSurfaceLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.inverseOnSurfaceLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.inversePrimaryDark
import com.schoolbridge.v2.ui.theme.colors.golden.inversePrimaryDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.inversePrimaryDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.inversePrimaryLight
import com.schoolbridge.v2.ui.theme.colors.golden.inversePrimaryLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.inversePrimaryLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.inverseSurfaceDark
import com.schoolbridge.v2.ui.theme.colors.golden.inverseSurfaceDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.inverseSurfaceDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.inverseSurfaceLight
import com.schoolbridge.v2.ui.theme.colors.golden.inverseSurfaceLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.inverseSurfaceLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onBackgroundDark
import com.schoolbridge.v2.ui.theme.colors.golden.onBackgroundDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onBackgroundDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onBackgroundLight
import com.schoolbridge.v2.ui.theme.colors.golden.onBackgroundLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onBackgroundLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onErrorContainerDark
import com.schoolbridge.v2.ui.theme.colors.golden.onErrorContainerDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onErrorContainerDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onErrorContainerLight
import com.schoolbridge.v2.ui.theme.colors.golden.onErrorContainerLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onErrorContainerLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onErrorDark
import com.schoolbridge.v2.ui.theme.colors.golden.onErrorDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onErrorDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onErrorLight
import com.schoolbridge.v2.ui.theme.colors.golden.onErrorLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onErrorLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onPrimaryContainerDark
import com.schoolbridge.v2.ui.theme.colors.golden.onPrimaryContainerDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onPrimaryContainerDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onPrimaryContainerLight
import com.schoolbridge.v2.ui.theme.colors.golden.onPrimaryContainerLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onPrimaryContainerLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onPrimaryDark
import com.schoolbridge.v2.ui.theme.colors.golden.onPrimaryDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onPrimaryDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onPrimaryLight
import com.schoolbridge.v2.ui.theme.colors.golden.onPrimaryLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onPrimaryLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onSecondaryContainerDark
import com.schoolbridge.v2.ui.theme.colors.golden.onSecondaryContainerDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onSecondaryContainerDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onSecondaryContainerLight
import com.schoolbridge.v2.ui.theme.colors.golden.onSecondaryContainerLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onSecondaryContainerLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onSecondaryDark
import com.schoolbridge.v2.ui.theme.colors.golden.onSecondaryDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onSecondaryDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onSecondaryLight
import com.schoolbridge.v2.ui.theme.colors.golden.onSecondaryLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onSecondaryLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onSurfaceDark
import com.schoolbridge.v2.ui.theme.colors.golden.onSurfaceDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onSurfaceDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onSurfaceLight
import com.schoolbridge.v2.ui.theme.colors.golden.onSurfaceLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onSurfaceLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onSurfaceVariantDark
import com.schoolbridge.v2.ui.theme.colors.golden.onSurfaceVariantDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onSurfaceVariantDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onSurfaceVariantLight
import com.schoolbridge.v2.ui.theme.colors.golden.onSurfaceVariantLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onSurfaceVariantLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onTertiaryContainerDark
import com.schoolbridge.v2.ui.theme.colors.golden.onTertiaryContainerDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onTertiaryContainerDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onTertiaryContainerLight
import com.schoolbridge.v2.ui.theme.colors.golden.onTertiaryContainerLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onTertiaryContainerLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onTertiaryDark
import com.schoolbridge.v2.ui.theme.colors.golden.onTertiaryDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onTertiaryDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onTertiaryLight
import com.schoolbridge.v2.ui.theme.colors.golden.onTertiaryLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.onTertiaryLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.outlineDark
import com.schoolbridge.v2.ui.theme.colors.golden.outlineDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.outlineDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.outlineLight
import com.schoolbridge.v2.ui.theme.colors.golden.outlineLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.outlineLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.outlineVariantDark
import com.schoolbridge.v2.ui.theme.colors.golden.outlineVariantDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.outlineVariantDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.outlineVariantLight
import com.schoolbridge.v2.ui.theme.colors.golden.outlineVariantLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.outlineVariantLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.primaryContainerDark
import com.schoolbridge.v2.ui.theme.colors.golden.primaryContainerDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.primaryContainerDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.primaryContainerLight
import com.schoolbridge.v2.ui.theme.colors.golden.primaryContainerLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.primaryContainerLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.primaryDark
import com.schoolbridge.v2.ui.theme.colors.golden.primaryDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.primaryDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.primaryLight
import com.schoolbridge.v2.ui.theme.colors.golden.primaryLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.primaryLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.scrimDark
import com.schoolbridge.v2.ui.theme.colors.golden.scrimDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.scrimDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.scrimLight
import com.schoolbridge.v2.ui.theme.colors.golden.scrimLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.scrimLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.secondaryContainerDark
import com.schoolbridge.v2.ui.theme.colors.golden.secondaryContainerDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.secondaryContainerDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.secondaryContainerLight
import com.schoolbridge.v2.ui.theme.colors.golden.secondaryContainerLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.secondaryContainerLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.secondaryDark
import com.schoolbridge.v2.ui.theme.colors.golden.secondaryDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.secondaryDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.secondaryLight
import com.schoolbridge.v2.ui.theme.colors.golden.secondaryLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.secondaryLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceBrightDark
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceBrightDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceBrightDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceBrightLight
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceBrightLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceBrightLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceContainerDark
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceContainerDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceContainerDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceContainerHighDark
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceContainerHighDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceContainerHighDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceContainerHighLight
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceContainerHighLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceContainerHighLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceContainerHighestDark
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceContainerHighestDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceContainerHighestDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceContainerHighestLight
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceContainerHighestLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceContainerHighestLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceContainerLight
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceContainerLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceContainerLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceContainerLowDark
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceContainerLowDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceContainerLowDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceContainerLowLight
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceContainerLowLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceContainerLowLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceContainerLowestDark
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceContainerLowestDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceContainerLowestDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceContainerLowestLight
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceContainerLowestLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceContainerLowestLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceDark
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceDimDark
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceDimDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceDimDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceDimLight
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceDimLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceDimLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceLight
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceVariantDark
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceVariantDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceVariantDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceVariantLight
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceVariantLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.surfaceVariantLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.tertiaryContainerDark
import com.schoolbridge.v2.ui.theme.colors.golden.tertiaryContainerDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.tertiaryContainerDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.tertiaryContainerLight
import com.schoolbridge.v2.ui.theme.colors.golden.tertiaryContainerLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.tertiaryContainerLightMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.tertiaryDark
import com.schoolbridge.v2.ui.theme.colors.golden.tertiaryDarkHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.tertiaryDarkMediumContrast
import com.schoolbridge.v2.ui.theme.colors.golden.tertiaryLight
import com.schoolbridge.v2.ui.theme.colors.golden.tertiaryLightHighContrast
import com.schoolbridge.v2.ui.theme.colors.golden.tertiaryLightMediumContrast

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

private val mediumContrastLightColorScheme = lightColorScheme(
    primary = primaryLightMediumContrast,
    onPrimary = onPrimaryLightMediumContrast,
    primaryContainer = primaryContainerLightMediumContrast,
    onPrimaryContainer = onPrimaryContainerLightMediumContrast,
    secondary = secondaryLightMediumContrast,
    onSecondary = onSecondaryLightMediumContrast,
    secondaryContainer = secondaryContainerLightMediumContrast,
    onSecondaryContainer = onSecondaryContainerLightMediumContrast,
    tertiary = tertiaryLightMediumContrast,
    onTertiary = onTertiaryLightMediumContrast,
    tertiaryContainer = tertiaryContainerLightMediumContrast,
    onTertiaryContainer = onTertiaryContainerLightMediumContrast,
    error = errorLightMediumContrast,
    onError = onErrorLightMediumContrast,
    errorContainer = errorContainerLightMediumContrast,
    onErrorContainer = onErrorContainerLightMediumContrast,
    background = backgroundLightMediumContrast,
    onBackground = onBackgroundLightMediumContrast,
    surface = surfaceLightMediumContrast,
    onSurface = onSurfaceLightMediumContrast,
    surfaceVariant = surfaceVariantLightMediumContrast,
    onSurfaceVariant = onSurfaceVariantLightMediumContrast,
    outline = outlineLightMediumContrast,
    outlineVariant = outlineVariantLightMediumContrast,
    scrim = scrimLightMediumContrast,
    inverseSurface = inverseSurfaceLightMediumContrast,
    inverseOnSurface = inverseOnSurfaceLightMediumContrast,
    inversePrimary = inversePrimaryLightMediumContrast,
    surfaceDim = surfaceDimLightMediumContrast,
    surfaceBright = surfaceBrightLightMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestLightMediumContrast,
    surfaceContainerLow = surfaceContainerLowLightMediumContrast,
    surfaceContainer = surfaceContainerLightMediumContrast,
    surfaceContainerHigh = surfaceContainerHighLightMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestLightMediumContrast,
)

private val highContrastLightColorScheme = lightColorScheme(
    primary = primaryLightHighContrast,
    onPrimary = onPrimaryLightHighContrast,
    primaryContainer = primaryContainerLightHighContrast,
    onPrimaryContainer = onPrimaryContainerLightHighContrast,
    secondary = secondaryLightHighContrast,
    onSecondary = onSecondaryLightHighContrast,
    secondaryContainer = secondaryContainerLightHighContrast,
    onSecondaryContainer = onSecondaryContainerLightHighContrast,
    tertiary = tertiaryLightHighContrast,
    onTertiary = onTertiaryLightHighContrast,
    tertiaryContainer = tertiaryContainerLightHighContrast,
    onTertiaryContainer = onTertiaryContainerLightHighContrast,
    error = errorLightHighContrast,
    onError = onErrorLightHighContrast,
    errorContainer = errorContainerLightHighContrast,
    onErrorContainer = onErrorContainerLightHighContrast,
    background = backgroundLightHighContrast,
    onBackground = onBackgroundLightHighContrast,
    surface = surfaceLightHighContrast,
    onSurface = onSurfaceLightHighContrast,
    surfaceVariant = surfaceVariantLightHighContrast,
    onSurfaceVariant = onSurfaceVariantLightHighContrast,
    outline = outlineLightHighContrast,
    outlineVariant = outlineVariantLightHighContrast,
    scrim = scrimLightHighContrast,
    inverseSurface = inverseSurfaceLightHighContrast,
    inverseOnSurface = inverseOnSurfaceLightHighContrast,
    inversePrimary = inversePrimaryLightHighContrast,
    surfaceDim = surfaceDimLightHighContrast,
    surfaceBright = surfaceBrightLightHighContrast,
    surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
    surfaceContainerLow = surfaceContainerLowLightHighContrast,
    surfaceContainer = surfaceContainerLightHighContrast,
    surfaceContainerHigh = surfaceContainerHighLightHighContrast,
    surfaceContainerHighest = surfaceContainerHighestLightHighContrast,
)

private val mediumContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkMediumContrast,
    onPrimary = onPrimaryDarkMediumContrast,
    primaryContainer = primaryContainerDarkMediumContrast,
    onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
    secondary = secondaryDarkMediumContrast,
    onSecondary = onSecondaryDarkMediumContrast,
    secondaryContainer = secondaryContainerDarkMediumContrast,
    onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
    tertiary = tertiaryDarkMediumContrast,
    onTertiary = onTertiaryDarkMediumContrast,
    tertiaryContainer = tertiaryContainerDarkMediumContrast,
    onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
    error = errorDarkMediumContrast,
    onError = onErrorDarkMediumContrast,
    errorContainer = errorContainerDarkMediumContrast,
    onErrorContainer = onErrorContainerDarkMediumContrast,
    background = backgroundDarkMediumContrast,
    onBackground = onBackgroundDarkMediumContrast,
    surface = surfaceDarkMediumContrast,
    onSurface = onSurfaceDarkMediumContrast,
    surfaceVariant = surfaceVariantDarkMediumContrast,
    onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
    outline = outlineDarkMediumContrast,
    outlineVariant = outlineVariantDarkMediumContrast,
    scrim = scrimDarkMediumContrast,
    inverseSurface = inverseSurfaceDarkMediumContrast,
    inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
    inversePrimary = inversePrimaryDarkMediumContrast,
    surfaceDim = surfaceDimDarkMediumContrast,
    surfaceBright = surfaceBrightDarkMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkMediumContrast,
    surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
    surfaceContainer = surfaceContainerDarkMediumContrast,
    surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkMediumContrast,
)

private val highContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkHighContrast,
    onPrimary = onPrimaryDarkHighContrast,
    primaryContainer = primaryContainerDarkHighContrast,
    onPrimaryContainer = onPrimaryContainerDarkHighContrast,
    secondary = secondaryDarkHighContrast,
    onSecondary = onSecondaryDarkHighContrast,
    secondaryContainer = secondaryContainerDarkHighContrast,
    onSecondaryContainer = onSecondaryContainerDarkHighContrast,
    tertiary = tertiaryDarkHighContrast,
    onTertiary = onTertiaryDarkHighContrast,
    tertiaryContainer = tertiaryContainerDarkHighContrast,
    onTertiaryContainer = onTertiaryContainerDarkHighContrast,
    error = errorDarkHighContrast,
    onError = onErrorDarkHighContrast,
    errorContainer = errorContainerDarkHighContrast,
    onErrorContainer = onErrorContainerDarkHighContrast,
    background = backgroundDarkHighContrast,
    onBackground = onBackgroundDarkHighContrast,
    surface = surfaceDarkHighContrast,
    onSurface = onSurfaceDarkHighContrast,
    surfaceVariant = surfaceVariantDarkHighContrast,
    onSurfaceVariant = onSurfaceVariantDarkHighContrast,
    outline = outlineDarkHighContrast,
    outlineVariant = outlineVariantDarkHighContrast,
    scrim = scrimDarkHighContrast,
    inverseSurface = inverseSurfaceDarkHighContrast,
    inverseOnSurface = inverseOnSurfaceDarkHighContrast,
    inversePrimary = inversePrimaryDarkHighContrast,
    surfaceDim = surfaceDimDarkHighContrast,
    surfaceBright = surfaceBrightDarkHighContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkHighContrast,
    surfaceContainerLow = surfaceContainerLowDarkHighContrast,
    surfaceContainer = surfaceContainerDarkHighContrast,
    surfaceContainerHigh = surfaceContainerHighDarkHighContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkHighContrast,
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

val unspecified_scheme = ColorFamily(
    Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified
)
/*
@Composable
fun SchoolBridgeV2Theme(
    isDarkTheme: Boolean, // This is now passed from MainActivity
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDarkTheme -> darkScheme
        else -> lightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}*/


@Composable
fun SchoolBridgeV2Theme(
    isDarkTheme : Boolean,                // system or user toggle
    palette     : AppPalette = AppPalette.GOLDEN,
    contrast    : Contrast   = Contrast.NORMAL,
    dynamicColor: Boolean    = false,    // Material You override
    content     : @Composable () -> Unit
) {
    val colors = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (isDarkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        else -> palette.variants.scheme(isDarkTheme, contrast)
    }

    MaterialTheme(
        colorScheme = colors,
        typography  = Typography,
        content     = content
    )
}
