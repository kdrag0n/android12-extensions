package dev.kdrag0n.android12ext.ui.settings.settings

import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.pref
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.core.data.SettingsRepository
import dev.kdrag0n.android12ext.core.data.hasSystemUiGoogle
import dev.kdrag0n.android12ext.ui.settings.BaseSettingsViewModel
import dev.kdrag0n.android12ext.ui.utils.featureSwitch
import dev.kdrag0n.android12ext.ui.utils.setInteractive
import javax.inject.Inject

@HiltViewModel
class AndroidSettingsSettingsViewModel @Inject constructor(
    @ApplicationContext context: Context,
    settingsRepo: SettingsRepository,
) : BaseSettingsViewModel() {
    private val prefScreen = settingsRepo.prefScreen {
        featureSwitch(
            key = "settings_battery_slots",
            title = R.string.android_settings_battery_slots,
            summary = R.string.android_settings_battery_slots_desc,
            icon = R.drawable.ic_fluent_battery_5_24_regular,
        ) {
            visible = context.hasSystemUiGoogle()
        }

        pref("android_settings_info") {
            iconRes = R.drawable.ic_fluent_info_24_regular
            summaryRes = R.string.android_settings_info
            // Disabling the view makes text contrast too low, so use our extension instead
            setInteractive(false)
        }
    }
    override val prefAdapter = PreferencesAdapter(prefScreen)
}
