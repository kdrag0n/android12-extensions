package dev.kdrag0n.android12ext.monet.colors

import dev.kdrag0n.android12ext.monet.colors.Lch.Companion.toLab
import dev.kdrag0n.android12ext.monet.colors.Lch.Companion.toLch

data class Cam16UcsLch(
    override val L: Double,
    override val C: Double,
    override val h: Double = 0.0,
) : Lch {
    fun toCam16Ucs(): Cam16Ucs {
        val (l, a, b) = toLab()
        return Cam16Ucs(l, a, b)
    }

    companion object {
        fun Cam16Ucs.toCam16UcsLch(): Cam16UcsLch {
            val (l, c, h) = toLch()
            return Cam16UcsLch(l, c, h)
        }
    }
}
