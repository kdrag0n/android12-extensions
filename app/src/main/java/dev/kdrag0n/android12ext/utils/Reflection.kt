package dev.kdrag0n.android12ext.utils

fun Any.call(methodName: String, vararg args: Any): Any? =
    this::class.java.getDeclaredMethod(methodName, *args.map {
        when (it) {
            // Map primitive types
            is Boolean -> Boolean::class.java
            is Int -> Int::class.java
            is Long -> Long::class.java
            is Float -> Float::class.java
            is Double -> Double::class.java
            is Short -> Short::class.java
            is Byte -> Byte::class.java
            is Char -> Char::class.java
            else -> it::class.java
        }
    }.toTypedArray()).let {
        it.isAccessible = true
        it.invoke(this, *args)
    }
