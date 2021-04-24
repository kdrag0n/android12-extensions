package dev.kdrag0n.android12ext.ui.settings

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.*
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.core.*
import dev.kdrag0n.android12ext.core.xposed.XposedPreferenceProvider
import dev.kdrag0n.android12ext.ui.NavViewModel
import dev.kdrag0n.android12ext.ui.utils.buildWithPrefs
import dev.kdrag0n.android12ext.ui.utils.navPref
import dev.kdrag0n.android12ext.ui.utils.setInteractive
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SettingsViewModel(private val app: Application) : NavViewModel(app) {
    private val prefs = app.createDeviceProtectedStorageContext()
        .getSharedPreferences(XposedPreferenceProvider.DEFAULT_PREFS, Context.MODE_PRIVATE)

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

        buildWithPrefs(prefs)
    }
    val prefAdapter = PreferencesAdapter(prefScreen)

    // Doesn't need to be atomic because the viewModelScope dispatcher is single-threaded
    private var prefChangeCount = 0

    // Needs to be separate from registerOnSharedPreferenceChangeListener in order to hold a strong reference
    private val prefChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
        // Debounce restarts to mitigate excessive disruption
        viewModelScope.launch {
            showReloadWarning.value = false

            val startCount = ++prefChangeCount
            delay(Broadcasts.RELOAD_DEBOUNCE_DELAY)

            // First debounce: show warning
            if (prefChangeCount == startCount) {
                showReloadWarning.value = true
                delay(Broadcasts.RELOAD_WARNING_DURATION.toLong())

                // Second debounce: make sure warning is still shown *and* no pref changes were made
                if (prefChangeCount == startCount && showReloadWarning.value == true) {
                    broadcastReload()

                    // Give time for SystemUI to restart
                    delay(Broadcasts.RELOAD_RESTART_DELAY)
                    showReloadWarning.value = false
                }
            }
        }
    }
    val showReloadWarning = MutableLiveData(false)

    fun broadcastReload() {
        app.sendBroadcast(Intent(Broadcasts.RELOAD_ACTION))
    }

    init {
        prefs.registerOnSharedPreferenceChangeListener(prefChangeListener)
    }

    override fun onCleared() {
        prefs.unregisterOnSharedPreferenceChangeListener(prefChangeListener)
    }
}