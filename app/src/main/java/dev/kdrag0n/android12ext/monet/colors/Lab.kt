package dev.kdrag0n.android12ext.monet.colors

// Interface for Lab complementary color spaces
interface Lab : Color {
    val L: Float
    val a: Float
    val b: Float
}
