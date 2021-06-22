package dev.kdrag0n.android12ext.monet.colors

import dev.kdrag0n.android12ext.monet.colors.google.Cam
import dev.kdrag0n.android12ext.monet.colors.google.Frame
import dev.kdrag0n.android12ext.monet.toDegrees
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.pow

data class Cam16Ucs(
    override val L: Float,
    override val a: Float,
    override val b: Float,
) : Lab {
    override fun toLinearSrgb() = toSrgb().toLinearSrgb()

    fun toSrgb(): Srgb {
        // CAM16-UCS inverse solved from forward equations
        // Need to do this ourselves because Google only implements forward UCS
        val j = L / (1.7f - 0.007f*L)
        val hRad = atan2(b, a)
        val mp = a / cos(hRad) // a' = M' * cos(h)
        val m = (exp(0.0228f * mp) - 1) / 0.0228f

        // M -> C for Jch inverse
        val c = m / Frame.DEFAULT.fl.pow(0.25f)
        // Convert h to degrees for Google's CAM16 implementation
        val hDeg = hRad.toDegrees()

        val cam = Cam.fromJch(j, c, hDeg)
        return Srgb(cam.viewedInSrgb())
    }

    companion object {
        fun Srgb.toCam16Ucs(): Cam16Ucs {
            val cam = Cam.fromInt(quantize8())
            return Cam16Ucs(
                L = cam.jstar,
                a = cam.astar,
                b = cam.bstar,
            )
        }
    }
}
