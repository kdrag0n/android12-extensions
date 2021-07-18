package dev.kdrag0n.android12ext.ui.settings.launcher

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
class LauncherSettingsViewModel @Inject constructor(
    settingsRepo: SettingsRepository,
) : BaseSettingsViewModel() {
    private val prefScreen = settingsRepo.prefScreen {
        featureSwitch(
            key = "launcher_themed_icons",
            title = R.string.launcher_themed_icons,
            summary = R.string.launcher_themed_icons_desc,
            icon = R.drawable.ic_fluent_icons_24_regular,
        )
        featureSwitch(
            key = "launcher_device_search",
            title = R.string.launcher_device_search,
            summary = R.string.launcher_device_search_desc,
            icon = R.drawable.ic_fluent_search_24_regular,
        )
        featureSwitch(
            key = "launcher_animations",
            title = R.string.launcher_animations,
            summary = R.string.launcher_animations_desc,
            icon = R.drawable.ic_fluent_open_24_regular,
        )

        pref("launcher_info") {
            iconRes = R.drawable.ic_fluent_info_24_regular
            summaryRes = R.string.launcher_info
            // Disabling the view makes text contrast too low, so use our extension instead
            setInteractive(false)
        }
    }
    override val prefAdapter = PreferencesAdapter(prefScreen)
}
