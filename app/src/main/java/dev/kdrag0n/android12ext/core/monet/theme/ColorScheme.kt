package dev.kdrag0n.android12ext.core.monet.theme

import dev.kdrag0n.android12ext.core.monet.colors.Color

abstract class ColorScheme {
    abstract val neutral1: List<Color>
    abstract val neutral2: List<Color>

    abstract val accent1: List<Color>
    abstract val accent2: List<Color>
    abstract val accent3: List<Color>

    // Helpers
    val neutralColors: List<List<Color>>
        get() = listOf(neutral1, neutral2)
    val accentColors: List<List<Color>>
        get() = listOf(accent1, accent2, accent3)
}