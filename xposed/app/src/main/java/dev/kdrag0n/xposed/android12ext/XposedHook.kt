package dev.kdrag0n.xposed.android12ext

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.Log
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage

class XposedHook : IXposedHookLoadPackage {
    private val featureFlagHook = object : XC_MethodReplacement() {
        override fun replaceHookedMethod(param: MethodHookParam) = true
    }

    private val gameDashHook = object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            param.thisObject.javaClass.getDeclaredField("DISABLED").let {
                it.isAccessible = true
                it.set(null, java.lang.Boolean.FALSE)
            }

            XposedHelpers.setBooleanField(param.thisObject, "mShouldShow", true)
        }
    }

    private val rippleHook = object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            XposedHelpers.getObjectField(param.thisObject, "mState").let { state ->
                XposedHelpers.setIntField(state, "mRippleStyle", 1)
            }
        }
    }

    private val edgeEffectHook = object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            XposedHelpers.setIntField(param.thisObject, "mEdgeEffectType", 1)
        }
    }

    private fun hookSysui(lpparam: XC_LoadPackage.LoadPackageParam) {
        // Enable feature flags
        FEATURE_FLAGS.forEach {
            Log.i(TAG, "Hooking feature flag: $it")
            hookMethod(lpparam, FEATURE_FLAGS_CLASS, featureFlagHook, it)
        }

        // Enable game dashboard
        hookMethod(lpparam, GAME_ENTRY_CLASS, gameDashHook, "setButtonState", Boolean::class.java, Boolean::class.java)
    }

    private fun hookRipple(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedHelpers.findAndHookConstructor(
            RIPPLE_CLASS,
            lpparam.classLoader,
            XposedHelpers.findClass(RIPPLE_STATE_CLASS, lpparam.classLoader),
            Resources::class.java,
            rippleHook,
        )

        hookMethod(lpparam, RIPPLE_CLASS, rippleHook, "updateStateFromTypedArray", TypedArray::class.java)
        hookMethod(lpparam, RIPPLE_CLASS, rippleHook, "setRippleStyle", Int::class.java)
    }

    private fun hookEdge(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedHelpers.findAndHookConstructor(
            EDGE_CLASS,
            lpparam.classLoader,
            Context::class.java,
            AttributeSet::class.java,
            edgeEffectHook,
        )

        hookMethod(lpparam, EDGE_CLASS, edgeEffectHook, "setType", Int::class.java)
    }

    private fun hookMethod(
        lpparam: XC_LoadPackage.LoadPackageParam,
        className: String,
        hook: XC_MethodHook,
        methodName: String,
        vararg argTypes: Class<*>
    ) {
        XposedHelpers.findClass(className, lpparam.classLoader)
                .getDeclaredMethod(methodName, *argTypes)
                .let { method ->
                    XposedBridge.hookMethod(method, hook)
                }
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "com.android.systemui") {
            hookSysui(lpparam)
        }

        hookRipple(lpparam)
        hookEdge(lpparam)
    }

    companion object {
        const val TAG = "A12Ext"

        const val FEATURE_FLAGS_CLASS = "com.android.systemui.statusbar.FeatureFlags"
        const val GAME_ENTRY_CLASS = "com.google.android.systemui.gamedashboard.EntryPointController"
        const val RIPPLE_CLASS = "android.graphics.drawable.RippleDrawable"
        const val RIPPLE_STATE_CLASS = "android.graphics.drawable.RippleDrawable\$RippleState"
        const val EDGE_CLASS = "android.widget.EdgeEffect"

        val FEATURE_FLAGS = listOf(
            "isKeyguardLayoutEnabled",
            "isMonetEnabled",
            //"isNewNotifPipelineRenderingEnabled", // breaks notifications
            "isPeopleTileEnabled",
            //"isQSLabelsEnabled", // causes crash
            "isShadeOpaque",
            "isToastStyleEnabled",
            "useNewBrightnessSlider",
            "useNewLockscreenAnimations",
        )
    }
}