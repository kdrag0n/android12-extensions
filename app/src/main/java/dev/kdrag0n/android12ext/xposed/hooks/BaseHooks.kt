package dev.kdrag0n.android12ext.xposed.hooks

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import dev.kdrag0n.android12ext.utils.hookMethods
import java.lang.reflect.Member

abstract class BaseHooks(
    protected val lpparam: XC_LoadPackage.LoadPackageParam,
) {
    protected inline fun hookBefore(
        className: String,
        methodName: String,
        crossinline block: XC_MethodHook.MethodHookParam.() -> Unit,
    ) = lpparam.hookMethods(
        className,
        object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) = block(param)
        },
        methodName,
    )

    protected inline fun hookAfter(
        className: String,
        methodName: String,
        crossinline block: XC_MethodHook.MethodHookParam.() -> Unit,
    ) = lpparam.hookMethods(
        className,
        object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) = block(param)
        },
        methodName,
    )

    protected inline fun hookReplace(
        className: String,
        methodName: String,
        crossinline block: XC_MethodHook.MethodHookParam.() -> Any?,
    ) = lpparam.hookMethods(
        className,
        object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam) = block(param)
        },
        methodName,
    )

    protected fun hookReturn(
        className: String,
        methodName: String,
        returnValue: Any? = null,
    ) = lpparam.hookMethods(
        className,
        object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam) = returnValue
        },
        methodName,
    )

    protected inline fun hookBefore(
        method: Member,
        crossinline block: XC_MethodHook.MethodHookParam.() -> Unit,
    ) {
        XposedBridge.hookMethod(method, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) = block(param)
        })
    }

    protected inline fun hookAfter(
        method: Member,
        crossinline block: XC_MethodHook.MethodHookParam.() -> Unit,
    ) {
        XposedBridge.hookMethod(method, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) = block(param)
        })
    }

    protected inline fun hookReplace(
        method: Member,
        crossinline block: XC_MethodHook.MethodHookParam.() -> Unit,
    ) {
        XposedBridge.hookMethod(method, object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam) = block(param)
        })
    }

    protected inline fun <reified T> hookBefore(
        methodName: String,
        crossinline block: XC_MethodHook.MethodHookParam.() -> Unit,
    ) = hookBefore(T::class.java.name, methodName, block)

    protected inline fun <reified T> hookAfter(
        methodName: String,
        crossinline block: XC_MethodHook.MethodHookParam.() -> Unit,
    ) = hookAfter(T::class.java.name, methodName, block)

    protected inline fun <reified T> hookReplace(
        methodName: String,
        crossinline block: XC_MethodHook.MethodHookParam.() -> Any?,
    ) = hookReplace(T::class.java.name, methodName, block)

    protected inline fun <reified T> hookReturn(
        methodName: String,
        returnValue: Any? = null,
    ) = hookReturn(T::class.java.name, methodName, returnValue)

    protected inline fun hookBeforeCons(
        className: String,
        vararg args: Class<*>,
        crossinline block: XC_MethodHook.MethodHookParam.() -> Unit,
    ) = hookBefore(Class.forName(className).getDeclaredConstructor(*args), block)

    protected inline fun hookAfterCons(
        className: String,
        vararg args: Class<*>,
        crossinline block: XC_MethodHook.MethodHookParam.() -> Unit,
    ) = hookAfter(Class.forName(className).getDeclaredConstructor(*args), block)
}
