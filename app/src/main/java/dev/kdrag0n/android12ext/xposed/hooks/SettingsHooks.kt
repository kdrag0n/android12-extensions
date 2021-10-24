package dev.kdrag0n.android12ext.xposed.hooks

import de.robv.android.xposed.callbacks.XC_LoadPackage

class SettingsHooks(
    lpparam: XC_LoadPackage.LoadPackageParam,
) : BaseHooks(lpparam) {
    fun applyBatterySlots(enabled: Boolean) {
        hookReturn(
            "com.google.android.settings.fuelgauge.PowerUsageFeatureProviderGoogleImpl",
            "isChartGraphSlotsEnabled",
            enabled,
        )
    }
}
