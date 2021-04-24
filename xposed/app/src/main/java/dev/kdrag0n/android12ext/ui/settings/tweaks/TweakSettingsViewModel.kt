package dev.kdrag0n.android12ext.ui.settings.tweaks

import android.app.Application
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.pref
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.core.data.SettingsRepository
import dev.kdrag0n.android12ext.ui.settings.BaseSettingsViewModel
import dev.kdrag0n.android12ext.ui.utils.buildWithPrefs
import dev.kdrag0n.android12ext.ui.utils.featureSwitch
import dev.kdrag0n.android12ext.ui.utils.setInteractive

class TweakSettingsViewModel(
    app: Application,
    private val settingsRepo: SettingsRepository,
) : BaseSettingsViewModel(app) {
    private val prefScreen = PreferenceScreen.Builder(app).run {
        featureSwitch(
            key = "patterned_ripple",
            title = R.string.tweak_patterned_ripple,
            summary = R.string.tweak_patterned_ripple_desc,
            icon = R.drawable.ic_fluent_tap_single_24_regular,
        )
        pref("tweaks_info") {
            iconRes = R.drawable.ic_fluent_info_24_regular
            summaryRes = R.string.tweaks_info
            // Disabling the view makes the text contrast too low, so use our extension instead
            setInteractive(false)
        }

        buildWithPrefs(settingsRepo.prefs)
    }
    override val prefAdapter = PreferencesAdapter(prefScreen)
}
