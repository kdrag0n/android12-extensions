package dev.kdrag0n.android12ext.utils

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kotlin.reflect.KClass

fun XC_LoadPackage.LoadPackageParam.hookMethod(
    className: String,
    hook: XC_MethodHook,
    methodName: String,
    vararg argTypes: Class<*>
) {
    getClass(className)
        .getDeclaredMethod(methodName, *argTypes)
        .let { method ->
            XposedBridge.hookMethod(method, hook)
        }
}

fun XC_LoadPackage.LoadPackageParam.hookMethods(
    className: String,
    hook: XC_MethodHook,
    methodName: String,
) {
    getClass(className)
        .declaredMethods
        .filter { it.name == methodName }
        .forEach { method ->
            XposedBridge.hookMethod(method, hook)
        }
}

fun XC_LoadPackage.LoadPackageParam.hookMethods(
    clazz: KClass<*>,
    hook: XC_MethodHook,
    methodName: String,
) {
    hookMethods(clazz.java.name, hook, methodName)
}

fun XC_LoadPackage.LoadPackageParam.getClass(className: String) =
    XposedHelpers.findClass(className, classLoader)

private class DebugStackException(message: String) : RuntimeException(message)
fun dumpStack(message: String) = DebugStackException(message).printStackTrace()
