package com.schoolbridge.v2.ui.theme

import com.schoolbridge.v2.ui.theme.colors.golden.GoldenPalette
import com.schoolbridge.v2.ui.theme.colors.indigo.IndigoPalette
import com.schoolbridge.v2.ui.theme.colors.material.MaterialPalette
import com.schoolbridge.v2.ui.theme.colors.slate.SlatePalette
import com.schoolbridge.v2.ui.theme.colors.teal.TealPalette

/** All colour families the app ships with. */
enum class AppPalette(val variants: PaletteVariants) {
    GOLDEN(GoldenPalette),
    BLUE  (IndigoPalette),
    GREEN (SlatePalette),
    RED   (TealPalette),
    MATERIAL(MaterialPalette);
}
