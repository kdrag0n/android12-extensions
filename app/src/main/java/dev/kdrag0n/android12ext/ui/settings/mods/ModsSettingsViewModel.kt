package dev.kdrag0n.android12ext.ui.settings.mods

import android.app.Application
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.categoryHeader
import de.Maxr1998.modernpreferences.helpers.onClick
import de.Maxr1998.modernpreferences.helpers.pref
import dev.kdrag0n.android12ext.BuildConfig
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.core.data.SettingsRepository
import dev.kdrag0n.android12ext.monet.theme.ReferenceGenerator
import dev.kdrag0n.android12ext.ui.settings.BaseSettingsViewModel
import dev.kdrag0n.android12ext.ui.utils.buildWithPrefs
import dev.kdrag0n.android12ext.ui.utils.featureSwitch
import dev.kdrag0n.android12ext.ui.utils.setInteractive

class ModsSettingsViewModel(
        app: Application,
        private val settingsRepo: SettingsRepository,
        private val refGen: ReferenceGenerator,
) : BaseSettingsViewModel(app) {
    private val prefScreen = PreferenceScreen.Builder(app).run {
        featureSwitch(
            key = "haptic_touch",
            title = R.string.mod_haptic_touch,
            summary = R.string.mod_haptic_touch_desc,
            icon = R.drawable.ic_fluent_phone_vibrate_24_regular,
            default = false,
        )
        pref("mods_info") {
            iconRes = R.drawable.ic_fluent_info_24_regular
            summaryRes = R.string.mods_info
            // Disabling the view makes the text contrast too low, so use our extension instead
            setInteractive(false)
        }

        categoryHeader("category_theming") {
            titleRes = R.string.category_theming
        }
        featureSwitch(
            key = "custom_monet",
            title = R.string.mod_custom_monet,
            summary = R.string.mod_custom_monet_desc,
            icon = R.drawable.ic_fluent_paint_brush_24_regular,
            default = false,
        )
        featureSwitch(
            key = "custom_monet_boost_chroma",
            title = R.string.mod_custom_monet_boost_chroma,
            summary = R.string.mod_custom_monet_boost_chroma_desc,
            icon = R.drawable.ic_fluent_color_fill_24_regular,
            default = false,
            dependency = "custom_monet_enabled",
        )

        // Debug
        if (BuildConfig.DEBUG) {
            categoryHeader("debug_header") {
                title = "Debug"
            }
            pref("gen_ref") {
                title = "Generate reference color table"
                onClick {
                    refGen.generateTable()
                    false
                }
            }
        }

        buildWithPrefs(settingsRepo.prefs)
    }
    override val prefAdapter = PreferencesAdapter(prefScreen)
}