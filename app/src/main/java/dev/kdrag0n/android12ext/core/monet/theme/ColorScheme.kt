package dev.kdrag0n.android12ext.core.monet.theme

abstract class ColorScheme {
    abstract val neutral1: List<Int>
    abstract val neutral2: List<Int>

    abstract val accent1: List<Int>
    abstract val accent2: List<Int>
    abstract val accent3: List<Int>

    // Helpers
    val neutralColors: List<List<Int>>
        get() = listOf(neutral1, neutral2)
    val accentColors: List<List<Int>>
        get() = listOf(accent1, accent2, accent3)
}