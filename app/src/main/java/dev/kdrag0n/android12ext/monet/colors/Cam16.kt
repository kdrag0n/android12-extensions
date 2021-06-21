package dev.kdrag0n.android12ext.monet.colors

import dev.kdrag0n.android12ext.monet.colors.aosp.Cam

data class Cam16(
    override val L: Double,
    override val C: Double,
    override val h: Double,
) : Lch {
    override fun toLinearSrgb() = toSrgb().toLinearSrgb()

    fun toSrgb(): Srgb {
        val cam = Cam.fromJch(L.toFloat(), C.toFloat(), h.toFloat())
        return Srgb(cam.viewedInSrgb())
    }

    companion object {
        fun Srgb.toCam16(): Cam16 {
            val cam = Cam.fromInt(quantize8())
            return Cam16(
                L = cam.j.toDouble(),
                C = cam.chroma.toDouble(),
                h = cam.hue.toDouble(),
            )
        }
    }
}
