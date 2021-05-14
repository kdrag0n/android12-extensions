package dev.kdrag0n.android12ext.core.xposed.hooks

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import dev.kdrag0n.android12ext.core.monet.overlay.ThemeOverlayController
import dev.kdrag0n.android12ext.core.monet.theme.ReferenceColors
import dev.kdrag0n.android12ext.core.xposed.hookMethod
import timber.log.Timber

object SystemUIHooks {
    private const val FEATURE_FLAGS_CLASS = "com.android.systemui.statusbar.FeatureFlags"
    private const val GAME_ENTRY_CLASS = "com.google.android.systemui.gamedashboard.EntryPointController"
    private const val PRIVACY_CLASS = "com.android.systemui.privacy.PrivacyItemController"

    private val featureFlag = object : XC_MethodReplacement() {
        override fun replaceHookedMethod(param: MethodHookParam) = true
    }

    private val gameDashboard = object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            param.thisObject.javaClass.getDeclaredField("DISABLED").let {
                it.isAccessible = true
                it.set(null, java.lang.Boolean.FALSE)
            }

            XposedHelpers.setBooleanField(param.thisObject, "mShouldShow", true)
        }
    }

    private val roundedScreenshotBg = object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            param.thisObject.javaClass.getDeclaredField("DEBUG_COLOR").let {
                it.isAccessible = true
                it.setBoolean(null, false)
            }
        }
    }

    private val privacyIndicators = object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            XposedHelpers.setBooleanField(param.thisObject, "micCameraAvailable", true)
            XposedHelpers.setBooleanField(param.thisObject, "locationAvailable", true)
        }
    }

    private val themeOverlayController by lazy(mode = LazyThreadSafetyMode.NONE) {
        ThemeOverlayController(ReferenceColors.MonetPurple)
    }

    private val themeGetOverlayHook = object : XC_MethodReplacement() {
        override fun replaceHookedMethod(param: MethodHookParam): Any {
            return themeOverlayController.getOverlay(param.args[0] as Int, param.args[1] as Int)
        }
    }

    fun applyFeatureFlag(lpparam: XC_LoadPackage.LoadPackageParam, flag: String) {
        try {
            lpparam.hookMethod(FEATURE_FLAGS_CLASS, featureFlag, flag)
        } catch (e: NoSuchMethodException) {
            Timber.w("Feature flag does not exist: $flag")
        }
    }

    fun applyGameDashboard(lpparam: XC_LoadPackage.LoadPackageParam) {
        lpparam.hookMethod(
                GAME_ENTRY_CLASS,
                gameDashboard,
                "setButtonState",
                Boolean::class.java,
                Boolean::class.java
        )
    }

    fun applyRoundedScreenshotBg(lpparam: XC_LoadPackage.LoadPackageParam) {
        lpparam.hookMethod(
                "com.android.systemui.ScreenDecorations",
                roundedScreenshotBg,
                "updateColorInversion",
                Int::class.java
        )
    }

    fun applyPrivacyIndicators(lpparam: XC_LoadPackage.LoadPackageParam) {
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
                privacyIndicators,
        )
    }

    fun applyThemeOverlayController(lpparam: XC_LoadPackage.LoadPackageParam) {
        lpparam.hookMethod(
                "com.google.android.systemui.theme.ThemeOverlayControllerGoogle",
                themeGetOverlayHook,
                "getOverlay",
                Int::class.java,
                Int::class.java,
        )
    }
}