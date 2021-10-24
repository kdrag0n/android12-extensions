package dev.kdrag0n.android12ext.xposed.hooks

import android.annotation.SuppressLint
import android.app.WallpaperColors
import android.content.Context
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import dev.kdrag0n.android12ext.monet.overlay.ThemeOverlayController
import dev.kdrag0n.android12ext.monet.theme.ColorSchemeFactory
import dev.kdrag0n.android12ext.utils.getClass
import dev.kdrag0n.android12ext.utils.setBool

@SuppressLint("PrivateApi")
class SystemUIHooks(
    private val context: Context,
    lpparam: XC_LoadPackage.LoadPackageParam,
) : BaseHooks(lpparam) {
    fun applyFeatureFlag(flag: String, enabled: Boolean) {
        hookReturn(FEATURE_FLAGS_CLASS, flag, enabled)
    }

    fun applyRoundedScreenshots(enabled: Boolean) {
        val clazz = lpparam.getClass("com.android.systemui.ScreenDecorations")
        clazz.setBool("DEBUG_SCREENSHOT_ROUNDED_CORNERS", enabled)
        clazz.setBool("DEBUG_COLOR", false)
    }

    fun applyMonetColor(
        isGoogle: Boolean,
        colorOverride: Int,
    ) {
        val clazz = if (isGoogle) THEME_CLASS_GOOGLE else THEME_CLASS_AOSP
        hookBefore(clazz, "getOverlay") {
            args[0] = colorOverride
        }
    }

    fun applyThemeOverlayController(
        isGoogle: Boolean,
        colorSchemeFactory: ColorSchemeFactory,
        colorOverride: Int?,
    ) {
        val controller = ThemeOverlayController(context, colorSchemeFactory)
        val clazz = if (isGoogle) THEME_CLASS_GOOGLE else THEME_CLASS_AOSP

        hookReplace(clazz, "getOverlay") {
            controller.getOverlay(args[0] as Int, args[1] as Int)
        }

        hookReplace<WallpaperColors>("getNeutralColor") {
            colorOverride
                ?: controller.getNeutralColor(args[0] as WallpaperColors)
        }

        hookReplace<WallpaperColors>("getAccentColor") {
            colorOverride
                ?: controller.getAccentColor(args[0] as WallpaperColors)
        }
    }

    companion object {
        private const val FEATURE_FLAGS_CLASS = "com.android.systemui.statusbar.FeatureFlags"

        private const val THEME_CLASS_AOSP = "com.android.systemui.theme.ThemeOverlayController"
        private const val THEME_CLASS_GOOGLE = "com.google.android.systemui.theme.ThemeOverlayControllerGoogle"
    }
}
