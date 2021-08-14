package dev.kdrag0n.android12ext.xposed.hooks

import android.animation.Animator
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.RippleDrawable
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.PathInterpolator
import androidx.core.animation.addListener
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import dev.kdrag0n.android12ext.core.ripple.RIPPLE_SHADER_FLUENT
import dev.kdrag0n.android12ext.core.ripple.RIPPLE_SHADER_NO_SPARKLES
import dev.kdrag0n.android12ext.xposed.hookMethod
import dev.kdrag0n.android12ext.monet.extraction.JzazbzPointProvider
import java.util.function.Consumer

class FrameworkHooks(
    private val lpparam: XC_LoadPackage.LoadPackageParam,
) {
    private fun applyRippleShader(shader: String) {
        val hook = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                // Only hijack RippleShader super calls
                if (param.thisObject::class.java.name != "android.graphics.drawable.RippleShader") {
                    return
                }

                param.args[0] = shader
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

    fun applyNoSparklesRipple() {
        applyRippleShader(RIPPLE_SHADER_NO_SPARKLES)
    }

    fun applyLegacyRipple() {
        val hook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                XposedHelpers.setIntField(param.thisObject, "mRippleStyle", 0)
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

    @Suppress("PrivateApi", "unchecked_cast")
    fun applyFluentRipple() {
        // Replace ripple shader
        applyRippleShader(RIPPLE_SHADER_FLUENT)

        // Reduce duration of enter animation
        // Change expand curve to linear
        val linearInterpolator = LinearInterpolator()
        val hookStart = object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam) {
                val expand = param.args[0] as Animator
                expand.duration = 350
                expand.addListener(onEnd = {
                    XposedHelpers.callMethod(param.thisObject, "onAnimationEnd", it)
                })
                expand.interpolator = linearInterpolator
                expand.start()

                val loop = param.args[1] as Animator
                loop.duration = 7000
                loop.addListener(onEnd = {
                    XposedHelpers.callMethod(param.thisObject, "onAnimationEnd", it)
                    XposedHelpers.setObjectField(param.thisObject, "mLoopAnimation", null)
                })
                loop.interpolator = linearInterpolator
                loop.start()

                val mLoopAnimation = XposedHelpers.getObjectField(param.thisObject, "mLoopAnimation") as Animator?
                mLoopAnimation?.cancel()
                XposedHelpers.setObjectField(param.thisObject, "mLoopAnimation", loop)
            }
        }
        lpparam.hookMethod(
            "android.graphics.drawable.RippleAnimationSession",
            hookStart,
            "startAnimation",
            Animator::class.java,
            Animator::class.java,
        )

        // Remove start delay and increase duration to compensate
        val hookExit = object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam) {
                val canvas = param.args[0]

                val canvasProperties = XposedHelpers.callMethod(param.thisObject, "getCanvasProperties")
                val progressProp = XposedHelpers.callMethod(canvasProperties, "getProgress")
                val exit = Class.forName("android.graphics.animation.RenderNodeAnimator")
                    .getDeclaredConstructor(Class.forName("android.graphics.CanvasProperty"), Float::class.java)
                    .newInstance(progressProp, 1.0f) as Animator

                exit.duration = 450
                exit.addListener(onEnd = {
                    XposedHelpers.callMethod(param.thisObject, "onAnimationEnd", it)
                    val onEnd = XposedHelpers.getObjectField(param.thisObject, "mOnSessionEnd") as Consumer<Any?>?
                    onEnd?.accept(param.thisObject)
                })

                XposedHelpers.callMethod(exit, "setTarget", canvas)
                exit.interpolator = linearInterpolator
                exit.start()
            }
        }
        lpparam.hookMethod(
            "android.graphics.drawable.RippleAnimationSession",
            hookExit,
            "exitHardware",
            XposedHelpers.findClass("android.graphics.RecordingCanvas", lpparam.classLoader),
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
        val centroidProvider = JzazbzPointProvider()

        lpparam.hookMethod(LAB_POINT_CLASS, object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam) =
                centroidProvider.fromInt(param.args[0] as Int)
        }, "fromInt", Int::class.java)

        lpparam.hookMethod(LAB_POINT_CLASS, object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam) =
                centroidProvider.toInt(param.args[0] as FloatArray)
        }, "toInt", FloatArray::class.java)

        lpparam.hookMethod(LAB_POINT_CLASS, object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam) =
                centroidProvider.distance(param.args[0] as FloatArray, param.args[1] as FloatArray)
        }, "distance", FloatArray::class.java, FloatArray::class.java)
    }

    companion object {
        private const val LAB_POINT_CLASS = "com.android.internal.graphics.palette.LABPointProvider"
    }
}
