package dev.kdrag0n.android12ext.monet.colors

import kotlin.math.pow

data class LinearSrgb(
    val r: Float,
    val g: Float,
    val b: Float,
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
        private fun f(x: Float) = if (x >= 0.0031308f) {
            1.055f * x.pow(1.0f / 2.4f) - 0.055f
        } else {
            12.92f * x
        }

        // sRGB -> linear
        private fun fInv(x: Float) = if (x >= 0.04045f) {
            ((x + 0.055f) / 1.055f).pow(2.4f)
        } else {
            x / 12.92f
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
