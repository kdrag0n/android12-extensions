package dev.kdrag0n.xposed.android12ext

import android.util.Log
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage

class XposedHook : IXposedHookLoadPackage {
    private val featureFlagHook = object : XC_MethodReplacement() {
        override fun replaceHookedMethod(param: MethodHookParam) = true
    }

    private val gameDashHook = object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            val field = param.thisObject.javaClass.getDeclaredField("DISABLED")
            field.isAccessible = true
            field.set(null, java.lang.Boolean.FALSE)

            param.thisObject.javaClass.getDeclaredField("mShouldShow").let {
                it.isAccessible = true
                it.setBoolean(param.thisObject, true)
            }
        }
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            "com.android.systemui" -> {
                // Enable feature flags
                FEATURE_FLAGS.forEach {
                    Log.i(TAG, "Hooking feature flag: $it")
                    hookMethod(lpparam, FEATURE_FLAGS_CLASS, featureFlagHook, it)
                }

                // Enable game dashboard
                hookMethod(
                    lpparam,
                    GAME_ENTRY_CLASS,
                    gameDashHook,
                    "setButtonState",
                    Boolean::class.java,
                    Boolean::class.java
                )
            }
            "android" -> {

            }
        }
    }

    private fun hookMethod(
        lpparam: XC_LoadPackage.LoadPackageParam,
        className: String,
        hook: XC_MethodHook,
        methodName: String,
        vararg argTypes: Class<*>
    ) {
        val method = XposedHelpers.findClass(className, lpparam.classLoader)
            .getDeclaredMethod(methodName, *argTypes)
        XposedBridge.hookMethod(method, hook)
    }

    companion object {
        const val TAG = "A12Ext"
        const val FEATURE_FLAGS_CLASS = "com.android.systemui.statusbar.FeatureFlags"
        const val GAME_ENTRY_CLASS = "com.google.android.systemui.gamedashboard.EntryPointController"

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