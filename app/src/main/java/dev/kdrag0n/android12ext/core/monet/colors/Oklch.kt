package dev.kdrag0n.android12ext.core.monet.colors

import kotlin.math.*

data class Oklch(
    val L: Double,
    val C: Double,
    val h: Double,
) {
    fun toOklab(): Oklab {
        val hRad = Math.toRadians(h)

        return Oklab(
            L = L,
            a = C * cos(hRad),
            b = C * sin(hRad),
        )
    }

    companion object {
        fun Oklab.toOklch(): Oklch {
            val hDeg = Math.toDegrees(atan2(b, a))

            return Oklch(
                L = L,
                C = sqrt(a.pow(2) + b.pow(2)),
                // Normalize the angle, as many will be negative
                h = if (hDeg < 0) hDeg + 360 else hDeg,
            )
        }
    }
}