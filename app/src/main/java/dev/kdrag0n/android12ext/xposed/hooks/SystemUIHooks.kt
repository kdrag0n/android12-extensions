package dev.kdrag0n.android12ext.xposed.hooks

import android.annotation.SuppressLint
import de.robv.android.xposed.callbacks.XC_LoadPackage
import dev.kdrag0n.android12ext.monet.overlay.ThemeOverlayController
import dev.kdrag0n.android12ext.monet.theme.ColorSchemeFactory
import dev.kdrag0n.android12ext.utils.*
import timber.log.Timber

@SuppressLint("PrivateApi")
class SystemUIHooks(
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

    fun applyThemeOverlayController(
        colorSchemeFactory: ColorSchemeFactory?,
        colorOverride: Int?,
    ) = lpparam.getClass(THEME_CLASS_AOSP).declaredConstructors.forEach { cons ->
        // Defer hook until we know which class is the active implementation of ThemeOverlayController
        hookBefore(cons) {
            // Hook both the AOSP and active classes in case the method hasn't been overridden
            val activeClass = thisObject::class.java.name
            Timber.d("ThemeOverlayController class = $activeClass")

            setOf(THEME_CLASS_AOSP, activeClass).forEach { clazz ->
                if (colorSchemeFactory != null) {
                    val controller = ThemeOverlayController(colorSchemeFactory)
                    hookReplace(clazz, "getOverlay") {
                        controller.getOverlay(colorOverride ?: args[0] as Int, args[1] as Int)
                    }
                } else if (colorOverride != null) {
                    hookBefore(clazz, "getOverlay") {
                        args[0] = colorOverride
                    }
                }
            }
        }
    }

    companion object {
        private const val FEATURE_FLAGS_CLASS = "com.android.systemui.statusbar.FeatureFlags"
        private const val THEME_CLASS_AOSP = "com.android.systemui.theme.ThemeOverlayController"
    }
}
