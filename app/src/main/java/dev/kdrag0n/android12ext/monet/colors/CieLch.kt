package dev.kdrag0n.android12ext.monet.colors

import dev.kdrag0n.android12ext.monet.colors.Lch.Companion.toLab
import dev.kdrag0n.android12ext.monet.colors.Lch.Companion.toLch

data class CieLch(
    override val L: Double,
    override val C: Double,
    override val h: Double = 0.0,
) : Lch {
    fun toCieLab(): CieLab {
        val (l, a, b) = toLab()
        return CieLab(l, a, b)
    }

    companion object {
        fun CieLab.toCieLch(): CieLch {
            val (l, c, h) = toLch()
            return CieLch(l, c, h)
        }
    }
}
