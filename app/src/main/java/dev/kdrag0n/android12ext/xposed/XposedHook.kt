package dev.kdrag0n.android12ext.xposed

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import de.robv.android.xposed.callbacks.XC_LoadPackage
import dev.kdrag0n.android12ext.CustomApplication
import dev.kdrag0n.android12ext.core.BroadcastManager
import dev.kdrag0n.android12ext.data.hasSystemUiGoogle
import dev.kdrag0n.android12ext.monet.theme.ColorSchemeFactory
import dev.kdrag0n.android12ext.xposed.hooks.*
import timber.log.Timber

private val FEATURE_FLAGS = mapOf(
    "isMonetEnabled" to "monet",
    "isToastStyleEnabled" to "toast",
    "useNewLockscreenAnimations" to "lockscreen",
    "isKeyguardLayoutEnabled" to "lockscreen",
    "isChargingRippleEnabled" to "charging_ripple",
    "isProviderModelSettingEnabled" to "internet_ui",
    "isCombinedStatusBarSignalIconsEnabled" to "combined_signal",
)

// We're stuck with manual dependency injection here unless we bootstrap Dagger in the Xposed entry point
class XposedHook(
    private val context: Context,
    private val lpparam: XC_LoadPackage.LoadPackageParam,
    private val prefs: SharedPreferences,
    private val broadcastManager: BroadcastManager,
) {
    private val sysuiHooks = SystemUIHooks(lpparam)
    private val settingsHooks = SettingsHooks(lpparam)
    private val frameworkHooks = FrameworkHooks(lpparam)
    private val launcherHooks = LauncherHooks(lpparam)
    private val colorSchemeHooks = ColorSchemeHooks(lpparam)

    init {
        CustomApplication.commonInit()
    }

    private fun isFeatureEnabled(feature: String, default: Boolean = true): Boolean {
        return prefs.getBoolean("${feature}_enabled", default)
    }

    private fun applySysUi() {
        broadcastManager.listenForPings()

        // Enable feature flags
        FEATURE_FLAGS.forEach { (flag, prefKey) ->
            sysuiHooks.applyFeatureFlag(flag, isFeatureEnabled(prefKey))
        }

        // Dedicated network QS tiles
        if (!isFeatureEnabled("internet_ui")) {
            sysuiHooks.applyNetworkQsTiles()
        }

        // Get color override, applied below
        val colorOverride = if (isFeatureEnabled("monet_custom_color", false)) {
            prefs.getInt("monet_custom_color_value", Color.BLUE)
        } else {
            null
        }

        // Custom Monet engine
        sysuiHooks.applyThemeOverlayController(
            colorSchemeFactory = if (isFeatureEnabled("custom_monet")) {
                ColorSchemeFactory.getFactory(prefs)
            } else null,
            colorOverride = colorOverride,
        )

        // Disable Monet, if necessary
        if (!isFeatureEnabled("monet")) {
            disableMonetOverlays()
        }

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
            this["ENABLE_SMARTSPACE_ENHANCED"] = isFeatureEnabled("launcher_live_space")
            this["KEYGUARD_ANIMATION"] = isFeatureEnabled("launcher_keyguard_anim")
            this["ENABLE_LOCAL_COLOR_POPUPS"] = isFeatureEnabled("launcher_local_color_popups")
        }
        launcherHooks.applyFeatureFlags()

        // Obfuscated
        applyColorScheme(shadesOfName = "a")
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

    private fun applySettings() {
        if (context.hasSystemUiGoogle()) {
            settingsHooks.applyBatterySlots(isFeatureEnabled("settings_battery_slots"))
        }
    }

    private fun applyColorScheme(shadesOfName: String) {
        if (isFeatureEnabled("custom_monet")) {
            colorSchemeHooks.applyColorScheme(
                colorSchemeFactory = ColorSchemeFactory.getFactory(prefs),
                shadesOfName = shadesOfName,
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
            // Settings
            "com.android.settings" -> applySettings()
            // Launcher
            "com.android.launcher3", "com.google.android.apps.nexuslauncher" -> applyLauncher()
            // Wallpaper & style
            "com.google.android.apps.wallpaper" -> applyColorScheme(shadesOfName = "of")
        }

        // All apps
        applyFramework()
    }
}
