package dev.kdrag0n.android12ext.monet.colors

interface Color {
    // All colors should have a conversion path to linear sRGB
    fun toLinearSrgb(): LinearSrgb
}