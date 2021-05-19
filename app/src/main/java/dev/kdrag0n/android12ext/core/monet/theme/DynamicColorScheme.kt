package dev.kdrag0n.android12ext.core.monet.theme

import dev.kdrag0n.android12ext.core.monet.colors.Color
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

    // Main background color. Tinted with the primary color.
    override val neutral1 = transformQuantizedColors(targetColors.neutral1)
    // Secondary background color. Slightly tinted with the primary color.
    override val neutral2 = transformQuantizedColors(targetColors.neutral2)

    // Main accent color. Generally, this is close to the primary color.
    override val accent1 = transformQuantizedColors(targetColors.accent1)
    // Secondary accent color. Darker shades of accent1.
    override val accent2 = transformQuantizedColors(targetColors.accent2)
    // Tertiary accent color. Primary color shifted to the next secondary color via hue offset.
    override val accent3 = transformQuantizedColors(targetColors.accent3) { lch ->
        lch.copy(h = lch.h + ACCENT3_HUE_OFFSET_DEGREES)
    }

    private fun transformQuantizedColors(
        colors: List<Color>,
        colorFilter: (Oklch) -> Oklch = { it }
    ): List<Color> {
        return colors.withIndex().map { colorEntry ->
            val colorLch = colorEntry.value as? Oklch
                    ?: colorEntry.value.toLinearSrgb().toOklab().toOklch()
            val newLch = colorFilter(transformColor(colorLch))
            val newColor = newLch.toOklab().toLinearSrgb().toSrgb()

            val newRgb8 = newColor.quantize8()
            Timber.d("Transform: $colorLch => $newLch => ${String.format("%06x", newRgb8)}")
            return@map newColor
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

    companion object {
        // Hue offset for the tertiary accent color (accent3), in degrees.
        // 60 degrees = shifting by a secondary color
        private const val ACCENT3_HUE_OFFSET_DEGREES = 60.0
    }
}