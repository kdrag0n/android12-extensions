package dev.kdrag0n.android12ext.xposed

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import de.robv.android.xposed.callbacks.XC_LoadPackage
import dev.kdrag0n.android12ext.CustomApplication
import dev.kdrag0n.android12ext.core.BroadcastManager
import dev.kdrag0n.android12ext.data.hasSystemUiGoogle
import dev.kdrag0n.android12ext.xposed.hooks.*
import timber.log.Timber

private val FEATURE_FLAGS = mapOf(
    // DP2
    "isKeyguardLayoutEnabled" to "lockscreen",
    "isMonetEnabled" to "monet",
    //"isNewNotifPipelineEnabled" to "notification_shade", // crashes on DP2, does nothing on DP3
    //"isNewNotifPipelineRenderingEnabled" to "notification_shade", // breaks notifications
    "isToastStyleEnabled" to "toast",
    "useNewLockscreenAnimations" to "lockscreen",

    // DP3
    "isAlarmTileAvailable" to "global", // optional QS tile, no reason to keep disabled
    "isChargingRippleEnabled" to "charging_ripple", // only affects keyguard, so assign to lock screen
    "isQuickAccessWalletEnabled" to "global", // optional QS tile, no reason to keep disabled
    //"isTwoColumnNotificationShadeEnabled" to "notification_shade", // landscape tablets only

    // Beta 1 has no new flags and isNewNotifPipelineRenderingEnabled is still unstable.

    // Beta 2
    "isPMLiteEnabled" to "global",

    // Beta 4
    "isCombinedStatusBarSignalIconsEnabled" to "combined_signal",
)

class XposedHook(
    private val context: Context,
    private val lpparam: XC_LoadPackage.LoadPackageParam,
    private val prefs: SharedPreferences,
    private val broadcastManager: BroadcastManager,
) {
    private val sysuiHooks = SystemUIHooks(context, lpparam)
    private val settingsHooks = SettingsHooks(lpparam)
    private val frameworkHooks = FrameworkHooks(lpparam)
    private val launcherHooks = LauncherHooks(lpparam)
    private val playGamesHooks = PlayGamesHooks()
    private val themePickerHooks = ThemePickerHooks(lpparam)

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

        // Fix weather on Beta 2
        sysuiHooks.applyFeatureFlag("isSmartspaceEnabled", false)

        // Get color override, applied below
        val colorOverride = if (isFeatureEnabled("monet_custom_color", false)) {
            prefs.getInt("monet_custom_color_value", Color.BLUE)
        } else {
            null
        }

        // Custom Monet engine, forced on AOSP
        if (isFeatureEnabled("custom_monet", false) ||
            (isFeatureEnabled("monet") && !hasSystemUiGoogle)
        ) {
            sysuiHooks.applyThemeOverlayController(
                isGoogle = hasSystemUiGoogle,
                colorSchemeFactory = ColorSchemeFactory.getFactory(prefs),
                colorOverride = colorOverride,
            )
        } else if (colorOverride != null) {
            sysuiHooks.applyMonetColor(hasSystemUiGoogle, colorOverride)
        }

        // Disable Monet, if necessary
        if (!isFeatureEnabled("monet")) {
            disableMonetOverlays()
        }

        // Unlock sensor privacy toggles
        sysuiHooks.applySensorPrivacyToggles()

        // Rounded screenshots
        sysuiHooks.applyRoundedScreenshots(isFeatureEnabled("rounded_screenshots", false))
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
        launcherHooks.flagValues.apply {
            this["ENABLE_DEVICE_SEARCH"] = isFeatureEnabled("launcher_device_search")

            this["PROTOTYPE_APP_CLOSE"] = isFeatureEnabled("launcher_animations")
            this["ENABLE_SCRIM_FOR_APP_LAUNCH"] = isFeatureEnabled("launcher_animations")

            this["ENABLE_SMARTSPACE_ENHANCED"] = isFeatureEnabled("launcher_live_space", false)

            this["KEYGUARD_ANIMATION"] = isFeatureEnabled("launcher_keyguard_anim")
        }

        launcherHooks.applyFeatureFlags()
    }

    private fun applyFramework() {
        // Ripple style
        when (prefs.getString("ripple_style", null)) {
            "no_sparkles" -> frameworkHooks.applyNoSparklesRipple()
            "legacy" -> frameworkHooks.applyLegacyRipple()
            "fluent" -> frameworkHooks.applyFluentRipple()
        }

        // Unified "Internet" settings
        frameworkHooks.applyInternetFlag(isFeatureEnabled("internet_ui"))

        // Haptics mod
        if (isFeatureEnabled("haptic_touch", false)) {
            frameworkHooks.applyHapticTouch()
        }
    }

    private fun applyPlayGames() {
        playGamesHooks.applyPreviewSdk()
    }

    private fun applySettings() {
        if (context.hasSystemUiGoogle()) {
            settingsHooks.applyBatterySlots(isFeatureEnabled("settings_battery_slots"))
        }
    }

    private fun applyThemePicker() {
        if (isFeatureEnabled("custom_monet", false)) {
            themePickerHooks.applyColorScheme(
                colorSchemeFactory = ColorSchemeFactory.getFactory(prefs),
            )
        }
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
            // Settings
            "com.android.settings" -> applySettings()
            // Launcher
            "com.android.launcher3", "com.google.android.apps.nexuslauncher" -> applyLauncher()
            // Wallpaper & style
            "com.google.android.apps.wallpaper" -> applyThemePicker()
        }

        // All apps
        applyFramework()
    }
}
