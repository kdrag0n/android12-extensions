package dev.kdrag0n.android12ext.monet.theme

import dev.kdrag0n.android12ext.monet.colors.CieLab
import dev.kdrag0n.android12ext.monet.colors.Color
import dev.kdrag0n.android12ext.monet.colors.Zcam
import dev.kdrag0n.android12ext.monet.colors.Zcam.Companion.toZcam

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
        ).map { it.key to cielabL(it.value) }.toMap()

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

        private fun cielabL(l: Double) = CieLab(
            L = l,
            a = 0.0,
            b = 0.0,
        ).toCieXyz().toZcam().lightness
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
