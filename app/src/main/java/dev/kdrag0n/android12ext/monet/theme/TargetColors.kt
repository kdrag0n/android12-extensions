package dev.kdrag0n.android12ext.monet.theme

import dev.kdrag0n.android12ext.monet.colors.Color
import dev.kdrag0n.android12ext.monet.colors.Cam16

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
            0    to 100.0,
            10   to 98.077286,
            50   to 92.8308,
            100  to 86.24347,
            200  to 73.39204,
            300  to 61.44706,
            400  to 49.99679,
            500  to 39.11102,
            600  to 30.351782,
            700  to 21.80448,
            800  to 14.609172,
            900  to 8.459497,
            1000 to 0.0,
        )

        // Accent chroma from Pixel defaults
        // We use the most chromatic color as the reference
        // A-1 chroma = avg(default Pixel Blue shades 100-900)
        // Excluding very bright variants (10, 50) to avoid light bias
        // A-1 > A-3 > A-2
        private const val ACCENT1_CHROMA = 47.35212111111111
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
            it.key to Cam16(it.value, chromaAdj, 0.0)
        }.toMap()
    }
}
