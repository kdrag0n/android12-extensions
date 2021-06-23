package dev.kdrag0n.android12ext.core.xposed.hooks

import android.content.Context
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import dev.kdrag0n.android12ext.core.xposed.hookMethod

class SettingsHooks(
    private val lpparam: XC_LoadPackage.LoadPackageParam,
) {
    fun applySharedAxisTransition() {
        val hook = object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam) = true
        }

        lpparam.hookMethod(
            "com.android.settings.Utils",
            hook,
            "isPageTransitionEnabled",
            Context::class.java,
        )
    }

    fun applyBatterySlots(enabled: Boolean) {
        val hook = object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam) = enabled
        }

        lpparam.hookMethod(
            "com.google.android.settings.fuelgauge.PowerUsageFeatureProviderGoogleImpl",
            hook,
            "isChartGraphSlotsEnabled",
            Context::class.java,
        )
    }
}
