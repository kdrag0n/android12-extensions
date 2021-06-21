package dev.kdrag0n.android12ext.monet.colors

import dev.kdrag0n.android12ext.monet.colors.Lch.Companion.calcLabA
import dev.kdrag0n.android12ext.monet.colors.Lch.Companion.calcLabB
import dev.kdrag0n.android12ext.monet.colors.Lch.Companion.calcLchC
import dev.kdrag0n.android12ext.monet.colors.Lch.Companion.calcLchH

data class Cam16UcsLch(
    override val L: Double,
    override val C: Double,
    override val h: Double,
) : Lch {
    override fun toLinearSrgb() = toCam16Ucs().toLinearSrgb()

    fun toCam16Ucs(): Cam16Ucs {
        return Cam16Ucs(
            L = L,
            a = calcLabA(),
            b = calcLabB(),
        )
    }

    companion object {
        fun Cam16Ucs.toCam16UcsLch(): Cam16UcsLch {
            return Cam16UcsLch(
                L = L,
                C = calcLchC(),
                h = calcLchH(),
            )
        }
    }
}
