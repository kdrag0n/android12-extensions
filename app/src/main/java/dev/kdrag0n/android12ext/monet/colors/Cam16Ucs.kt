package dev.kdrag0n.android12ext.monet.colors

import dev.kdrag0n.android12ext.monet.colors.aosp.Cam
import dev.kdrag0n.android12ext.monet.colors.aosp.Frame
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.pow

data class Cam16Ucs(
    override val L: Double,
    override val a: Double,
    override val b: Double,
) : Color, Lab {
    override fun toLinearSrgb() = toSrgb().toLinearSrgb()

    fun toSrgb(): Srgb {
        // CAM16-UCS inverse solved from forward equations
        // Need to do this ourselves because Google only implements forward UCS
        val J = L / (1.7 - 0.007*L)
        val hRad = atan2(b, a)
        val Mp = a / cos(hRad) // a' = M' * cos(h)
        val M = (exp(0.0228 * Mp) - 1) / 0.0228

        // M -> C for Jch inverse
        val C = M / Frame.DEFAULT.fl.pow(0.25f)
        // Convert h to degrees for Google's CAM16 implementation
        val hDeg = Math.toDegrees(hRad)

        val cam = Cam.fromJch(J.toFloat(), C.toFloat(), hDeg.toFloat())
        return Srgb(cam.viewedInSrgb())
    }

    companion object {
        fun Srgb.toCam16Ucs(): Cam16Ucs {
            val cam = Cam.fromInt(quantize8())
            return Cam16Ucs(
                L = cam.jstar.toDouble(),
                a = cam.astar.toDouble(),
                b = cam.bstar.toDouble(),
            )
        }
    }
}
