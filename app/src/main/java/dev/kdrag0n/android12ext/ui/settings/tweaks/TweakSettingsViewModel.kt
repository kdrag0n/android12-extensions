package dev.kdrag0n.android12ext.ui.settings.tweaks

import android.app.Application
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.pref
import de.Maxr1998.modernpreferences.helpers.singleChoice
import de.Maxr1998.modernpreferences.preferences.choice.SelectionItem
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
        singleChoice("ripple_style", listOf(
            SelectionItem("default", R.string.tweak_ripple_style_default, R.string.tweak_ripple_style_default_desc),
            SelectionItem("no_sparkles", R.string.tweak_ripple_style_no_sparkles, R.string.tweak_ripple_style_no_sparkles_desc),
            SelectionItem("legacy", R.string.tweak_ripple_style_legacy, R.string.tweak_ripple_style_legacy_desc),
            SelectionItem("fluent", R.string.tweak_ripple_style_fluent, R.string.tweak_ripple_style_fluent_desc),
        )) {
            titleRes = R.string.tweak_ripple_style
            summaryRes = R.string.tweak_ripple_style_default
            iconRes = R.drawable.ic_fluent_tap_single_24_regular
        }

        featureSwitch(
            key = "haptic_touch",
            title = R.string.tweak_haptic_touch,
            summary = R.string.tweak_haptic_touch_desc,
            icon = R.drawable.ic_fluent_phone_vibrate_24_regular,
            default = false,
        )
        pref("tweaks_info") {
            iconRes = R.drawable.ic_fluent_info_24_regular
            summaryRes = R.string.tweaks_info
            // Disabling the view makes text contrast too low, so use our extension instead
            setInteractive(false)
        }

        buildWithPrefs(settingsRepo.prefs)
    }
    override val prefAdapter = PreferencesAdapter(prefScreen)
}
