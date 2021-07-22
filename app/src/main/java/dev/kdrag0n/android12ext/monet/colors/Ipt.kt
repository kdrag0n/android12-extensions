package dev.kdrag0n.android12ext.monet.colors

import kotlin.math.pow

data class Ipt(
    val I: Double,
    val P: Double,
    val T: Double,
) : Lab {
    override val L: Double get() = I
    override val a: Double get() = T
    override val b: Double get() = P

    override fun toLinearSrgb() = toCieXyz().toLinearSrgb()

    fun toCieXyz(): CieXyz {
        val l = fInv(1.8501 * I - 1.1383 * P + 0.2385 * T)
        val m = fInv(0.3668 * I + 0.6439 * P - 0.0107 * T)
        val s = fInv(1.0889 * T)

        return CieXyz(
            x = l + 0.0976 * m + 0.2052 * s,
            y = l - 0.1139 * m + 0.1332 * s,
            z = l + 0.0326 * m - 0.6769 * s,
        )
    }

    companion object {
        private fun f(x: Double) = x.pow(0.43)
        private fun fInv(x: Double) = x.pow(1.0 / 0.43)

        fun CieXyz.toIpt(): Ipt {
            val l2 = f(+0.4002 * x + 0.7075 * y - 0.0807 * z)
            val m2 = f(-0.2280 * x + 1.1500 * y + 0.0612 * z)
            val s2 = f(0.9184 * z)

            return Ipt(
                I = 0.4000 * l2 + 0.4000 * m2 + 0.2000 * s2,
                P = 4.4550 * l2 - 4.8510 * m2 + 0.3960 * s2,
                T = 0.8056 * l2 + 0.3572 * m2 - 1.1628 * s2,
            )
        }
    }
}
