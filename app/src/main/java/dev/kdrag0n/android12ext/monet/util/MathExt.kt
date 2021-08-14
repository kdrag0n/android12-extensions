// These simple math functions should always be inlined for performance
@file:Suppress("NOTHING_TO_INLINE")

package dev.kdrag0n.android12ext.monet.util

import kotlin.math.PI

internal inline fun cube(x: Double) = x * x * x
internal inline fun square(x: Double) = x * x
internal inline fun square(x: Float) = x * x

internal fun Double.toRadians() = this * PI / 180.0
internal fun Double.toDegrees() = this * 180.0 / PI
