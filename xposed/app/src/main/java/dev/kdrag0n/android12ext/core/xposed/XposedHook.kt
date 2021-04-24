package dev.kdrag0n.android12ext.core.xposed

import android.app.Instrumentation
import android.content.*
import com.crossbowffs.remotepreferences.RemotePreferences
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage
import dev.kdrag0n.android12ext.BuildConfig
import dev.kdrag0n.android12ext.CustomApplication
import dev.kdrag0n.android12ext.core.BroadcastManager
import dev.kdrag0n.android12ext.core.xposed.hooks.FrameworkHooks
import dev.kdrag0n.android12ext.core.xposed.hooks.SystemUIHooks
import kotlin.system.exitProcess

private val FEATURE_FLAGS = mapOf(
    // DP2
    "isKeyguardLayoutEnabled" to "lockscreen",
    "isMonetEnabled" to "monet",
    //"isNewNotifPipelineEnabled" to "notification_shade", // crashes on DP2, does nothing on DP3
    //"isNewNotifPipelineRenderingEnabled" to "notification_shade", // breaks notifications
    "isShadeOpaque" to "notification_shade",
    "isToastStyleEnabled" to "toast",
    "useNewBrightnessSlider" to "notification_shade",
    "useNewLockscreenAnimations" to "lockscreen",

    // DP3
    "isQSLabelsEnabled" to "quick_settings", // crashes on DP2
    "isAlarmTileAvailable" to "global", // optional QS tile, no reason to keep disabled
    "isChargingRippleEnabled" to "charging_ripple", // only affects keyguard, so assign to lock screen
    "isNavigationBarOverlayEnabled" to "global", // for game dashboard, does nothing otherwise
    "isPMLiteEnabled" to "quick_settings", // doesn't work
    "isQuickAccessWalletEnabled" to "global", // optional QS tile, no reason to keep disabled
    //"isTwoColumnNotificationShadeEnabled" to "notification_shade", // landscape tablets only
)

class XposedHook : IXposedHookLoadPackage {
    private lateinit var prefs: SharedPreferences
    private lateinit var context: Context
    private lateinit var broadcastManager: BroadcastManager

    init {
        CustomApplication.commonInit()
    }

    private fun isFeatureEnabled(feature: String, default: Boolean = true): Boolean {
        return prefs.getBoolean("${feature}_enabled", default)
    }

    private fun applySysUi(lpparam: XC_LoadPackage.LoadPackageParam) {
        broadcastManager.listenForPings()

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
            // Always register broadcast receiver in System UI
            if (lpparam.packageName == "com.android.systemui") {
                broadcastManager.listenForPings()
            }

            return
        }

        when (lpparam.packageName) {
            // Never hook our own app in case something goes wrong
            BuildConfig.APPLICATION_ID -> return
            // System UI
            "com.android.systemui" -> applySysUi(lpparam)
        }

        // All apps
        if (isFeatureEnabled("patterned_ripple")) {
            FrameworkHooks.applyRipple(lpparam)
        }

        if (isFeatureEnabled("haptic_touch", false)) {
            FrameworkHooks.applyHapticTouch(lpparam)
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
                broadcastManager = BroadcastManager(context)
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
                    IntentFilter(BroadcastManager.RELOAD_ACTION),
                    BroadcastManager.MANAGER_PERMISSION,
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