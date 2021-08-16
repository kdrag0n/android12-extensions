package dev.kdrag0n.android12ext.ui.settings.appearance.advanced

import dagger.hilt.android.lifecycle.HiltViewModel
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.seekBar
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.data.SettingsRepository
import dev.kdrag0n.android12ext.ui.settings.BaseSettingsViewModel
import dev.kdrag0n.android12ext.ui.utils.featureSwitch
import dev.kdrag0n.android12ext.ui.utils.telemetryPrefs
import javax.inject.Inject

@HiltViewModel
class AdvancedCamSettingsViewModel @Inject constructor(
    settingsRepo: SettingsRepository,
) : BaseSettingsViewModel() {
    private val prefScreen = settingsRepo.prefScreen {
        featureSwitch(
            key = "custom_monet_zcam_linear_lightness",
            title = R.string.appearance_advanced_linear_lightness,
            summary = R.string.appearance_advanced_linear_lightness_desc,
            icon = R.drawable.ic_fluent_weather_haze_24_regular,
            default = false,
            dependency = "custom_monet_enabled",
        )
        seekBar("custom_monet_zcam_white_luminance_user") {
            titleRes = R.string.appearance_advanced_white_luminance
            summaryRes = R.string.appearance_advanced_white_luminance_desc
            iconRes = R.drawable.ic_fluent_weather_sunny_24_regular
            dependency = "custom_monet_enabled"

            min = 0
            default = SettingsRepository.WHITE_LUMINANCE_USER_DEFAULT
            max = SettingsRepository.WHITE_LUMINANCE_USER_MAX
            step = SettingsRepository.WHITE_LUMINANCE_USER_STEP
            formatter = { value ->
                value.toString()
            }
        }

        telemetryPrefs(this@AdvancedCamSettingsViewModel, settingsRepo)
    }
    override val prefAdapter = PreferencesAdapter(prefScreen)
}
