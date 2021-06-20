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
            0    to 1.00000,
            10   to 0.98809,
            50   to 0.95514,
            100  to 0.91279,
            200  to 0.82656,
            300  to 0.74123,
            400  to 0.65335,
            500  to 0.56241,
            600  to 0.48193,
            700  to 0.39418,
            800  to 0.30919,
            900  to 0.22213,
            1000 to 0.00000,
        )

        // Accent chroma from Pixel defaults
        // We use the most chromatic color as the reference
        // A-1 chroma = avg(default Pixel Blue shades 100-900)
        // Excluding very bright variants (10, 50) to avoid light bias
        // A-1 > A-3 > A-2
        private const val ACCENT1_CHROMA = 0.13281
        private const val ACCENT2_CHROMA = ACCENT1_CHROMA / 3
        private const val ACCENT3_CHROMA = ACCENT2_CHROMA * 2

        // Neutral chroma derived from Google's CAM16 implementation
        // N-2 > N-1
        private const val NEUTRAL1_CHROMA = ACCENT1_CHROMA / 12
        private const val NEUTRAL2_CHROMA = NEUTRAL1_CHROMA * 2
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
