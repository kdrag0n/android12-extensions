package dev.kdrag0n.android12ext.core.monet.theme

import dev.kdrag0n.android12ext.core.monet.colors.LinearSrgb.Companion.toLinearSrgb
import dev.kdrag0n.android12ext.core.monet.colors.Oklab.Companion.toOklab
import dev.kdrag0n.android12ext.core.monet.colors.Oklch
import dev.kdrag0n.android12ext.core.monet.colors.Oklch.Companion.toOklch
import dev.kdrag0n.android12ext.core.monet.colors.Srgb
import timber.log.Timber

class DynamicColorScheme(
    targetColors: ColorScheme,
    primaryColor: Int,
) : ColorScheme() {
    private val primaryLch = Srgb(primaryColor).toLinearSrgb().toOklab().toOklch()

    override val neutral1 = transformQuantizedColors(targetColors.neutral1)
    override val neutral2 = transformQuantizedColors(targetColors.neutral2)

    override val accent1 = transformQuantizedColors(targetColors.accent1)
    override val accent2 = transformQuantizedColors(targetColors.accent2)
    override val accent3 = transformQuantizedColors(targetColors.accent3)

    private fun transformQuantizedColors(colors: List<Int>): List<Int> {
        return colors.withIndex().map { colorEntry ->
            val colorLch = Srgb(colorEntry.value).toLinearSrgb().toOklab().toOklch()
            val newLch = transformColor(colorLch)
            val newRgb8 = newLch.toOklab().toLinearSrgb().toSrgb().quantize8()

            Timber.d("Transform: $colorLch => $newLch => ${String.format("%06x", newRgb8)}")
            return@map newRgb8
        }
    }

    private fun transformColor(color: Oklch): Oklch {
        return Oklch(
            // Keep target luminance. Themes should never need to change it.
            L = color.L,
            // Allow colorless gray and 10% over-saturation for naturally saturated colors.
            C = primaryLch.C.coerceIn(0.0, color.C * 1.1),
            // Use the primary color's hue, since it's the most prominent feature of the theme.
            h = primaryLch.h,
        )
    }
}