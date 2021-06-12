package dev.kdrag0n.android12ext.monet.theme

import dev.kdrag0n.android12ext.monet.colors.Color
import dev.kdrag0n.android12ext.monet.colors.Lch
import dev.kdrag0n.android12ext.monet.colors.Oklab.Companion.toOklab
import dev.kdrag0n.android12ext.monet.colors.Oklch
import dev.kdrag0n.android12ext.monet.colors.Oklch.Companion.toOklch
import timber.log.Timber

class DynamicColorScheme(
    targetColors: ColorScheme,
    primaryColor: Color,
    chromaMultiplier: Double = 1.0,
) : ColorScheme() {
    private val primaryNeutral = primaryColor.toLinearSrgb().toOklab().toOklch().let { lch ->
        lch.copy(C = lch.C * chromaMultiplier)
    }
    private val primaryAccent = primaryNeutral

    init {
        Timber.i("Primary color: ${String.format("%06x", primaryColor.toLinearSrgb().toSrgb().quantize8())} => $primaryNeutral")
    }

    // Main background color. Tinted with the primary color.
    override val neutral1 by lazy(mode = LazyThreadSafetyMode.NONE) {
        transformSwatch(targetColors.neutral1, primaryNeutral)
    }

    // Secondary background color. Slightly tinted with the primary color.
    override val neutral2 by lazy(mode = LazyThreadSafetyMode.NONE) {
        transformSwatch(targetColors.neutral2, primaryNeutral)
    }

    // Main accent color. Generally, this is close to the primary color.
    override val accent1 by lazy(mode = LazyThreadSafetyMode.NONE) {
        transformSwatch(targetColors.accent1, primaryAccent)
    }

    // Secondary accent color. Darker shades of accent1.
    override val accent2 by lazy(mode = LazyThreadSafetyMode.NONE) {
        transformSwatch(targetColors.accent2, primaryAccent)
    }

    // Tertiary accent color. Primary color shifted to the next secondary color via hue offset.
    override val accent3 by lazy(mode = LazyThreadSafetyMode.NONE) {
        transformSwatch(targetColors.accent3, primaryAccent) { lch ->
            lch.copy(h = lch.h + ACCENT3_HUE_SHIFT_DEGREES)
        }
    }

    private fun transformSwatch(
        swatch: Map<Int, Color>,
        primary: Lch,
        colorFilter: (Oklch) -> Oklch = { it },
    ): Map<Int, Color> {
        return swatch.map { (shade, color) ->
            val target = color as? Lch
                ?: color.toLinearSrgb().toOklab().toOklch()
            val newLch = colorFilter(transformColor(target, primary))
            val newSrgb = newLch.toOklab().toLinearSrgb().toSrgb()

            val newRgb8 = newSrgb.quantize8()
            Timber.d("Transform: [$shade] $target => $newLch => ${String.format("%06x", newRgb8)}")
            shade to newSrgb
        }.toMap()
    }

    private fun transformColor(target: Lch, primary: Lch): Oklch {
        return Oklch(
            // Keep target luminance. Themes should never need to change it.
            L = target.L,
            // Allow colorless gray.
            C = primary.C.coerceIn(0.0, target.C),
            // Use the primary color's hue, since it's the most prominent feature of the theme.
            h = primary.h,
        )
    }

    companion object {
        // Hue shift for the tertiary accent color (accent3), in degrees.
        // 60 degrees = shifting by a secondary color
        private const val ACCENT3_HUE_SHIFT_DEGREES = 60.0
    }
}
