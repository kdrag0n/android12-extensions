package dev.kdrag0n.android12ext.ui.settings

import android.app.Application
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.*
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.core.*
import dev.kdrag0n.android12ext.core.data.SettingsRepository
import dev.kdrag0n.android12ext.ui.NavViewModel
import dev.kdrag0n.android12ext.ui.utils.buildWithPrefs
import dev.kdrag0n.android12ext.ui.utils.navPref

class SettingsViewModel(
    app: Application,
    private val settingsRepo: SettingsRepository,
    private val broadcastManager: BroadcastManager,
) : NavViewModel(app) {
    private val prefScreen = PreferenceScreen.Builder(app).run {
        Preference.Config.summaryMaxLines = 5

        switch("global_enabled") {
            titleRes = R.string.enabled
            summaryRes = R.string.enabled_desc
            iconRes = R.drawable.ic_fluent_checkmark_circle_24_regular
            defaultValue = true
        }

        categoryHeader("settings") {
            titleRes = R.string.settings
        }
        navPref(
            key = "settings_system",
            title = R.string.settings_system,
            summary = R.string.settings_system_desc,
            icon = R.drawable.ic_fluent_phone_24_regular,
            action = R.id.action_settings_root_to_system,
            vm = this@SettingsViewModel,
        )
        navPref(
            key = "settings_tweaks",
            title = R.string.settings_tweaks,
            summary = R.string.settings_tweaks_desc,
            icon = R.drawable.ic_fluent_wrench_24_regular,
            action = R.id.action_settings_root_to_system,
            vm = this@SettingsViewModel,
        )
        navPref(
            key = "settings_mods",
            title = R.string.settings_mods,
            summary = R.string.settings_mods_desc,
            icon = R.drawable.ic_fluent_layer_24_regular,
            action = R.id.action_settings_root_to_system,
            vm = this@SettingsViewModel,
        )

        buildWithPrefs(settingsRepo.prefs)
    }
    val prefAdapter = PreferencesAdapter(prefScreen)

    fun reload() {
        broadcastManager.broadcastReload()
    }
}