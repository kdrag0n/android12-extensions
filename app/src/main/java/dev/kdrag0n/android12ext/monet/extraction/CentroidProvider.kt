package dev.kdrag0n.android12ext.monet.extraction

import androidx.annotation.ColorInt

interface CentroidProvider {
    // RGB8 -> Lab
    fun getCentroid(@ColorInt color: Int): FloatArray
    // Lab -> RGB8
    @ColorInt fun getColor(color: FloatArray): Int

    // Must be Euclidean distance for K-means/WSM
    fun distance(a: FloatArray, b: FloatArray): Float
}
