package dev.kdrag0n.android12ext.monet.colors

import dev.kdrag0n.android12ext.monet.toDegrees
import dev.kdrag0n.android12ext.monet.toRadians
import kotlin.math.*

interface Lch : Color {
    val L: Float
    val C: Float
    val h: Float

    companion object {
        internal fun Lab.calcLchC() = sqrt(a*a + b*b)
        internal fun Lab.calcLchH(): Float {
            val hDeg = atan2(b, a).toDegrees()
            return if (hDeg < 0) hDeg + 360 else hDeg
        }

        internal fun Lch.calcLabA() = C * cos(h.toRadians())
        internal fun Lch.calcLabB() = C * sin(h.toRadians())
    }
}
