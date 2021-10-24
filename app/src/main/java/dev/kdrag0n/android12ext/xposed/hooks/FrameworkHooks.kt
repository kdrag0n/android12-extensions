package dev.kdrag0n.android12ext.xposed.hooks

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.drawable.RippleDrawable
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.animation.addListener
import de.robv.android.xposed.callbacks.XC_LoadPackage
import dev.kdrag0n.android12ext.utils.call
import dev.kdrag0n.android12ext.utils.get
import dev.kdrag0n.android12ext.utils.set
import dev.kdrag0n.android12ext.utils.setInt
import dev.kdrag0n.android12ext.xposed.deoptimizeMethod
import dev.kdrag0n.android12ext.xposed.ripple.RIPPLE_SHADER_FLUENT
import dev.kdrag0n.android12ext.xposed.ripple.RIPPLE_SHADER_NO_SPARKLES
import timber.log.Timber
import java.util.function.Consumer

@SuppressLint("PrivateApi")
class FrameworkHooks(
    lpparam: XC_LoadPackage.LoadPackageParam,
) : BaseHooks(lpparam) {
    private fun applyRippleShader(shader: String) {
        // De-optimize the RippleShader constructor first. Otherwise, both superclass constructors
        // get inlined and the hook doesn't work.
        // More info: https://github.com/LSPosed/LSPosed/issues/1123
        try {
            deoptimizeMethod(
                Class.forName("android.graphics.drawable.RippleShader")
                    .getDeclaredConstructor()
            )

            // In some cases, we also need to de-optimize createAnimationProperties or the entire
            // constructor gets inlined.
            val createAnimationProperties = Class.forName("android.graphics.drawable.RippleDrawable")
                .getDeclaredMethod(
                    "createAnimationProperties",
                    Float::class.java,
                    Float::class.java,
                    Float::class.java,
                    Float::class.java,
                    Float::class.java,
                    Float::class.java,
                )
            deoptimizeMethod(createAnimationProperties)
        } catch (e: NoSuchMethodException) {
            // Older versions of LSPosed don't have this
            Timber.e(e, "Failed to de-optimize RippleShader constructor")
        }

        // Now hook the super constructor that accepts the shader as an argument
        hookBeforeCons(
            "android.graphics.RuntimeShader",
            String::class.java,
            Boolean::class.java,
        ) {
            // Only hijack RippleShader super calls
            if (thisObject::class.java.name != "android.graphics.drawable.RippleShader") {
                return@hookBeforeCons
            }

            args[0] = shader
        }
    }

    fun applyNoSparklesRipple() {
        applyRippleShader(RIPPLE_SHADER_NO_SPARKLES)
    }

    fun applyLegacyRipple() {
        hookAfterCons(
            "android.graphics.drawable.RippleDrawable\$RippleState",
            Class.forName("android.graphics.drawable.LayerDrawable\$LayerState"),
            RippleDrawable::class.java,
            Resources::class.java,
        ) {
            thisObject.setInt("mRippleStyle", 0)
        }
    }

    @Suppress("PrivateApi", "unchecked_cast")
    fun applyFluentRipple() {
        // Replace ripple shader
        applyRippleShader(RIPPLE_SHADER_FLUENT)

        // Reduce duration of enter animation
        // Change expand curve to linear
        val linearInterpolator = LinearInterpolator()
        hookReplace("android.graphics.drawable.RippleAnimationSession", "startAnimation") {
            val expand = args[0] as Animator
            expand.duration = 350
            expand.addListener(onEnd = {
                thisObject.onAnimationEnd(it)
            })
            expand.interpolator = linearInterpolator
            expand.start()

            val loop = args[1] as Animator
            loop.duration = 7000
            loop.addListener(onEnd = {
                thisObject.onAnimationEnd(it)
                thisObject.set("mLoopAnimation", null)
            })
            loop.interpolator = linearInterpolator
            loop.start()

            val mLoopAnimation = thisObject.get<Animator?>("mLoopAnimation")
            mLoopAnimation?.cancel()
            thisObject.set("mLoopAnimation", loop)
        }

        // Remove start delay and increase duration to compensate
        hookReplace("android.graphics.drawable.RippleAnimationSession", "exitHardware") {
            val canvas = args[0]

            val canvasProperties = thisObject.call("getCanvasProperties")!!
            val progressProp = canvasProperties.call("getProgress")
            val exit = Class.forName("android.graphics.animation.RenderNodeAnimator")
                .getDeclaredConstructor(Class.forName("android.graphics.CanvasProperty"), Float::class.java)
                .newInstance(progressProp, 1.0f) as Animator

            exit.duration = 450
            exit.addListener(onEnd = {
                thisObject.onAnimationEnd(it)
                val onEnd = thisObject.get<Consumer<Any?>?>("mOnSessionEnd")
                onEnd?.accept(thisObject)
            })

            exit.call("setTarget", canvas)
            exit.interpolator = linearInterpolator
            exit.start()
        }
    }

    fun applyHapticTouch() {
        hookAfter<View>("performButtonActionOnTouchDown") {
            val view = thisObject as View
            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
        }
    }

    fun applyInternetFlag(enabled: Boolean) {
        hookAfter("android.util.FeatureFlagUtils", "isEnabled") {
            if (args[1] as String == "settings_provider_model") {
                result = enabled
            }
        }
    }

    companion object {
        private fun Any.onAnimationEnd(animator: Animator) {
            val method = this::class.java.getDeclaredMethod("onAnimationEnd", Animator::class.java)
            method.isAccessible = true
            method.invoke(this, animator)
        }
    }
}
