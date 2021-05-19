package dev.kdrag0n.android12ext.core.monet.theme

fun smoothstep(edge0: Double, edge1: Double, x: Double): Double {
    val x2 = ((x - edge0) / (edge1 - edge0)).coerceIn(0.0, 1.0)
    return x2 * x2 * (3 - 2 * x2)
}