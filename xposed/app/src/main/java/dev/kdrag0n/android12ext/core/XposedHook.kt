package dev.kdrag0n.android12ext.core

import android.app.Instrumentation
import android.content.*
import android.content.res.Resources
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.Log
import com.crossbowffs.remotepreferences.RemotePreferences
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage
import dev.kdrag0n.android12ext.BuildConfig
import kotlin.system.exitProcess

private const val TAG = "A12Ext"

private const val FEATURE_FLAGS_CLASS = "com.android.systemui.statusbar.FeatureFlags"
private const val GAME_ENTRY_CLASS = "com.google.android.systemui.gamedashboard.EntryPointController"
private const val RIPPLE_CLASS = "android.graphics.drawable.RippleDrawable"
private const val RIPPLE_STATE_CLASS = "android.graphics.drawable.RippleDrawable\$RippleState"
private const val EDGE_CLASS = "android.widget.EdgeEffect"

private val FEATURE_FLAGS = mapOf(
    "isKeyguardLayoutEnabled" to "lockscreen",
    "isMonetEnabled" to "monet",
    //"isNewNotifPipelineRenderingEnabled" to "notification_shade", // breaks notifications
    "isPeopleTileEnabled" to "people",
    //"isQSLabelsEnabled" to "notification_shade", // causes crash
    "isShadeOpaque" to "notification_shade",
    "isToastStyleEnabled" to "toast",
    "useNewBrightnessSlider" to "notification_shade",
    "useNewLockscreenAnimations" to "lockscreen",
)

class XposedHook : IXposedHookLoadPackage {
    private lateinit var prefs: SharedPreferences

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

    private val roundedScreenshotHook = object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            param.thisObject.javaClass.getDeclaredField("DEBUG_COLOR").let {
                it.isAccessible = true
                it.setBoolean(null, false)
            }
        }
    }

    private val privacyHook = object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            XposedHelpers.setBooleanField(param.thisObject, "allIndicatorsAvailable", true)
            XposedHelpers.setBooleanField(param.thisObject, "micCameraAvailable", true)
            XposedHelpers.setBooleanField(param.thisObject, "locationAvailable", true)
        }
    }

    private val edgeEffectHook = object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            XposedHelpers.setIntField(param.thisObject, "mEdgeEffectType", 1)
        }
    }

    private fun isFeatureEnabled(feature: String): Boolean {
        return prefs.getBoolean("${feature}_enabled", true)
    }

    private fun hookPrivacyIndicators(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedHelpers.findAndHookConstructor(
            "com.android.systemui.privacy.PrivacyItemController",
            lpparam.classLoader,
            XposedHelpers.findClass("com.android.systemui.appops.AppOpsController", lpparam.classLoader),
            XposedHelpers.findClass("com.android.systemui.util.concurrency.DelayableExecutor", lpparam.classLoader),
            XposedHelpers.findClass("com.android.systemui.util.concurrency.DelayableExecutor", lpparam.classLoader),
            XposedHelpers.findClass("com.android.systemui.util.DeviceConfigProxy", lpparam.classLoader),
            XposedHelpers.findClass("com.android.systemui.settings.UserTracker", lpparam.classLoader),
            XposedHelpers.findClass("com.android.systemui.privacy.logging.PrivacyLogger", lpparam.classLoader),
            XposedHelpers.findClass("com.android.systemui.dump.DumpManager", lpparam.classLoader),
            privacyHook,
        )
    }

    private fun hookSysui(lpparam: XC_LoadPackage.LoadPackageParam) {
        // Enable feature flags
        FEATURE_FLAGS.forEach { (method, prefKey) ->
            if (isFeatureEnabled(prefKey)) {
                Log.i(TAG, "Hooking feature flag: $method")
                hookMethod(lpparam, FEATURE_FLAGS_CLASS, featureFlagHook, method)
            }
        }

        // Enable privacy indicators
        if (isFeatureEnabled("privacy_indicators")) {
            hookPrivacyIndicators(lpparam)
        }

        // Enable game dashboard
        if (isFeatureEnabled("game_dashboard")) {
            hookMethod(
                lpparam,
                GAME_ENTRY_CLASS,
                gameDashHook,
                "setButtonState",
                Boolean::class.java,
                Boolean::class.java
            )
        }

        // Hide red background in rounded screenshots
        hookMethod(
            lpparam,
            "com.android.systemui.ScreenDecorations",
            roundedScreenshotHook,
            "updateColorInversion",
            Int::class.java
        )
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

    private fun initHooks(lpparam: XC_LoadPackage.LoadPackageParam) {
        // Global kill-switch
        if (!isFeatureEnabled("global")) {
            return
        }

        if (lpparam.packageName == "com.android.systemui") {
            hookSysui(lpparam)
        }

        // Never hook our own app in case something goes wrong
        if (lpparam.packageName == BuildConfig.APPLICATION_ID) {
            return
        }

        if (isFeatureEnabled("patterned_ripple")) {
            hookRipple(lpparam)
        }

        if (isFeatureEnabled("overscroll_bounce")) {
            hookEdge(lpparam)
        }
    }

    private val reloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            exitProcess(0)
        }
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        val contextHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                // Make sure we don't initialize twice
                if (::prefs.isInitialized) {
                    return
                }

                val context = param.result as Context
                prefs = RemotePreferences(
                    context,
                    XposedPreferenceProvider.AUTHORITY,
                    XposedPreferenceProvider.DEFAULT_PREFS,
                    true
                )

                initHooks(lpparam)

                context.registerReceiver(
                    reloadReceiver,
                    IntentFilter(RELOAD_BROADCAST_ACTION),
                    BROADCAST_PERMISSION,
                    null
                )
            }
        }

        // Wait to get a Context reference before initializing other hooks
        XposedBridge.hookAllMethods(
            Instrumentation::class.java,
            "newApplication",
            contextHook,
        )
    }
}