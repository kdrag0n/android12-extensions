package dev.kdrag0n.android12ext.core.xposed

import android.app.Instrumentation
import android.content.*
import com.crossbowffs.remotepreferences.RemotePreferences
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage
import dev.kdrag0n.android12ext.BuildConfig
import dev.kdrag0n.android12ext.CustomApplication
import dev.kdrag0n.android12ext.core.Broadcasts
import dev.kdrag0n.android12ext.core.xposed.hooks.FrameworkHooks
import dev.kdrag0n.android12ext.core.xposed.hooks.SystemUIHooks
import kotlin.system.exitProcess

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
    private lateinit var context: Context

    init {
        CustomApplication.commonInit()
    }

    private fun isFeatureEnabled(feature: String): Boolean {
        return prefs.getBoolean("${feature}_enabled", true)
    }

    private fun applySysUi(lpparam: XC_LoadPackage.LoadPackageParam) {
        Broadcasts.listenForPings(context)

        // Enable feature flags
        FEATURE_FLAGS.forEach { (flag, prefKey) ->
            if (isFeatureEnabled(prefKey)) {
                SystemUIHooks.applyFeatureFlag(lpparam, flag)
            }
        }

        // Enable privacy indicators
        if (isFeatureEnabled("privacy_indicators")) {
            SystemUIHooks.applyPrivacyIndicators(lpparam)
        }

        // Enable game dashboard
        if (isFeatureEnabled("game_dashboard")) {
            SystemUIHooks.applyGameDashboard(lpparam)
        }

        // Hide red background in rounded screenshots
        SystemUIHooks.applyRoundedScreenshotBg(lpparam)
    }

    private fun applyAll(lpparam: XC_LoadPackage.LoadPackageParam) {
        // Global kill-switch
        if (!isFeatureEnabled("global")) {
            return
        }

        if (lpparam.packageName == "com.android.systemui") {
            applySysUi(lpparam)
        }

        // Never hook our own app in case something goes wrong
        if (lpparam.packageName == BuildConfig.APPLICATION_ID) {
            return
        }

        if (isFeatureEnabled("patterned_ripple")) {
            FrameworkHooks.applyRipple(lpparam)
        }

        if (isFeatureEnabled("overscroll_bounce")) {
            FrameworkHooks.applyEdge(lpparam)
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

                context = param.result as Context
                prefs = RemotePreferences(
                    context,
                    XposedPreferenceProvider.AUTHORITY,
                    XposedPreferenceProvider.DEFAULT_PREFS,
                    true
                )

                applyAll(lpparam)

                // Only listen for reload requests after loading
                context.registerReceiver(
                    reloadReceiver,
                    IntentFilter(Broadcasts.RELOAD_ACTION),
                    Broadcasts.MANAGER_PERMISSION,
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