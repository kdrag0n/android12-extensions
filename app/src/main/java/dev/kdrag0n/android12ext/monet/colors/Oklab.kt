package dev.kdrag0n.android12ext.monet.colors

import dev.kdrag0n.android12ext.monet.cbrt
import dev.kdrag0n.android12ext.monet.cube

data class Oklab(
    override val L: Float,
    override val a: Float,
    override val b: Float,
) : Lab {
    override fun toLinearSrgb(): LinearSrgb {
        val l = oklabToL(this)
        val m = oklabToM(this)
        val s = oklabToS(this)

        return LinearSrgb(
            r = +4.0767416621f * l - 3.3077115913f * m + 0.2309699292f * s,
            g = -1.2684380046f * l + 2.6097574011f * m - 0.3413193965f * s,
            b = -0.0041960863f * l - 0.7034186147f * m + 1.7076147010f * s,
        )
    }

    fun toCieXyz(): CieXyz {
        val l = oklabToL(this)
        val m = oklabToM(this)
        val s = oklabToS(this)

        return CieXyz(
            x = +1.2270138511f * l - 0.5577999807f * m + 0.2812561490f * s,
            y = -0.0405801784f * l + 1.1122568696f * m - 0.0716766787f * s,
            z = -0.0763812845f * l - 0.4214819784f * m + 1.5861632204f * s,
        )
    }

    companion object {
        private fun lmsToOklab(l: Float, m: Float, s: Float): Oklab {
            val lp = cbrt(l)
            val mp = cbrt(m)
            val sp = cbrt(s)

            return Oklab(
                L = 0.2104542553f * lp + 0.7936177850f * mp - 0.0040720468f * sp,
                a = 1.9779984951f * lp - 2.4285922050f * mp + 0.4505937099f * sp,
                b = 0.0259040371f * lp + 0.7827717662f * mp - 0.8086757660f * sp,
            )
        }

        // Avoid arrays to minimize garbage
        private fun oklabToL(lab: Oklab) = cube(lab.L + 0.3963377774f * lab.a + 0.2158037573f * lab.b)
        private fun oklabToM(lab: Oklab) = cube(lab.L - 0.1055613458f * lab.a - 0.0638541728f * lab.b)
        private fun oklabToS(lab: Oklab) = cube(lab.L - 0.0894841775f * lab.a - 1.2914855480f * lab.b)

        fun LinearSrgb.toOklab() = lmsToOklab(
            l = 0.4122214708f * r + 0.5363325363f * g + 0.0514459929f * b,
            m = 0.2119034982f * r + 0.6806995451f * g + 0.1073969566f * b,
            s = 0.0883024619f * r + 0.2817188376f * g + 0.6299787005f * b,
        )

        fun CieXyz.toOklab() = lmsToOklab(
            l = 0.8189330101f * x + 0.3618667424f * y - 0.1288597137f * z,
            m = 0.0329845436f * x + 0.9293118715f * y + 0.0361456387f * z,
            s = 0.0482003018f * x + 0.2643662691f * y + 0.6338517070f * z,
        )
    }
}
