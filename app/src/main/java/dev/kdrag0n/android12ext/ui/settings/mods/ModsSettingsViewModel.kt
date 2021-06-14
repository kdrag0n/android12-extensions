package dev.kdrag0n.android12ext.ui.settings.mods

import android.app.*
import android.content.Intent
import android.graphics.drawable.Icon
import androidx.core.content.getSystemService
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.categoryHeader
import de.Maxr1998.modernpreferences.helpers.onClick
import de.Maxr1998.modernpreferences.helpers.pref
import de.Maxr1998.modernpreferences.helpers.seekBar
import dev.kdrag0n.android12ext.BuildConfig
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.core.CallService
import dev.kdrag0n.android12ext.core.data.SettingsRepository
import dev.kdrag0n.android12ext.core.data.hasSystemUiGoogle
import dev.kdrag0n.android12ext.monet.theme.ReferenceGenerator
import dev.kdrag0n.android12ext.ui.main.MainActivity
import dev.kdrag0n.android12ext.ui.settings.BaseSettingsViewModel
import dev.kdrag0n.android12ext.ui.utils.buildWithPrefs
import dev.kdrag0n.android12ext.ui.utils.featureSwitch
import dev.kdrag0n.android12ext.ui.utils.navPref
import dev.kdrag0n.android12ext.ui.utils.setInteractive
import java.util.concurrent.ThreadLocalRandom

class ModsSettingsViewModel(
    app: Application,
    private val settingsRepo: SettingsRepository,
    private val refGen: ReferenceGenerator,
) : BaseSettingsViewModel(app) {
    private val prefScreen = PreferenceScreen.Builder(app).run {
        categoryHeader("category_theming") {
            titleRes = R.string.category_theming
        }
        val hasSystemUiGoogle = app.hasSystemUiGoogle()
        featureSwitch(
            key = "custom_monet",
            title = R.string.mod_custom_monet,
            summary = if (hasSystemUiGoogle) R.string.mod_custom_monet_desc else R.string.mod_custom_monet_desc_forced,
            icon = R.drawable.ic_fluent_paint_brush_24_regular,
            default = !hasSystemUiGoogle,
            enabled = hasSystemUiGoogle,
        )
        seekBar("custom_monet_chroma_multiplier") {
            titleRes = R.string.mod_custom_monet_chroma_multiplier
            iconRes = R.drawable.ic_fluent_color_fill_24_regular
            dependency = "custom_monet_enabled"

            min = 0
            default = 50
            max = 100
            step = 10
            formatter = { value ->
                String.format("%.01fx", value.toFloat() / 50)
            }
        }

        if (!hasSystemUiGoogle) {
            categoryHeader("category_aosp") {
                titleRes = R.string.category_aosp
            }
            featureSwitch(
                key = "aosp_circle_icons",
                title = R.string.mod_aosp_circle_icons,
                summary = R.string.mod_aosp_circle_icons_desc,
                icon = R.drawable.ic_fluent_circle_24_regular,
            )
        }

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
            navPref(
                key = "monet_quantizer",
                title = R.string.mod_monet_quantizer,
                icon = R.drawable.ic_fluent_wallpaper_24_regular,
                action = R.id.action_settings_mods_to_quantizer,
                vm = this@ModsSettingsViewModel,
            )
            pref("test_ongoing_call") {
                title = "Test ongoing call"
                onClick {
                    CallService.start(app)
                    false
                }
            }
        }

        buildWithPrefs(settingsRepo.prefs)
    }
    override val prefAdapter = PreferencesAdapter(prefScreen)
}
