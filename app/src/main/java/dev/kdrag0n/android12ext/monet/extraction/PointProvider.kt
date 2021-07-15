package dev.kdrag0n.android12ext.monet.extraction

import androidx.annotation.ColorInt

interface PointProvider {
    // RGB8 -> Lab
    fun fromInt(@ColorInt color: Int): FloatArray
    // Lab -> RGB8
    @ColorInt fun toInt(color: FloatArray): Int

    // Must be Euclidean distance for K-means/WSM
    fun distance(a: FloatArray, b: FloatArray): Float
}
