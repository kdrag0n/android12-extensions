package dev.kdrag0n.android12ext.monet.colors

import kotlin.math.*

interface Lch {
    val L: Double
    val C: Double
    val h: Double

    companion object {
        internal fun Lab.calcLchC() = sqrt(a*a + b*b)
        internal fun Lab.calcLchH(): Double {
            val hDeg = Math.toDegrees(atan2(b, a))
            return if (hDeg < 0) hDeg + 360 else hDeg
        }

        internal fun Lch.calcLabA() = C * cos(Math.toRadians(h))
        internal fun Lch.calcLabB() = C * sin(Math.toRadians(h))
    }
}
