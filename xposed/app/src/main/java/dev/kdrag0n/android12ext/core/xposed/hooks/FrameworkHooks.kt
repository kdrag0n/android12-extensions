package dev.kdrag0n.android12ext.core.xposed.hooks

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.util.AttributeSet
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import dev.kdrag0n.android12ext.core.xposed.hookMethod

object FrameworkHooks {
    private const val RIPPLE_CLASS = "android.graphics.drawable.RippleDrawable"
    private const val RIPPLE_STATE_CLASS = "android.graphics.drawable.RippleDrawable\$RippleState"
    private const val EDGE_CLASS = "android.widget.EdgeEffect"

    private val ripple = object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            XposedHelpers.getObjectField(param.thisObject, "mState").let { state ->
                XposedHelpers.setIntField(state, "mRippleStyle", 1)
            }
        }
    }

    private val edgeEffect = object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            XposedHelpers.setIntField(param.thisObject, "mEdgeEffectType", 1)
        }
    }

    fun applyRipple(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedHelpers.findAndHookConstructor(
                RIPPLE_CLASS,
                lpparam.classLoader,
                XposedHelpers.findClass(RIPPLE_STATE_CLASS, lpparam.classLoader),
                Resources::class.java,
                ripple,
        )

        lpparam.hookMethod(RIPPLE_CLASS, ripple, "updateStateFromTypedArray", TypedArray::class.java)
        lpparam.hookMethod(RIPPLE_CLASS, ripple, "setRippleStyle", Int::class.java)
    }

    fun applyEdge(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedHelpers.findAndHookConstructor(
                EDGE_CLASS,
                lpparam.classLoader,
                Context::class.java,
                AttributeSet::class.java,
                edgeEffect,
        )

        lpparam.hookMethod(EDGE_CLASS, edgeEffect, "setType", Int::class.java)
    }
}