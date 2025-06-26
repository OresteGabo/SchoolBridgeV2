package com.schoolbridge.v2.ui.theme

import com.schoolbridge.v2.ui.theme.colors.golden.GoldenPalette
import com.schoolbridge.v2.ui.theme.colors.blue.BluePalette
import com.schoolbridge.v2.ui.theme.colors.green.GreenPalette
import com.schoolbridge.v2.ui.theme.colors.red.RedPalette

/** All colour families the app ships with. */
enum class AppPalette(val variants: PaletteVariants) {
    GOLDEN(GoldenPalette),
    BLUE  (BluePalette),
    GREEN (GreenPalette),
    RED   (RedPalette);
}
