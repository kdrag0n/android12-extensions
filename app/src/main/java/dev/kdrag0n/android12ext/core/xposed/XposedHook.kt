package dev.kdrag0n.android12ext.core.xposed

import android.content.Context
import android.content.SharedPreferences
import de.robv.android.xposed.callbacks.XC_LoadPackage
import dev.kdrag0n.android12ext.BuildConfig
import dev.kdrag0n.android12ext.CustomApplication
import dev.kdrag0n.android12ext.core.BroadcastManager
import dev.kdrag0n.android12ext.core.data.hasSystemUiGoogle
import dev.kdrag0n.android12ext.core.xposed.hooks.FrameworkHooks
import dev.kdrag0n.android12ext.core.xposed.hooks.LauncherHooks
import dev.kdrag0n.android12ext.core.xposed.hooks.PlayGamesHooks
import dev.kdrag0n.android12ext.core.xposed.hooks.SystemUIHooks
import timber.log.Timber

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

    // Beta 1 has no new flags and isNewNotifPipelineRenderingEnabled is still unstable.
)

class XposedHook(
    private val context: Context,
    private val lpparam: XC_LoadPackage.LoadPackageParam,
    private val prefs: SharedPreferences,
    private val broadcastManager: BroadcastManager,
) {
    private val sysuiHooks = SystemUIHooks(context, lpparam)
    private val frameworkHooks = FrameworkHooks(lpparam)
    private val launcherHooks = LauncherHooks(lpparam)
    private val playGamesHooks = PlayGamesHooks()

    init {
        CustomApplication.commonInit()
    }

    private fun isFeatureEnabled(feature: String, default: Boolean = true): Boolean {
        return prefs.getBoolean("${feature}_enabled", default)
    }

    private fun applySysUi() {
        broadcastManager.listenForPings()
        val hasSystemUiGoogle = context.hasSystemUiGoogle()

        // Enable feature flags
        FEATURE_FLAGS.forEach { (flag, prefKey) ->
            sysuiHooks.applyFeatureFlag(flag, isFeatureEnabled(prefKey))
        }

        // Enable privacy indicators
        sysuiHooks.applyPrivacyIndicators(isFeatureEnabled("privacy_indicators"))

        // Enable game dashboard
        if (hasSystemUiGoogle) {
            sysuiHooks.applyGameDashboard(isFeatureEnabled("game_dashboard"))
        }

        // Custom Monet engine, forced on AOSP
        if (isFeatureEnabled("custom_monet", false) ||
            (isFeatureEnabled("monet") && !hasSystemUiGoogle)
        ) {
            frameworkHooks.applyQuantizerColorspace()

            sysuiHooks.applyThemeOverlayController(
                hasSystemUiGoogle,
                prefs.getInt("custom_monet_chroma_multiplier", 50).toDouble() / 50,
                false,
            )
        }

        // Disable Monet, if necessary
        if (!isFeatureEnabled("monet")) {
            disableMonetOverlays()
        }

        // Unlock sensor privacy toggles
        sysuiHooks.applySensorPrivacyToggles()

        // Hide red background in rounded screenshots
        sysuiHooks.applyRoundedScreenshotBg()

        // Toggle GX overlay
        context.setOverlayEnabled(lpparam, "com.google.android.systemui.gxoverlay", isFeatureEnabled("gxoverlay"))

        // Toggle AOSP circle icons overlay
        if (!hasSystemUiGoogle) {
            context.setOverlayEnabled(lpparam, "com.android.theme.icon.circle", isFeatureEnabled("aosp_circle_icons"))
        }
    }

    private fun disableMonetOverlays() {
        try {
            context.setOverlayEnabled(lpparam, "com.android.systemui:accent", false)
            context.setOverlayEnabled(lpparam, "com.android.systemui:neutral", false)
        } catch (e: Exception) {
            Timber.e(e, "Failed to disable Monet overlays")
        }
    }

    private fun applyLauncher() {
        launcherHooks.applyFeatureFlags()
    }

    private fun applySystemServer() {
        frameworkHooks.applyMedianCutQuantizer()
    }

    private fun applyFramework() {
        frameworkHooks.applyRippleStyle(isFeatureEnabled("patterned_ripple"))

        // Unified "Internet" settings
        frameworkHooks.applyInternetFlag(isFeatureEnabled("internet_ui"))

        // Haptics mod
        if (isFeatureEnabled("haptic_touch", false)) {
            frameworkHooks.applyHapticTouch()
        }

        // Custom ripple mod
        frameworkHooks.applyCustomRipple()
    }

    private fun applyPlayGames() {
        playGamesHooks.applyPreviewSdk()
    }

    fun applyAll() {
        // Global kill-switch
        if (!isFeatureEnabled("global")) {
            // Always register broadcast receiver in System UI
            if (lpparam.packageName == "com.android.systemui") {
                broadcastManager.listenForPings()
            }

            return
        }

        when (lpparam.packageName) {
            // System UI
            "com.android.systemui" -> applySysUi()
            // Play Games
            "com.google.android.play.games" -> applyPlayGames()
            // Launcher
            "com.android.launcher3", "com.google.android.apps.nexuslauncher" -> applyLauncher()
        }

        // All apps
        applyFramework()
    }
}
