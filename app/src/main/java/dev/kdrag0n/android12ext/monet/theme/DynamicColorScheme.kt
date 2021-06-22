package dev.kdrag0n.android12ext.monet.theme

import dev.kdrag0n.android12ext.monet.colors.*
import dev.kdrag0n.android12ext.monet.colors.Oklab.Companion.toOklab
import dev.kdrag0n.android12ext.monet.colors.Oklch.Companion.toOklch
import timber.log.Timber

class DynamicColorScheme(
    targets: ColorScheme,
    seedColor: Color,
    chromaFactor: Double = 1.0,
    private val accurateShades: Boolean = true,
) : ColorScheme() {
    private val seedNeutral = seedColor.toLinearSrgb().toOklab().toOklch().let { lch ->
        lch.copy(C = lch.C * chromaFactor)
    }
    private val seedAccent = seedNeutral

    init {
        val seedRgb8 = seedColor.toLinearSrgb().toSrgb().quantize8()
        Timber.i("Seed color: ${String.format("%06x", seedRgb8)} => $seedNeutral")
    }

    // Main background color. Tinted with the seed color.
    override val neutral1 by lazy(mode = LazyThreadSafetyMode.NONE) {
        transformSwatch(targets.neutral1, seedNeutral)
    }

    // Secondary background color. Slightly tinted with the seed color.
    override val neutral2 by lazy(mode = LazyThreadSafetyMode.NONE) {
        transformSwatch(targets.neutral2, seedNeutral)
    }

    // Main accent color. Generally, this is close to the seed color.
    override val accent1 by lazy(mode = LazyThreadSafetyMode.NONE) {
        transformSwatch(targets.accent1, seedAccent)
    }

    // Secondary accent color. Darker shades of accent1.
    override val accent2 by lazy(mode = LazyThreadSafetyMode.NONE) {
        transformSwatch(targets.accent2, seedAccent)
    }

    // Tertiary accent color. Seed color shifted to the next secondary color via hue offset.
    override val accent3 by lazy(mode = LazyThreadSafetyMode.NONE) {
        val seedA3 = seedAccent.copy(h = seedAccent.h + ACCENT3_HUE_SHIFT_DEGREES)
        transformSwatch(targets.accent3, seedA3)
    }

    private fun transformSwatch(
        swatch: ColorSwatch,
        seed: Lch,
    ): ColorSwatch {
        return swatch.map { (shade, color) ->
            val target = color as? Lch
                ?: color.toLinearSrgb().toOklab().toOklch()
            val newLch = transformColor(target, seed)
            val newSrgb = newLch.toLinearSrgb().toSrgb()

            val newRgb8 = newSrgb.quantize8()
            Timber.d("Transform: [$shade] $target => $newLch => ${String.format("%06x", newRgb8)}")
            shade to newSrgb
        }.toMap()
    }

    private fun transformColor(target: Lch, seed: Lch): Color {
        // Keep target lightness.
        val L = target.L
        // Allow colorless gray.
        val C = seed.C.coerceIn(0.0, target.C)
        // Use the seed color's hue, since it's the most prominent feature of the theme.
        val h = seed.h

        val oklab = Oklch(L, C, h).toOklab()
        val srgb = oklab.toLinearSrgb()
        val clipMethod = if (accurateShades) {
            // Prefer lightness
            OklabGamut.ClipMethod.PRESERVE_LIGHTNESS
        } else {
            // Prefer chroma
            OklabGamut.ClipMethod.ADAPTIVE_TOWARDS_LCUSP
        }

        return OklabGamut.clip(srgb, clipMethod, alpha = 5.0, oklab = oklab)
    }

    companion object {
        // Hue shift for the tertiary accent color (accent3), in degrees.
        // 60 degrees = shifting by a secondary color
        private const val ACCENT3_HUE_SHIFT_DEGREES = 60.0
    }
}
