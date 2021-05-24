package dev.kdrag0n.android12ext.monet.theme

import dev.kdrag0n.android12ext.monet.colors.Oklch

// Custom target color tables
object TargetColors {
    /*
     * Default target colors, conforming to Material You standards.
     *
     * Mostly derived from:
     *   - AOSP defaults: Untinted gray neutral colors and teal accent (+60 deg = ~purple).
     *   - Pixel defaults: Neutral colors are equivalent to AOSP. Main accent is blue.
     */
    object Default : ColorScheme() {
        /*
         * Lightness for all colors
         * L = AOSP defaults
         */
        private const val L_0    = 1.00
        private const val L_50   = 0.96
        private const val L_100  = 0.91
        private const val L_200  = 0.83
        private const val L_300  = 0.75
        private const val L_400  = 0.65
        private const val L_500  = 0.56
        private const val L_600  = 0.48
        private const val L_700  = 0.39
        private const val L_800  = 0.31
        private const val L_900  = 0.22
        private const val L_1000 = 0.00

        /*
         * Neutral targets:
         * C = derived from Google's CAM16 implementation
         */

        private const val NEUTRAL1_CHROMA = 0.012
        override val neutral1 = mapOf(
            0    to Oklch(L_0,    0.0),
            50   to Oklch(L_50,   NEUTRAL1_CHROMA),
            100  to Oklch(L_100,  NEUTRAL1_CHROMA),
            200  to Oklch(L_200,  NEUTRAL1_CHROMA),
            300  to Oklch(L_300,  NEUTRAL1_CHROMA),
            400  to Oklch(L_400,  NEUTRAL1_CHROMA),
            500  to Oklch(L_500,  NEUTRAL1_CHROMA),
            600  to Oklch(L_600,  NEUTRAL1_CHROMA),
            700  to Oklch(L_700,  NEUTRAL1_CHROMA),
            800  to Oklch(L_800,  NEUTRAL1_CHROMA),
            900  to Oklch(L_900,  NEUTRAL1_CHROMA),
            1000 to Oklch(L_1000, 0.0),
        )

        private const val NEUTRAL2_CHROMA = NEUTRAL1_CHROMA / 2.0
        override val neutral2 = mapOf(
            0    to Oklch(L_0,    0.0),
            50   to Oklch(L_50,   NEUTRAL2_CHROMA),
            100  to Oklch(L_100,  NEUTRAL2_CHROMA),
            200  to Oklch(L_200,  NEUTRAL2_CHROMA),
            300  to Oklch(L_300,  NEUTRAL2_CHROMA),
            400  to Oklch(L_400,  NEUTRAL2_CHROMA),
            500  to Oklch(L_500,  NEUTRAL2_CHROMA),
            600  to Oklch(L_600,  NEUTRAL2_CHROMA),
            700  to Oklch(L_700,  NEUTRAL2_CHROMA),
            800  to Oklch(L_800,  NEUTRAL2_CHROMA),
            900  to Oklch(L_900,  NEUTRAL2_CHROMA),
            1000 to Oklch(L_1000, 0.0),
        )

        /*
         * Accent targets:
         * C = Pixel defaults
         */

        private const val ACCENT1_CHROMA = 0.12
        override val accent1 = mapOf(
            0    to Oklch(L_0,    0.0),
            50   to Oklch(L_50,   ACCENT1_CHROMA),
            100  to Oklch(L_100,  ACCENT1_CHROMA),
            200  to Oklch(L_200,  ACCENT1_CHROMA),
            300  to Oklch(L_300,  ACCENT1_CHROMA),
            400  to Oklch(L_400,  ACCENT1_CHROMA),
            500  to Oklch(L_500,  ACCENT1_CHROMA),
            600  to Oklch(L_600,  ACCENT1_CHROMA),
            700  to Oklch(L_700,  ACCENT1_CHROMA),
            800  to Oklch(L_800,  ACCENT1_CHROMA),
            900  to Oklch(L_900,  ACCENT1_CHROMA),
            1000 to Oklch(L_1000, 0.0),
        )

        private const val ACCENT2_CHROMA = 0.04
        override val accent2 = mapOf(
            0    to Oklch(L_0,    0.0),
            50   to Oklch(L_50,   ACCENT2_CHROMA),
            100  to Oklch(L_100,  ACCENT2_CHROMA),
            200  to Oklch(L_200,  ACCENT2_CHROMA),
            300  to Oklch(L_300,  ACCENT2_CHROMA),
            400  to Oklch(L_400,  ACCENT2_CHROMA),
            500  to Oklch(L_500,  ACCENT2_CHROMA),
            600  to Oklch(L_600,  ACCENT2_CHROMA),
            700  to Oklch(L_700,  ACCENT2_CHROMA),
            800  to Oklch(L_800,  ACCENT2_CHROMA),
            900  to Oklch(L_900,  ACCENT2_CHROMA),
            1000 to Oklch(L_1000, 0.0),
        )

        private const val ACCENT3_CHROMA = 0.06
        override val accent3 = mapOf(
            0    to Oklch(L_0,    0.0),
            50   to Oklch(L_50,   ACCENT3_CHROMA),
            100  to Oklch(L_100,  ACCENT3_CHROMA),
            200  to Oklch(L_200,  ACCENT3_CHROMA),
            300  to Oklch(L_300,  ACCENT3_CHROMA),
            400  to Oklch(L_400,  ACCENT3_CHROMA),
            500  to Oklch(L_500,  ACCENT3_CHROMA),
            600  to Oklch(L_600,  ACCENT3_CHROMA),
            700  to Oklch(L_700,  ACCENT3_CHROMA),
            800  to Oklch(L_800,  ACCENT3_CHROMA),
            900  to Oklch(L_900,  ACCENT3_CHROMA),
            1000 to Oklch(L_1000, 0.0),
        )
    }
}
