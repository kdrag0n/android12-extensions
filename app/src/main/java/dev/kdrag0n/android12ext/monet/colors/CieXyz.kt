package dev.kdrag0n.android12ext.monet.colors

data class CieXyz(
    val x: Float,
    val y: Float,
    val z: Float,
) : Color {
    override fun toLinearSrgb(): LinearSrgb {
        return LinearSrgb(
            r = +3.2404542f * x + -1.5371385f * y + -0.4985314f * z,
            g = -0.9692660f * x + +1.8760108f * y + +0.0415560f * z,
            b = +0.0556434f * x + -0.2040259f * y + +1.0572252f * z,
        )
    }

    companion object {
        fun LinearSrgb.toCieXyz(): CieXyz {
            return CieXyz(
                x = 0.4124564f * r + 0.3575761f * g + 0.1804375f * b,
                y = 0.2126729f * r + 0.7151522f * g + 0.0721750f * b,
                z = 0.0193339f * r + 0.1191920f * g + 0.9503041f * b,
            )
        }
    }
}
