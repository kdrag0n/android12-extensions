package dev.kdrag0n.android12ext.ui.settings.appearance.advanced

import dagger.hilt.android.lifecycle.HiltViewModel
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.seekBar
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.core.data.SettingsRepository
import dev.kdrag0n.android12ext.ui.settings.BaseSettingsViewModel
import dev.kdrag0n.android12ext.ui.utils.featureSwitch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class AdvancedCamSettingsViewModel @Inject constructor(
    settingsRepo: SettingsRepository,
) : BaseSettingsViewModel() {
    private val prefScreen = settingsRepo.prefScreen {
        featureSwitch(
            key = "custom_monet_zcam_linear_lightness",
            title = R.string.appearance_advanced_linear_lightness,
            summary = R.string.appearance_advanced_linear_lightness_desc,
            icon = R.drawable.ic_fluent_search_24_regular,
            default = false,
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
                (SettingsRepository.WHITE_LUMINANCE_MAX - SettingsRepository.parseWhiteLuminanceUser(value))
                    .roundToInt().toString()
            }
        }
    }
    override val prefAdapter = PreferencesAdapter(prefScreen)
}
