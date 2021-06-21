package dev.kdrag0n.android12ext.monet.colors

import kotlin.math.pow

data class LinearSrgb(
    val r: Double,
    val g: Double,
    val b: Double,
) : Color {
    override fun toLinearSrgb() = this

    fun toSrgb(): Srgb {
        return Srgb(
            r = f(r),
            g = f(g),
            b = f(b),
        )
    }

    companion object {
        // Linear -> sRGB
        private fun f(x: Double) = if (x >= 0.0031308) {
            1.055 * x.pow(1.0 / 2.4) - 0.055
        } else {
            12.92 * x
        }

        // sRGB -> linear
        private fun fInv(x: Double) = if (x >= 0.04045) {
            ((x + 0.055) / 1.055).pow(2.4)
        } else {
            x / 12.92
        }

        fun Srgb.toLinearSrgb(): LinearSrgb {
            return LinearSrgb(
                r = fInv(r),
                g = fInv(g),
                b = fInv(b),
            )
        }
    }
}
