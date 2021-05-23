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
         * Neutral targets:
         * L = AOSP defaults
         * C: derived from AOSP defaults - ~avg C=0.005
         * h = 0 (populated by primary color)
         */

        // Luminance decreases in ~2% steps until 90%
        // After 90%, it jumps in steps of ~4-5% until ending at 70%
        override val neutral1 = mapOf(
            0    to Oklch(1.00, 0.000, 0.0),
            50   to Oklch(0.99, 0.003, 0.0),
            100  to Oklch(0.98, 0.004, 0.0),
            200  to Oklch(0.96, 0.004, 0.0),
            300  to Oklch(0.94, 0.004, 0.0),
            400  to Oklch(0.92, 0.004, 0.0),
            500  to Oklch(0.90, 0.004, 0.0),
            600  to Oklch(0.86, 0.004, 0.0),
            700  to Oklch(0.82, 0.004, 0.0),
            800  to Oklch(0.77, 0.004, 0.0),
            900  to Oklch(0.70, 0.004, 0.0),
            1000 to Oklch(0.00, 0.000, 0.0),
        )

        override val neutral2 = mapOf(
            0    to Oklch(1.00, 0.000, 0.0),
            50   to Oklch(0.99, 0.002, 0.0),
            100  to Oklch(0.98, 0.002, 0.0),
            200  to Oklch(0.96, 0.002, 0.0),
            300  to Oklch(0.94, 0.002, 0.0),
            400  to Oklch(0.92, 0.002, 0.0),
            500  to Oklch(0.90, 0.002, 0.0),
            600  to Oklch(0.86, 0.002, 0.0),
            700  to Oklch(0.82, 0.002, 0.0),
            800  to Oklch(0.77, 0.002, 0.0),
            900  to Oklch(0.70, 0.002, 0.0),
            1000 to Oklch(0.00, 0.000, 0.0),
        )

        override val accent1 = mapOf(
            0    to Oklch(1.00, 0.0000, 0.0),
            50   to Oklch(0.99, 0.0033, 0.0),
            100  to Oklch(0.98, 0.0080, 0.0),
            200  to Oklch(0.96, 0.0170, 0.0),
            300  to Oklch(0.94, 0.0284, 0.0),
            400  to Oklch(0.92, 0.0456, 0.0),
            500  to Oklch(0.90, 0.0759, 0.0),
            600  to Oklch(0.86, 0.0922, 0.0),
            700  to Oklch(0.82, 0.0908, 0.0),
            800  to Oklch(0.77, 0.0858, 0.0),
            900  to Oklch(0.70, 0.0822, 0.0),
            1000 to Oklch(0.00, 0.0000, 0.0),
        )

        /*
         * Accent targets:
         * L = AOSP defaults
         * C: Pixel defaults
         * h = 0 (populated by primary color)
         */

        override val accent2 = mapOf(
            0    to Oklch(1.00, 0.0000, 0.0),
            50   to Oklch(0.99, 0.0045, 0.0),
            100  to Oklch(0.98, 0.0084, 0.0),
            200  to Oklch(0.96, 0.0091, 0.0),
            300  to Oklch(0.94, 0.0099, 0.0),
            400  to Oklch(0.92, 0.0110, 0.0),
            500  to Oklch(0.90, 0.0131, 0.0),
            600  to Oklch(0.86, 0.0152, 0.0),
            700  to Oklch(0.82, 0.0177, 0.0),
            800  to Oklch(0.77, 0.0222, 0.0),
            900  to Oklch(0.70, 0.0332, 0.0),
            1000 to Oklch(0.00, 0.0000, 0.0),
        )

        override val accent3 = mapOf(
            0    to Oklch(1.00, 0.0000, 0.0),
            50   to Oklch(0.99, 0.0081, 0.0),
            100  to Oklch(0.98, 0.0120, 0.0),
            200  to Oklch(0.96, 0.0141, 0.0),
            300  to Oklch(0.94, 0.0155, 0.0),
            400  to Oklch(0.92, 0.0176, 0.0),
            500  to Oklch(0.90, 0.0199, 0.0),
            600  to Oklch(0.86, 0.0230, 0.0),
            700  to Oklch(0.82, 0.0288, 0.0),
            800  to Oklch(0.77, 0.0372, 0.0),
            900  to Oklch(0.70, 0.0572, 0.0),
            1000 to Oklch(0.00, 0.0000, 0.0),
        )
    }
}
