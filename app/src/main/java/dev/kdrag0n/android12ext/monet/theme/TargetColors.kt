package dev.kdrag0n.android12ext.monet.theme

import dev.kdrag0n.android12ext.monet.colors.Color
import dev.kdrag0n.android12ext.monet.colors.Jzczhz

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
            0    to 0.017580049949915525,
            10   to 0.01720883876215196,
            50   to 0.016194192816266096,
            100  to 0.01491710199863671,
            200  to 0.012419025912093814,
            300  to 0.010097298187197932,
            400  to 0.007885462735958415,
            500  to 0.005815914895234246,
            600  to 0.004198187917249932,
            700  to 0.002695487762239713,
            800  to 0.0015342903147554181,
            900  to 6.789793323185895E-4,
            1000 to -8.077935669463161E-26,
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

        // Accent chroma from Pixel defaults
        // We use the most chromatic color as the reference
        // A-1 chroma = avg(default Pixel Blue shades 100-900)
        // Excluding very bright variants (10, 50) to avoid light bias
        // A-1 > A-3 > A-2
        private const val ACCENT1_CHROMA = 0.010860848736413473
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
            it.key to Jzczhz(it.value, chromaAdj, 0.0)
        }.toMap()
    }
}
