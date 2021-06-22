package dev.kdrag0n.android12ext.monet.colors

import kotlin.math.pow

data class Jzazbz(
    override val L: Float,
    override val a: Float,
    override val b: Float,
) : Lab {
    override fun toLinearSrgb() = toCieXyz().toLinearSrgb()

    fun toCieXyz(): CieXyz {
        val jz = L + 1.6295499532821566e-11f
        val iz = jz / (0.44f + 0.56f*jz)

        val l = pqInv(iz + 1.386050432715393e-1f*a + 5.804731615611869e-2f*b)
        val m = pqInv(iz - 1.386050432715393e-1f*a - 5.804731615611891e-2f*b)
        val s = pqInv(iz - 9.601924202631895e-2f*a - 8.118918960560390e-1f*b)

        if (l.isNaN() || m.isNaN() || s.isNaN()) {
            return CieXyz(0.0f, 0.0f, 0.0f)
        }

        return CieXyz(
            + 1.661373055774069e+00f*l - 9.145230923250668e-01f*m + 2.313620767186147e-01f*s,
            - 3.250758740427037e-01f*l + 1.571847038366936e+00f*m - 2.182538318672940e-01f*s,
            - 9.098281098284756e-02f*l - 3.127282905230740e-01f*m + 1.522766561305260e+00f*s,
        )
    }

    companion object {
        // Perceptual Quantizer transfer function
        private fun pq(x: Float): Float {
            val xp = (x * 1e-4f).pow(0.1593017578125f)
            return ((0.8359375f + 18.8515625f * xp) / (1f + 18.6875f * xp)).pow(134.034375f)
        }

        // Inverse PQ transfer function
        private fun pqInv(x: Float): Float {
            val xp = x.pow(7.460772656268214e-03f)
            return 1e4f * ((0.8359375f - xp) / (18.6875f * xp - 18.8515625f)).pow(6.277394636015326f)
        }

        fun CieXyz.toJzazbz(): Jzazbz {
            val lp = pq(0.674207838f*x + 0.382799340f*y - 0.047570458f*z)
            val mp = pq(0.149284160f*x + 0.739628340f*y + 0.083327300f*z)
            val sp = pq(0.070941080f*x + 0.174768000f*y + 0.670970020f*z)

            val iz = 0.5f * (lp + mp)
            val az = 3.524000f*lp - 4.066708f*mp + 0.542708f*sp
            val bz = 0.199076f*lp + 1.096799f*mp - 1.295875f*sp
            val jz = (0.44f * iz) / (1f - 0.56f*iz) - 1.6295499532821566e-11f

            return Jzazbz(
                L = jz,
                a = az,
                b = bz,
            )
        }
    }
}
