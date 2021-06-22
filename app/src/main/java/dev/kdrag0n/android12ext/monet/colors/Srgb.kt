package dev.kdrag0n.android12ext.monet.colors

import kotlin.math.roundToInt
import dev.kdrag0n.android12ext.monet.colors.LinearSrgb.Companion.toLinearSrgb as realToLinearSrgb

data class Srgb(
    val r: Float,
    val g: Float,
    val b: Float,
) : Color {
    // Convenient constructors for quantized values
    constructor(r: Int, g: Int, b: Int) : this(
        r.toFloat() / 255.0f,
        g.toFloat() / 255.0f,
        b.toFloat() / 255.0f,
    )
    constructor(color: Int) : this(
        android.graphics.Color.red(color),
        android.graphics.Color.green(color),
        android.graphics.Color.blue(color),
    )

    override fun toLinearSrgb() = realToLinearSrgb()

    fun quantize8(): Int {
        return android.graphics.Color.rgb(
            quantize8(r),
            quantize8(g),
            quantize8(b),
        )
    }

    companion object {
        // Clamp out-of-bounds values
        private fun quantize8(n: Float) = (n * 255.0f).roundToInt().coerceIn(0, 255)
    }
}
