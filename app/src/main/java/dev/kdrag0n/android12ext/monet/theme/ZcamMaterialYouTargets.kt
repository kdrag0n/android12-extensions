package dev.kdrag0n.android12ext.monet.theme

import dev.kdrag0n.android12ext.monet.colors.Color
import dev.kdrag0n.android12ext.monet.colors.Zcam

/*
 * Default target colors, conforming to Material You standards.
 *
 * Derived from AOSP and Pixel defaults.
 */
class ZcamMaterialYouTargets(
    private val chromaFactor: Double = 1.0,
) : ColorScheme() {
    companion object {
        // Lightness from AOSP defaults
        private val LIGHTNESS_MAP = mapOf(
            0    to 100.00000296754273,
            10   to 98.60403974009428,
            50   to 94.72386350388908,
            100  to 89.69628870011267,
            200  to 79.3326296037671,
            300  to 68.938947819272,
            400  to 58.15091644790415,
            500  to 46.991689840263206,
            600  to 37.24709908558773,
            700  to 26.96785892507836,
            800  to 17.67571012446932,
            900  to 9.36696155986009,
            1000 to 0.0,
        )

        // Accent chroma from Pixel defaults
        // We use the most chromatic color as the reference
        // A-1 chroma = avg(default Pixel Blue shades 100-900)
        // Excluding very bright variants (10, 50) to avoid light bias
        // A-1 > A-3 > A-2
        private const val ACCENT1_CHROMA = 19.43373944
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
            it.key to Zcam(
                lightness = it.value,
                chroma = chromaAdj,
                hueAngle = 0.0,
                viewingConditions = Zcam.ViewingConditions.DEFAULT,
            )
        }.toMap()
    }
}
