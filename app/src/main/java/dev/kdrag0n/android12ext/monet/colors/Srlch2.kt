package dev.kdrag0n.android12ext.monet.colors

import dev.kdrag0n.android12ext.monet.colors.Lch.Companion.calcLabA
import dev.kdrag0n.android12ext.monet.colors.Lch.Companion.calcLabB
import dev.kdrag0n.android12ext.monet.colors.Lch.Companion.calcLchC
import dev.kdrag0n.android12ext.monet.colors.Lch.Companion.calcLchH

data class Srlch2(
    override val L: Double,
    override val C: Double,
    override val h: Double,
) : Color, Lch {
    override fun toLinearSrgb() = toSrlab2().toLinearSrgb()

    fun toSrlab2(): Srlab2 {
        return Srlab2(
            L = L,
            a = calcLabA(),
            b = calcLabB(),
        )
    }

    companion object {
        fun Srlab2.toSrlch2(): Srlch2 {
            return Srlch2(
                L = L,
                C = calcLchC(),
                h = calcLchH(),
            )
        }
    }
}
