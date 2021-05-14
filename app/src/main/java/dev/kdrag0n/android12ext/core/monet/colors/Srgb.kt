package dev.kdrag0n.android12ext.core.monet.colors

import android.graphics.Color

data class Srgb(
    val r: Double,
    val g: Double,
    val b: Double,
) {
    // Convenient constructors for quantized values
    constructor(r: Int, g: Int, b: Int) : this(r.toDouble() / 255.0, g.toDouble() / 255.0, b.toDouble() / 255.0)
    constructor(color: Int) : this(Color.red(color), Color.green(color), Color.blue(color))

    fun quantize8(): Int {
        return Color.rgb(
            quantize8(r),
            quantize8(g),
            quantize8(b),
        )
    }

    companion object {
        // Clamp out-of-bounds values
        private fun quantize8(n: Double) = (n * 255.0).toInt().coerceIn(0..255)
    }
}