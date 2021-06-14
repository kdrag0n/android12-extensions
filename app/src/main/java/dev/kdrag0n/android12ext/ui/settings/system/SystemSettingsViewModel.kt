package dev.kdrag0n.android12ext.ui.settings.system

import android.app.Application
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.core.data.SettingsRepository
import dev.kdrag0n.android12ext.ui.settings.BaseSettingsViewModel
import dev.kdrag0n.android12ext.ui.utils.buildWithPrefs
import dev.kdrag0n.android12ext.ui.utils.featureSwitch

class SystemSettingsViewModel(
    app: Application,
    private val settingsRepo: SettingsRepository,
) : BaseSettingsViewModel(app) {
    private val prefScreen = PreferenceScreen.Builder(app).run {
        featureSwitch(
            key = "monet",
            title = R.string.feature_monet,
            summary = R.string.feature_monet_desc,
            icon = R.drawable.ic_fluent_paint_brush_24_regular,
        )
        featureSwitch(
            key = "lockscreen",
            title = R.string.feature_lockscreen,
            summary = R.string.feature_lockscreen_desc,
            icon = R.drawable.ic_fluent_lock_closed_24_regular,
        )
        featureSwitch(
            key = "toast",
            title = R.string.feature_toast,
            summary = R.string.feature_toast_desc,
            icon = R.drawable.ic_fluent_badge_24_regular,
        )
        featureSwitch(
            key = "charging_ripple",
            title = R.string.feature_charging_ripple,
            summary = R.string.feature_charging_ripple_desc,
            icon = R.drawable.ic_fluent_battery_charge_24_regular,
        )
        featureSwitch(
            key = "internet_ui",
            title = R.string.feature_internet_ui,
            summary = R.string.feature_internet_ui_desc,
            icon = R.drawable.ic_fluent_globe_24_regular,
        )

        buildWithPrefs(settingsRepo.prefs)
    }
    override val prefAdapter = PreferencesAdapter(prefScreen)
}
