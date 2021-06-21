package dev.kdrag0n.android12ext.monet.colors

import dev.kdrag0n.android12ext.monet.colors.Lch.Companion.calcLabA
import dev.kdrag0n.android12ext.monet.colors.Lch.Companion.calcLabB
import dev.kdrag0n.android12ext.monet.colors.Lch.Companion.calcLchC
import dev.kdrag0n.android12ext.monet.colors.Lch.Companion.calcLchH

data class Oklch(
    override val L: Double,
    override val C: Double,
    override val h: Double,
) : Color, Lch {
    override fun toLinearSrgb() = toOklab().toLinearSrgb()

    fun toOklab(): Oklab {
        return Oklab(
            L = L,
            a = calcLabA(),
            b = calcLabB(),
        )
    }

    companion object {
        fun Oklab.toOklch(): Oklch {
            return Oklch(
                L = L,
                C = calcLchC(),
                h = calcLchH(),
            )
        }
    }
}
