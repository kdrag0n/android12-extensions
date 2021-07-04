package dev.kdrag0n.android12ext.monet.colors

import dev.kdrag0n.android12ext.monet.cbrt
import kotlin.math.pow

data class Srlab2(
    override val L: Float,
    override val a: Float,
    override val b: Float,
) : Lab {
    override fun toLinearSrgb(): LinearSrgb {
        val x = fInv(0.01f * L + 0.000904127f * a + 0.000456344f * b)
        val y = fInv(0.01f * L - 0.000533159f * a - 0.000269178f * b)
        val z = fInv(0.01f * L                    - 0.005800000f * b)

        return LinearSrgb(
            r =  5.435679f * x - 4.599131f * y + 0.163593f * z,
            g = -1.168090f * x + 2.327977f * y - 0.159798f * z,
            b =  0.037840f * x - 0.198564f * y + 1.160644f * z,
        )
    }

    companion object {
        private fun f(x: Float) = if (x <= 216.0f / 24389.0f) {
            x * 24389.0f / 2700.0f
        } else {
            1.16f * cbrt(x) - 0.16f
        }

        private fun fInv(x: Float) = if (x <= 0.08f) {
            x * 2700.0f / 24389.0f
        } else {
            ((x + 0.16f) / 1.16f).pow(3)
        }

        fun LinearSrgb.toSrlab2(): Srlab2 {
            val x2 = f(0.320530f * r + 0.636920f * g + 0.042560f * b)
            val y2 = f(0.161987f * r + 0.756636f * g + 0.081376f * b)
            val z2 = f(0.017228f * r + 0.108660f * g + 0.874112f * b)

            return Srlab2(
                L =  37.0950f * x2 +  62.9054f * y2 -   0.0008f * z2,
                a = 663.4684f * x2 - 750.5078f * y2 +  87.0328f * z2,
                b =  63.9569f * x2 + 108.4576f * y2 - 172.4152f * z2,
            )
        }
    }
}
