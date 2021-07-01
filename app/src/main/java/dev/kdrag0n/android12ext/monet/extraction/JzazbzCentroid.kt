package dev.kdrag0n.android12ext.monet.extraction

import dev.kdrag0n.android12ext.monet.colors.CieXyz.Companion.toCieXyz
import dev.kdrag0n.android12ext.monet.colors.Jzazbz
import dev.kdrag0n.android12ext.monet.colors.Jzazbz.Companion.toJzazbz
import dev.kdrag0n.android12ext.monet.colors.Srgb
import kotlin.math.pow

class JzazbzCentroid : CentroidProvider {
    override fun getCentroid(color: Int): FloatArray {
        val jzazbz = Srgb(color).toLinearSrgb().toCieXyz().toJzazbz()
        return floatArrayOf(jzazbz.L.toFloat(), jzazbz.a.toFloat(), jzazbz.b.toFloat())
    }

    override fun getColor(color: FloatArray): Int {
        val jzazbz = Jzazbz(color[0].toDouble(), color[1].toDouble(), color[2].toDouble())
        return jzazbz.toLinearSrgb().toSrgb().quantize8()
    }

    override fun distance(a: FloatArray, b: FloatArray): Float {
        val (x1, y1, z1) = a
        val (x2, y2, z2) = b

        val dx = x1 - x2
        val dy = y1 - y2
        val dz = z1 - z2

        // sqrt is unnecessary; see https://arxiv.org/pdf/1101.0395.pdf
        // K-means / weighted sort-means compare sum of squares
        return dx*dy + dy*dy + dz*dz
    }
}
