package dev.kdrag0n.android12ext.ui.settings.system

import dagger.hilt.android.lifecycle.HiltViewModel
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.pref
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.core.data.SettingsRepository
import dev.kdrag0n.android12ext.ui.settings.BaseSettingsViewModel
import dev.kdrag0n.android12ext.ui.utils.featureSwitch
import dev.kdrag0n.android12ext.ui.utils.setInteractive
import javax.inject.Inject

@HiltViewModel
class SystemSettingsViewModel @Inject constructor(
    settingsRepo: SettingsRepository,
) : BaseSettingsViewModel() {
    private val prefScreen = settingsRepo.prefScreen {
        featureSwitch(
            key = "monet",
            title = R.string.system_monet,
            summary = R.string.system_monet_desc,
            icon = R.drawable.ic_fluent_paint_brush_24_regular,
        )
        featureSwitch(
            key = "lockscreen",
            title = R.string.system_lockscreen,
            summary = R.string.system_lockscreen_desc,
            icon = R.drawable.ic_fluent_lock_closed_24_regular,
        )
        featureSwitch(
            key = "toast",
            title = R.string.system_toast,
            summary = R.string.system_toast_desc,
            icon = R.drawable.ic_fluent_badge_24_regular,
        )
        featureSwitch(
            key = "rounded_screenshots",
            title = R.string.system_rounded_screenshots,
            summary = R.string.system_rounded_screenshots_desc,
            icon = R.drawable.ic_fluent_screenshot_24_regular,
            default = false,
        )
        featureSwitch(
            key = "charging_ripple",
            title = R.string.system_charging_ripple,
            summary = R.string.system_charging_ripple_desc,
            icon = R.drawable.ic_fluent_battery_charge_24_regular,
        )
        featureSwitch(
            key = "internet_ui",
            title = R.string.system_internet_ui,
            summary = R.string.system_internet_ui_desc,
            icon = R.drawable.ic_fluent_globe_24_regular,
        )

        pref("system_info") {
            iconRes = R.drawable.ic_fluent_info_24_regular
            summaryRes = R.string.system_info
            // Disabling the view makes text contrast too low, so use our extension instead
            setInteractive(false)
        }
    }
    override val prefAdapter = PreferencesAdapter(prefScreen)
}
