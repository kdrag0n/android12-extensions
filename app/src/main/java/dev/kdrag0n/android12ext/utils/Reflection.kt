package dev.kdrag0n.android12ext.utils

import java.lang.reflect.Field

private fun getMethod(clazz: Class<*>, methodName: String, vararg args: Any) =
    clazz.getDeclaredMethod(methodName, *args.map {
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
    }.toTypedArray())

fun Any.call(methodName: String, vararg args: Any): Any? =
    getMethod(this::class.java, methodName, *args).let {
        it.isAccessible = true
        it.invoke(this, *args)
    }

fun Class<*>.callStatic(methodName: String, vararg args: Any): Any? =
    getMethod(this, methodName, *args).let {
        it.isAccessible = true
        it.invoke(null, *args)
    }

inline fun <reified T> Any.callTyped(methodName: String, vararg args: Any): T = call(methodName, *args) as T

fun Class<*>.getRawField(name: String): Field = try {
    // Prefer local/private fields
    getDeclaredField(name)
} catch (e: NoSuchFieldException) {
    // For superclass fields
    getField(name)
}

fun Any.getField(name: String): Field = this::class.java.getRawField(name).also {
    it.isAccessible = true
}

private fun Class<*>.getPrivField(name: String) = getRawField(name).also {
    it.isAccessible = true
}

inline fun <reified T> Any.get(fieldName: String): T = getField(fieldName).get(this) as T

fun Any.set(fieldName: String, value: Any?) = getField(fieldName).set(this, value)
fun Any.setInt(fieldName: String, value: Int) = getField(fieldName).setInt(this, value)
fun Any.setBool(fieldName: String, value: Boolean) = getField(fieldName).setBoolean(this, value)

fun Class<*>.set(fieldName: String, value: Any?) = getPrivField(fieldName).set(null, value)
fun Class<*>.setInt(fieldName: String, value: Int) = getPrivField(fieldName).setInt(null, value)
fun Class<*>.setBool(fieldName: String, value: Boolean) = getPrivField(fieldName).setBoolean(null, value)
