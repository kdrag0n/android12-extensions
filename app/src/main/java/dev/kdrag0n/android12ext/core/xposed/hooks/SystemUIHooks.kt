package dev.kdrag0n.android12ext.core.xposed.hooks

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import dev.kdrag0n.android12ext.monet.overlay.ThemeOverlayController
import dev.kdrag0n.android12ext.monet.theme.TargetColors
import dev.kdrag0n.android12ext.core.xposed.hookMethod
import timber.log.Timber

class SystemUIHooks(
    private val lpparam: XC_LoadPackage.LoadPackageParam,
) {
    fun applyFeatureFlag(flag: String, enabled: Boolean) {
        val hook = object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam) = enabled
        }

        try {
            lpparam.hookMethod(FEATURE_FLAGS_CLASS, hook, flag)
        } catch (e: NoSuchMethodException) {
            Timber.w("Feature flag does not exist: $flag")
        }
    }

    fun applyGameDashboard(enabled: Boolean) {
        val hook = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                XposedHelpers.setBooleanField(param.thisObject, "mShouldShow", enabled)
            }
        }

        lpparam.hookMethod(
                GAME_ENTRY_CLASS,
                hook,
                "setButtonState",
                Boolean::class.java,
                Boolean::class.java
        )
    }

    fun applyRoundedScreenshotBg() {
        val hook = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                param.thisObject.javaClass.getDeclaredField("DEBUG_COLOR").let {
                    it.isAccessible = true
                    it.setBoolean(null, false)
                }
            }
        }

        lpparam.hookMethod(
                "com.android.systemui.ScreenDecorations",
                hook,
                "updateColorInversion",
                Int::class.java
        )
    }

    fun applyPrivacyIndicators(enabled: Boolean) {
        val constructorHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                XposedHelpers.setBooleanField(param.thisObject, "micCameraAvailable", enabled)
                XposedHelpers.setBooleanField(param.thisObject, "locationAvailable", enabled)
            }
        }

        XposedHelpers.findAndHookConstructor(
                PRIVACY_CLASS,
                lpparam.classLoader,
                XposedHelpers.findClass("com.android.systemui.appops.AppOpsController", lpparam.classLoader),
                XposedHelpers.findClass("com.android.systemui.util.concurrency.DelayableExecutor", lpparam.classLoader),
                XposedHelpers.findClass("com.android.systemui.util.concurrency.DelayableExecutor", lpparam.classLoader),
                XposedHelpers.findClass("com.android.systemui.util.DeviceConfigProxy", lpparam.classLoader),
                XposedHelpers.findClass("com.android.systemui.settings.UserTracker", lpparam.classLoader),
                XposedHelpers.findClass("com.android.systemui.privacy.logging.PrivacyLogger", lpparam.classLoader),
                XposedHelpers.findClass("com.android.systemui.util.time.SystemClock", lpparam.classLoader),
                XposedHelpers.findClass("com.android.systemui.dump.DumpManager", lpparam.classLoader),
                constructorHook,
        )

        val flagHook = object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam) = true
        }

        lpparam.hookMethod(
                "com.android.systemui.qs.QuickStatusBarHeaderController",
                flagHook,
                "getChipEnabled",
        )
    }

    fun applyThemeOverlayController() {
        val controller = ThemeOverlayController(TargetColors.Default)
        val hook = object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam): Any {
                return controller.getOverlay(param.args[0] as Int, param.args[1] as Int)
            }
        }

        lpparam.hookMethod(
                "com.google.android.systemui.theme.ThemeOverlayControllerGoogle",
                hook,
                "getOverlay",
                Int::class.java,
                Int::class.java,
        )
    }

    fun applySensorPrivacyToggles() {
        val hook = object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam) = true
        }

        lpparam.hookMethod(
                "com.android.systemui.qs.tiles.MicrophoneToggleTile",
                hook,
                "isAvailable",
        )
        lpparam.hookMethod(
                "com.android.systemui.qs.tiles.CameraToggleTile",
                hook,
                "isAvailable",
        )
    }

    companion object {
        private const val FEATURE_FLAGS_CLASS = "com.android.systemui.statusbar.FeatureFlags"
        private const val GAME_ENTRY_CLASS = "com.google.android.systemui.gamedashboard.EntryPointController"
        private const val PRIVACY_CLASS = "com.android.systemui.privacy.PrivacyItemController"
    }
}
