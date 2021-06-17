package dev.kdrag0n.android12ext.monet.theme

import dev.kdrag0n.android12ext.monet.colors.Color
import dev.kdrag0n.android12ext.monet.colors.Srlch2

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
            0    to 100.00000,
            10   to 98.61786194717564,
            50   to 94.79598041142819,
            100  to 89.88343723721563,
            200  to 79.88099523671632,
            300  to 69.98193993576817,
            400  to 59.78854890533891,
            500  to 49.23885747836461,
            600  to 39.9039510598721,
            700  to 29.724610083590353,
            800  to 19.86549087952754,
            900  to 9.766921621553388,
            1000 to 0.00000,
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
        private const val ACCENT1_CHROMA = 46.82689336772568
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
            it.key to Srlch2(it.value, chromaAdj, 0.0)
        }.toMap()
    }
}
