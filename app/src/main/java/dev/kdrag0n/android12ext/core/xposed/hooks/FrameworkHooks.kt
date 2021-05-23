package dev.kdrag0n.android12ext.core.xposed.hooks

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import dev.kdrag0n.android12ext.core.xposed.hookMethod

class FrameworkHooks(
    private val lpparam: XC_LoadPackage.LoadPackageParam,
) {
    fun applyRipple(patterned: Boolean) {
        val hook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                XposedHelpers.getObjectField(param.thisObject, "mState").let { state ->
                    val style = if (patterned) 1 else 0
                    XposedHelpers.setIntField(state, "mRippleStyle", style)
                }
            }
        }

        XposedHelpers.findAndHookConstructor(
            RIPPLE_CLASS,
            lpparam.classLoader,
            XposedHelpers.findClass(RIPPLE_STATE_CLASS, lpparam.classLoader),
            Resources::class.java,
            hook,
        )

        lpparam.hookMethod(RIPPLE_CLASS, hook, "updateStateFromTypedArray", TypedArray::class.java)
        lpparam.hookMethod(RIPPLE_CLASS, hook, "setRippleStyle", Int::class.java)
    }

    fun applyHapticTouch() {
        val hook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val view = param.thisObject as View
                view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            }
        }

        lpparam.hookMethod(
            "android.view.View",
            hook,
            "performButtonActionOnTouchDown",
            MotionEvent::class.java,
        )
    }

    fun applyMedianCutQuantizer() {
        val hook = object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam) = Unit
        }

        lpparam.hookMethod(
            "com.android.internal.graphics.palette.Palette\$Builder",
            hook,
            "setQuantizer",
            XposedHelpers.findClass("com.android.internal.graphics.palette.Quantizer", lpparam.classLoader),
        )
    }

    fun applyInternetFlag(enabled: Boolean) {
        val hook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                if (param.args[1] as String == "settings_provider_model") {
                    param.result = enabled
                }
            }
        }

        lpparam.hookMethod(
            "android.util.FeatureFlagUtils",
            hook,
            "isEnabled",
            Context::class.java,
            String::class.java,
        )
    }

    companion object {
        private const val RIPPLE_CLASS = "android.graphics.drawable.RippleDrawable"
        private const val RIPPLE_STATE_CLASS = "android.graphics.drawable.RippleDrawable\$RippleState"
    }
}