package dev.kdrag0n.android12ext.monet.theme

import dev.kdrag0n.android12ext.monet.colors.*
import dev.kdrag0n.android12ext.monet.colors.Oklab.Companion.toOklab
import dev.kdrag0n.android12ext.monet.colors.Oklch.Companion.toOklch
import timber.log.Timber

class DynamicColorScheme(
    targetColors: ColorScheme,
    primaryColor: Color,
    chromaMultiplier: Double = 1.0,
    private val accurateShades: Boolean = true,
) : ColorScheme() {
    private val primaryNeutral = primaryColor.toLinearSrgb().toOklab().toOklch().let { lch ->
        lch.copy(C = lch.C * chromaMultiplier)
    }
    private val primaryAccent = primaryNeutral

    init {
        val primaryRgb8 = primaryColor.toLinearSrgb().toSrgb().quantize8()
        Timber.i("Primary color: ${String.format("%06x", primaryRgb8)} => $primaryNeutral")
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
        val primaryA3 = primaryAccent.copy(h = primaryAccent.h + ACCENT3_HUE_SHIFT_DEGREES)
        transformSwatch(targetColors.accent3, primaryA3)
    }

    private fun transformSwatch(
        swatch: ColorSwatch,
        primary: Lch,
    ): ColorSwatch {
        return swatch.map { (shade, color) ->
            val target = color as? Lch
                ?: color.toLinearSrgb().toOklab().toOklch()
            val newLch = transformColor(target, primary)
            val newSrgb = newLch.toLinearSrgb().toSrgb()

            val newRgb8 = newSrgb.quantize8()
            Timber.d("Transform: [$shade] $target => $newLch => ${String.format("%06x", newRgb8)}")
            shade to newSrgb
        }.toMap()
    }

    private fun transformColor(target: Lch, primary: Lch): Color {
        // Keep target lightness.
        val L = target.L
        // Allow colorless gray.
        val C = primary.C.coerceIn(0.0, target.C)
        // Use the primary color's hue, since it's the most prominent feature of the theme.
        val h = primary.h

        val srgb = Oklch(L, C, h).toLinearSrgb()
        val clipMethod = if (accurateShades) {
            // Prefer lightness
            OklabGamut.ClipMethod.PRESERVE_LIGHTNESS
        } else {
            // Prefer chroma
            OklabGamut.ClipMethod.PROJECT_TO_LCUSP
        }

        return OklabGamut.clip(srgb, clipMethod)
    }

    companion object {
        // Hue shift for the tertiary accent color (accent3), in degrees.
        // 60 degrees = shifting by a secondary color
        private const val ACCENT3_HUE_SHIFT_DEGREES = 60.0
    }
}
