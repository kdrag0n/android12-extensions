package dev.kdrag0n.android12ext.core.xposed.hooks

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import dev.kdrag0n.android12ext.core.monet.overlay.ThemeOverlayController
import dev.kdrag0n.android12ext.core.monet.theme.ReferenceColors
import dev.kdrag0n.android12ext.core.xposed.hookMethod
import timber.log.Timber

class SystemUIHooks(
    private val lpparam: XC_LoadPackage.LoadPackageParam,
) {
    fun applyFeatureFlag(flag: String) {
        val hook = object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam) = true
        }

        try {
            lpparam.hookMethod(FEATURE_FLAGS_CLASS, hook, flag)
        } catch (e: NoSuchMethodException) {
            Timber.w("Feature flag does not exist: $flag")
        }
    }

    fun applyGameDashboard() {
        val hook = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                param.thisObject.javaClass.getDeclaredField("DISABLED").let {
                    it.isAccessible = true
                    it.set(null, java.lang.Boolean.FALSE)
                }

                XposedHelpers.setBooleanField(param.thisObject, "mShouldShow", true)
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

    fun applyPrivacyIndicators() {
        val hook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                XposedHelpers.setBooleanField(param.thisObject, "micCameraAvailable", true)
                XposedHelpers.setBooleanField(param.thisObject, "locationAvailable", true)
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
                XposedHelpers.findClass("com.android.systemui.dump.DumpManager", lpparam.classLoader),
                hook,
        )
    }

    fun applyThemeOverlayController() {
        val controller = ThemeOverlayController(ReferenceColors.Beta1.MonetGreen)
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

    companion object {
        private const val FEATURE_FLAGS_CLASS = "com.android.systemui.statusbar.FeatureFlags"
        private const val GAME_ENTRY_CLASS = "com.google.android.systemui.gamedashboard.EntryPointController"
        private const val PRIVACY_CLASS = "com.android.systemui.privacy.PrivacyItemController"
    }
}