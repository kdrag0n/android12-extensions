package dev.kdrag0n.android12ext.core.monet.colors

import kotlin.math.pow

data class LinearSrgb(
    val r: Double,
    val g: Double,
    val b: Double,
) {
    fun toSrgb(): Srgb {
        return Srgb(
            r = eotf(r),
            g = eotf(g),
            b = eotf(b),
        )
    }

    companion object {
        // Electro-optical transfer function
        private fun eotf(x: Double): Double {
            return if (x >= 0.04045) {
                ((x + 0.055) / 1.055).pow(2.4)
            } else {
                x / 12.92
            }
        }

        // Opto-electrical transfer function
        private fun oetf(x: Double): Double {
            return if (x >= 0.0031308) {
                1.055 * x.pow(1.0 / 2.4) - 0.055
            } else {
                12.92 * x
            }
        }

        fun Srgb.toLinearSrgb(): LinearSrgb {
            return LinearSrgb(
                r = oetf(r),
                g = oetf(g),
                b = oetf(b),
            )
        }
    }
}