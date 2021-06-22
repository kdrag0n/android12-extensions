package dev.kdrag0n.android12ext.monet.colors

import dev.kdrag0n.android12ext.monet.colors.Lch.Companion.calcLabA
import dev.kdrag0n.android12ext.monet.colors.Lch.Companion.calcLabB
import dev.kdrag0n.android12ext.monet.colors.Lch.Companion.calcLchC
import dev.kdrag0n.android12ext.monet.colors.Lch.Companion.calcLchH

data class IptLch(
    override val L: Float,
    override val C: Float,
    override val h: Float,
) : Lch {
    override fun toLinearSrgb() = toIpt().toLinearSrgb()

    fun toIpt(): Ipt {
        return Ipt(
            I = L,
            P = calcLabA(),
            T = calcLabB(),
        )
    }

    companion object {
        fun Ipt.toIptLch(): IptLch {
            return IptLch(
                L = L,
                C = calcLchC(),
                h = calcLchH(),
            )
        }
    }
}
