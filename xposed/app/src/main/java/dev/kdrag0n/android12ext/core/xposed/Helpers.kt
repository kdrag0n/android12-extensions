package dev.kdrag0n.android12ext.core.xposed

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

fun XC_LoadPackage.LoadPackageParam.hookMethod(
        className: String,
        hook: XC_MethodHook,
        methodName: String,
        vararg argTypes: Class<*>
) {
    XposedHelpers.findClass(className, classLoader)
            .getDeclaredMethod(methodName, *argTypes)
            .let { method ->
                XposedBridge.hookMethod(method, hook)
            }
}