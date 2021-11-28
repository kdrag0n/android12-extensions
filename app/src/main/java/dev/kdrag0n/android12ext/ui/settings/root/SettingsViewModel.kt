package dev.kdrag0n.android12ext.ui.settings.root

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.*
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.core.*
import dev.kdrag0n.android12ext.data.SettingsRepository
import dev.kdrag0n.android12ext.data.hasPixelLauncher
import dev.kdrag0n.android12ext.ui.settings.BaseSettingsViewModel
import dev.kdrag0n.android12ext.ui.utils.featureSwitch
import dev.kdrag0n.android12ext.ui.utils.navPref
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val settingsRepo: SettingsRepository,
    private val broadcastManager: BroadcastManager,
) : BaseSettingsViewModel() {
    private val prefScreen = settingsRepo.prefScreen {
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
            dependency = "global_enabled",
        )
        navPref(
            key = "settings_android_settings",
            title = R.string.settings_android_settings,
            summary = R.string.settings_android_settings_desc,
            icon = R.drawable.ic_fluent_settings_24_regular,
            action = R.id.action_settings_root_to_android_settings,
            vm = this@SettingsViewModel,
            dependency = "global_enabled",
        )
        if (context.hasPixelLauncher()) {
            navPref(
                key = "settings_launcher",
                title = R.string.settings_launcher,
                summary = R.string.settings_launcher_desc,
                icon = R.drawable.ic_fluent_app_folder_24_regular,
                action = R.id.action_settings_root_to_launcher,
                vm = this@SettingsViewModel,
                dependency = "global_enabled",
            )
        }
        navPref(
            key = "settings_tweaks",
            title = R.string.settings_tweaks,
            summary = R.string.settings_tweaks_desc,
            icon = R.drawable.ic_fluent_wrench_24_regular,
            action = R.id.action_settings_root_to_tweaks,
            vm = this@SettingsViewModel,
            dependency = "global_enabled",
        )
        navPref(
            key = "settings_appearance",
            title = R.string.settings_appearance,
            summary = R.string.settings_appearance_desc,
            icon = R.drawable.ic_fluent_color_24_regular,
            action = R.id.action_settings_root_to_appearance,
            vm = this@SettingsViewModel,
            dependency = "global_enabled",
        )

        categoryHeader("telemetry") {
            titleRes = R.string.appearance_advanced_telemetry
        }
        featureSwitch(
            key = "telemetry_send_settings_report",
            title = R.string.appearance_advanced_send_settings_report,
            summary = R.string.appearance_advanced_send_settings_report_desc,
            icon = R.drawable.ic_fluent_emoji_24_regular,
            default = false,
        )
    }
    override val prefAdapter = PreferencesAdapter(prefScreen)

    val showTelemetryPrompt = MutableLiveData(!settingsRepo.prefs.getBoolean("telemetry_prompt_shown", false))

    fun setTelemetryConsent(sendReports: Boolean) {
        showTelemetryPrompt.value = false
        settingsRepo.prefs.edit {
            putBoolean("telemetry_prompt_shown", true)
            putBoolean("telemetry_send_settings_report_enabled", sendReports)
        }

        // Now send an initial report
        if (sendReports) {
            viewModelScope.launch {
                settingsRepo.reportSettings()
            }
        }
    }

    fun reload() {
        broadcastManager.broadcastReload()
    }
}
