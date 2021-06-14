package dev.kdrag0n.android12ext.monet.theme

import dev.kdrag0n.android12ext.monet.colors.Color
import dev.kdrag0n.android12ext.monet.colors.Oklch

/*
 * Default target colors, conforming to Material You standards.
 *
 * Mostly derived from:
 *   - AOSP defaults: Untinted gray neutral colors and teal accent (+60 deg = ~purple).
 *   - Pixel defaults: Neutral colors are equivalent to AOSP. Main accent is blue.
 */
class TargetColors(
    private val chromaFactor: Double = 1.0,
) : ColorScheme() {
    companion object {
        // Lightness from AOSP defaults
        private val LIGHTNESS_MAP = mapOf(
            0    to 1.000,
            10   to 0.988,
            50   to 0.955,
            100  to 0.913,
            200  to 0.827,
            300  to 0.741,
            400  to 0.653,
            500  to 0.562,
            600  to 0.482,
            700  to 0.394,
            800  to 0.309,
            900  to 0.222,
            1000 to 0.000,
        )

        // Lightness map in CIELAB L*
        val LSTAR_LIGHTNESS_MAP = mapOf(
            0    to 100.0,
            10   to  99.0,
            50   to  95.0,
            100  to  90.0,
            200  to  80.0,
            300  to  70.0,
            400  to  60.0,
            500  to  49.6,
            600  to  40.0,
            700  to  30.0,
            800  to  20.0,
            900  to  10.0,
            1000 to   0.0,
        )

        // Neutral chroma from Google's CAM16 implementation
        private const val NEUTRAL1_CHROMA = 0.0132
        private const val NEUTRAL2_CHROMA = NEUTRAL1_CHROMA / 2

        // Accent chroma from Pixel defaults
        private const val ACCENT1_CHROMA = 0.1212
        private const val ACCENT2_CHROMA = 0.04
        private const val ACCENT3_CHROMA = 0.06
    }

    override val neutral1 = shadesWithChroma(NEUTRAL1_CHROMA)
    override val neutral2 = shadesWithChroma(NEUTRAL2_CHROMA)

    override val accent1 = shadesWithChroma(ACCENT1_CHROMA)
    override val accent2 = shadesWithChroma(ACCENT2_CHROMA)
    override val accent3 = shadesWithChroma(ACCENT3_CHROMA)

    private fun shadesWithChroma(chroma: Double): Map<Int, Color> {
        // Adjusted chroma
        val chromaAdj = chroma * chromaFactor

        return LIGHTNESS_MAP.map {
            it.key to Oklch(it.value, chromaAdj)
        }.toMap()
    }
}
