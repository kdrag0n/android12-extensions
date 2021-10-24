package dev.kdrag0n.android12ext.utils

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

fun Class<*>.call(methodName: String, vararg args: Any): Any? =
    getMethod(this, methodName, *args).let {
        it.isAccessible = true
        it.invoke(null, *args)
    }

@JvmName("callTyped")
inline fun <reified T> Any.call(methodName: String, vararg args: Any?): T = call(methodName, args) as T

inline fun <reified T> Any.get(fieldName: String): T = this::class.java.getDeclaredField(fieldName).get(this) as T

fun Any.set(fieldName: String, value: Any?) = this::class.java.getDeclaredField(fieldName).set(this, value)
fun Any.setInt(fieldName: String, value: Int) = this::class.java.getDeclaredField(fieldName).setInt(this, value)
fun Any.setBool(fieldName: String, value: Boolean) = this::class.java.getDeclaredField(fieldName).setBoolean(this, value)

fun Class<*>.set(fieldName: String, value: Any?) = getDeclaredField(fieldName).set(null, value)
fun Class<*>.setInt(fieldName: String, value: Int) = getDeclaredField(fieldName).setInt(null, value)
fun Class<*>.setBool(fieldName: String, value: Boolean) = getDeclaredField(fieldName).setBoolean(null, value)
