package dev.kdrag0n.android12ext.monet.colors

import kotlin.math.pow

data class Ipt(
    val I: Float,
    val P: Float,
    val T: Float,
) : Lab {
    override val L: Float get() = I
    override val a: Float get() = T
    override val b: Float get() = P

    override fun toLinearSrgb() = toCieXyz().toLinearSrgb()

    fun toCieXyz(): CieXyz {
        val l = fInv(1.8501f * I - 1.1383f * P + 0.2385f * T)
        val m = fInv(0.3668f * I + 0.6439f * P - 0.0107f * T)
        val s = fInv(1.0889f * T)

        return CieXyz(
            x = l + 0.0976f * m + 0.2052f * s,
            y = l - 0.1139f * m + 0.1332f * s,
            z = l + 0.0326f * m - 0.6769f * s,
        )
    }

    companion object {
        private fun f(x: Float) = x.pow(0.43f)
        private fun fInv(x: Float) = x.pow(1.0f / 0.43f)

        fun CieXyz.toIpt(): Ipt {
            val l2 = f(+0.4002f * x + 0.7075f * y - 0.0807f * z)
            val m2 = f(-0.2280f * x + 1.1500f * y + 0.0612f * z)
            val s2 = f(0.9184f * z)

            return Ipt(
                I = 0.4000f * l2 + 0.4000f * m2 + 0.2000f * s2,
                P = 4.4550f * l2 - 4.8510f * m2 + 0.3960f * s2,
                T = 0.8056f * l2 + 0.3572f * m2 - 1.1628f * s2,
            )
        }
    }
}
