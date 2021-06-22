// These simple math functions should always be inlined for performance
@file:Suppress("NOTHING_TO_INLINE")

package dev.kdrag0n.android12ext.monet

import kotlin.math.PI

internal inline fun cube(x: Float) = x * x * x
internal inline fun square(x: Float) = x * x

internal inline fun Float.toDegrees() = this * 180 / PI.toFloat()
internal inline fun Float.toRadians() = this * PI.toFloat() / 180

internal inline fun cbrt(x: Float) = Math.cbrt(x.toDouble()).toFloat()
