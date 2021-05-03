package dev.kdrag0n.android12ext.core.xposed.hooks

import android.content.res.Resources
import android.content.res.TypedArray
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import dev.kdrag0n.android12ext.core.xposed.hookMethod

object FrameworkHooks {
    private const val RIPPLE_CLASS = "android.graphics.drawable.RippleDrawable"
    private const val RIPPLE_STATE_CLASS = "android.graphics.drawable.RippleDrawable\$RippleState"

    private val rippleHook = object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            XposedHelpers.getObjectField(param.thisObject, "mState").let { state ->
                XposedHelpers.setIntField(state, "mRippleStyle", 1)
            }
        }
    }

    private val hapticTouchHook = object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            val view = param.thisObject as View
            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
        }
    }

    fun applyRipple(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedHelpers.findAndHookConstructor(
                RIPPLE_CLASS,
                lpparam.classLoader,
                XposedHelpers.findClass(RIPPLE_STATE_CLASS, lpparam.classLoader),
                Resources::class.java,
                rippleHook,
        )

        lpparam.hookMethod(RIPPLE_CLASS, rippleHook, "updateStateFromTypedArray", TypedArray::class.java)
        lpparam.hookMethod(RIPPLE_CLASS, rippleHook, "setRippleStyle", Int::class.java)
    }

    fun applyHapticTouch(lpparam: XC_LoadPackage.LoadPackageParam) {
        lpparam.hookMethod(
            "android.view.View",
            hapticTouchHook,
            "performButtonActionOnTouchDown",
            MotionEvent::class.java,
        )
    }
}