package dev.kdrag0n.android12ext.monet.colors

import dev.kdrag0n.android12ext.monet.util.cube

data class Oklab(
    override val L: Double,
    override val a: Double,
    override val b: Double,
) : Lab {
    override fun toLinearSrgb(): LinearSrgb {
        val l = oklabToL(this)
        val m = oklabToM(this)
        val s = oklabToS(this)

        return LinearSrgb(
            r = +4.0767416621 * l - 3.3077115913 * m + 0.2309699292 * s,
            g = -1.2684380046 * l + 2.6097574011 * m - 0.3413193965 * s,
            b = -0.0041960863 * l - 0.7034186147 * m + 1.7076147010 * s,
        )
    }

    fun toCieXyz(): CieXyz {
        val l = oklabToL(this)
        val m = oklabToM(this)
        val s = oklabToS(this)

        return CieXyz(
            x = +1.2270138511 * l - 0.5577999807 * m + 0.2812561490 * s,
            y = -0.0405801784 * l + 1.1122568696 * m - 0.0716766787 * s,
            z = -0.0763812845 * l - 0.4214819784 * m + 1.5861632204 * s,
        )
    }

    companion object {
        private fun lmsToOklab(l: Double, m: Double, s: Double): Oklab {
            val lp = Math.cbrt(l)
            val mp = Math.cbrt(m)
            val sp = Math.cbrt(s)

            return Oklab(
                L = 0.2104542553 * lp + 0.7936177850 * mp - 0.0040720468 * sp,
                a = 1.9779984951 * lp - 2.4285922050 * mp + 0.4505937099 * sp,
                b = 0.0259040371 * lp + 0.7827717662 * mp - 0.8086757660 * sp,
            )
        }

        // Avoid arrays to minimize garbage
        private fun oklabToL(lab: Oklab) = cube(lab.L + 0.3963377774 * lab.a + 0.2158037573 * lab.b)
        private fun oklabToM(lab: Oklab) = cube(lab.L - 0.1055613458 * lab.a - 0.0638541728 * lab.b)
        private fun oklabToS(lab: Oklab) = cube(lab.L - 0.0894841775 * lab.a - 1.2914855480 * lab.b)

        fun LinearSrgb.toOklab() = lmsToOklab(
            l = 0.4122214708 * r + 0.5363325363 * g + 0.0514459929 * b,
            m = 0.2119034982 * r + 0.6806995451 * g + 0.1073969566 * b,
            s = 0.0883024619 * r + 0.2817188376 * g + 0.6299787005 * b,
        )

        fun CieXyz.toOklab() = lmsToOklab(
            l = 0.8189330101 * x + 0.3618667424 * y - 0.1288597137 * z,
            m = 0.0329845436 * x + 0.9293118715 * y + 0.0361456387 * z,
            s = 0.0482003018 * x + 0.2643662691 * y + 0.6338517070 * z,
        )
    }
}
