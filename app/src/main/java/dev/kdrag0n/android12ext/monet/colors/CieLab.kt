package dev.kdrag0n.android12ext.monet.colors

data class CieLab(
    override val L: Double,
    override val a: Double,
    override val b: Double,
) : Lab {
    override fun toLinearSrgb() = toCieXyz().toLinearSrgb()

    fun toCieXyz(): CieXyz {
        val lp = (L + 16.0) / 116.0

        return CieXyz(
            x = Illuminants.D65.x * fInv(lp + (a / 500.0)),
            y = Illuminants.D65.y * fInv(lp),
            z = Illuminants.D65.z * fInv(lp - (b / 200.0)),
        )
    }

    companion object {
        private fun f(x: Double) = if (x > 216.0/24389.0) {
            Math.cbrt(x)
        } else {
            x / (108.0/841.0) + 4.0/29.0
        }

        private fun fInv(x: Double) = if (x > 6.0/29.0) {
            x * x * x
        } else {
            (108.0/841.0) * (x - 4.0/29.0)
        }

        fun CieXyz.toCieLab(): CieLab {
            return CieLab(
                L = 116.0 * f(y / Illuminants.D65.y) - 16.0,
                a = 500.0 * (f(x / Illuminants.D65.x) - f(y / Illuminants.D65.y)),
                b = 200.0 * (f(y / Illuminants.D65.y) - f(z / Illuminants.D65.z)),
            )
        }
    }
}
