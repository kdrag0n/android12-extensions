package dev.kdrag0n.android12ext.monet.util

import kotlin.jvm.JvmInline

@JvmInline
value class Vector3(
    val values: DoubleArray,
) {
    constructor(
        n1: Double, n2: Double, n3: Double,
    ) : this(doubleArrayOf(n1, n2, n3))

    operator fun times(matrix: Matrix3) = matrix * this

    operator fun div(vec: Vector3): Vector3 {
        val (l1, l2, l3) = this
        val (r1, r2, r3) = vec

        return Vector3(
            l1 / r1,
            l2 / r2,
            l3 / r3,
        )
    }

    fun toDiagMatrix(): Matrix3 {
        val (n1, n2, n3) = this

        return Matrix3(
            n1, 0.0, 0.0,
            0.0, n2, 0.0,
            0.0, 0.0, n3,
        )
    }

    operator fun component1() = values[0]
    operator fun component2() = values[1]
    operator fun component3() = values[2]
}
