package dev.kdrag0n.android12ext.monet.colors

import dev.kdrag0n.android12ext.monet.cbrt
import dev.kdrag0n.android12ext.monet.cube

data class CieLab(
    override val L: Float,
    override val a: Float,
    override val b: Float,
) : Lab {
    override fun toLinearSrgb() = toCieXyz().toLinearSrgb()

    fun toCieXyz(): CieXyz {
        val lp = (L + 16.0f) / 116.0f

        return CieXyz(
            x = Illuminants.D65.x * fInv(lp + (a / 500.0f)),
            y = Illuminants.D65.y * fInv(lp),
            z = Illuminants.D65.z * fInv(lp - (b / 200.0f)),
        )
    }

    companion object {
        private fun f(x: Float) = if (x > 216.0f/24389.0f) {
            cbrt(x)
        } else {
            x / (108.0f/841.0f) + 4.0f/29.0f
        }

        private fun fInv(x: Float) = if (x > 6.0f/29.0f) {
            cube(x)
        } else {
            (108.0f/841.0f) * (x - 4.0f/29.0f)
        }

        fun CieXyz.toCieLab(): CieLab {
            return CieLab(
                L = 116.0f * f(y / Illuminants.D65.y) - 16.0f,
                a = 500.0f * (f(x / Illuminants.D65.x) - f(y / Illuminants.D65.y)),
                b = 200.0f * (f(y / Illuminants.D65.y) - f(z / Illuminants.D65.z)),
            )
        }
    }
}
