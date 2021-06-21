package dev.kdrag0n.android12ext.monet.colors

// Interface for Lab complementary color spaces
interface Lab : Color {
    val L: Double
    val a: Double
    val b: Double
}
