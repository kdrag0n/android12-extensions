package dev.kdrag0n.android12ext.ui.settings.settings

import android.app.Application
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.pref
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.core.data.SettingsRepository
import dev.kdrag0n.android12ext.core.data.hasSystemUiGoogle
import dev.kdrag0n.android12ext.ui.settings.BaseSettingsViewModel
import dev.kdrag0n.android12ext.ui.utils.buildWithPrefs
import dev.kdrag0n.android12ext.ui.utils.featureSwitch
import dev.kdrag0n.android12ext.ui.utils.setInteractive

class AndroidSettingsSettingsViewModel(
    app: Application,
    private val settingsRepo: SettingsRepository,
) : BaseSettingsViewModel(app) {
    private val prefScreen = PreferenceScreen.Builder(app).run {
        featureSwitch(
            key = "settings_shared_axis",
            title = R.string.android_settings_shared_axis,
            summary = R.string.android_settings_shared_axis_desc,
            icon = R.drawable.ic_fluent_slide_transition_24_regular,
        )
        featureSwitch(
            key = "settings_battery_slots",
            title = R.string.android_settings_battery_slots,
            summary = R.string.android_settings_battery_slots_desc,
            icon = R.drawable.ic_fluent_battery_5_24_regular,
        ) {
            visible = app.hasSystemUiGoogle()
        }

        pref("android_settings_info") {
            iconRes = R.drawable.ic_fluent_info_24_regular
            summaryRes = R.string.android_settings_info
            // Disabling the view makes text contrast too low, so use our extension instead
            setInteractive(false)
        }

        buildWithPrefs(settingsRepo.prefs)
    }
    override val prefAdapter = PreferencesAdapter(prefScreen)
}
