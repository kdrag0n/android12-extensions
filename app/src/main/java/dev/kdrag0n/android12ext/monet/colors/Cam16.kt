package dev.kdrag0n.android12ext.monet.colors

import dev.kdrag0n.android12ext.monet.colors.google.Cam

data class Cam16(
    override val L: Float,
    override val C: Float,
    override val h: Float,
) : Lch {
    override fun toLinearSrgb() = toSrgb().toLinearSrgb()

    fun toSrgb(): Srgb {
        val cam = Cam.fromJch(L, C, h)
        return Srgb(cam.viewedInSrgb())
    }

    companion object {
        fun Srgb.toCam16(): Cam16 {
            val cam = Cam.fromInt(quantize8())
            return Cam16(
                L = cam.j,
                C = cam.chroma,
                h = cam.hue,
            )
        }
    }
}
