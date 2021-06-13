package dev.kdrag0n.android12ext.core.xposed.hooks

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.RippleDrawable
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import dev.kdrag0n.android12ext.core.RippleShader
import dev.kdrag0n.android12ext.core.xposed.hookMethod
import dev.kdrag0n.android12ext.monet.extraction.JzazbzCentroid

class FrameworkHooks(
    private val lpparam: XC_LoadPackage.LoadPackageParam,
) {
    fun applyRippleStyle(patterned: Boolean) {
        val hook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val style = if (patterned) 1 else 0
                XposedHelpers.setIntField(param.thisObject, "mRippleStyle", style)
            }
        }

        XposedHelpers.findAndHookConstructor(
            "android.graphics.drawable.RippleDrawable\$RippleState",
            lpparam.classLoader,
            XposedHelpers.findClass("android.graphics.drawable.LayerDrawable\$LayerState", lpparam.classLoader),
            RippleDrawable::class.java,
            Resources::class.java,
            hook,
        )
    }

    fun applyCustomRipple() {
        val hook = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                // Hijack RippleShader super calls
                if (param.thisObject::class.java.name != "android.graphics.drawable.RippleShader") {
                    return
                }

                param.args[0] = RippleShader.SHADER
            }
        }

        XposedHelpers.findAndHookConstructor(
            "android.graphics.RuntimeShader",
            lpparam.classLoader,
            String::class.java,
            Boolean::class.java,
            hook,
        )
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

    fun applyQuantizerColorspace() {
        val centroidProvider = JzazbzCentroid()

        lpparam.hookMethod(CENTROID_CLASS, object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam) =
                centroidProvider.getCentroid(param.args[0] as Int)
        }, "getCentroid", Int::class.java)

        lpparam.hookMethod(CENTROID_CLASS, object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam) =
                centroidProvider.getColor(param.args[0] as FloatArray)
        }, "getColor", FloatArray::class.java)

        lpparam.hookMethod(CENTROID_CLASS, object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam) =
                centroidProvider.distance(param.args[0] as FloatArray, param.args[1] as FloatArray)
        }, "distance", FloatArray::class.java, FloatArray::class.java)
    }

    companion object {
        private const val CENTROID_CLASS = "com.android.internal.graphics.palette.LABCentroid"
    }
}
