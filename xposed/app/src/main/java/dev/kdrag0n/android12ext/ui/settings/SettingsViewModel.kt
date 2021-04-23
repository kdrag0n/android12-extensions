package dev.kdrag0n.android12ext.ui.settings

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.*
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.core.*
import dev.kdrag0n.android12ext.core.xposed.XposedPreferenceProvider
import dev.kdrag0n.android12ext.ui.utils.buildWithPrefs
import dev.kdrag0n.android12ext.ui.utils.setInteractive
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SettingsViewModel(private val app: Application) : AndroidViewModel(app) {
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

        categoryHeader("features") {
            titleRes = R.string.features
        }
        switch("monet_enabled") {
            titleRes = R.string.feature_monet
            summaryRes = R.string.feature_monet_desc
            iconRes = R.drawable.ic_fluent_paint_brush_24_regular
            defaultValue = true
            dependency = "global_enabled"
        }
        switch("lockscreen_enabled") {
            titleRes = R.string.feature_lockscreen
            summaryRes = R.string.feature_lockscreen_desc
            iconRes = R.drawable.ic_fluent_lock_closed_24_regular
            defaultValue = true
            dependency = "global_enabled"
        }
        switch("notification_shade_enabled") {
            titleRes = R.string.feature_notification_shade
            summaryRes = R.string.feature_notification_shade_desc
            iconRes = R.drawable.ic_fluent_alert_24_regular
            defaultValue = true
            dependency = "global_enabled"
        }
        switch("quick_settings_enabled") {
            titleRes = R.string.feature_quick_settings
            summaryRes = R.string.feature_quick_settings_desc
            iconRes = R.drawable.ic_fluent_table_settings_24_regular
            defaultValue = true
            dependency = "global_enabled"
        }
        switch("toast_enabled") {
            titleRes = R.string.feature_toast
            summaryRes = R.string.feature_toast_desc
            iconRes = R.drawable.ic_fluent_badge_24_regular
            defaultValue = true
            dependency = "global_enabled"
        }
        switch("game_dashboard_enabled") {
            titleRes = R.string.feature_game_dashboard
            summaryRes = R.string.feature_game_dashboard_desc
            iconRes = R.drawable.ic_fluent_games_24_regular
            defaultValue = true
            dependency = "global_enabled"
        }
        switch("privacy_indicators_enabled") {
            titleRes = R.string.feature_privacy_indicators
            summaryRes = R.string.feature_privacy_indicators_desc
            iconRes = R.drawable.ic_fluent_incognito_24_regular
            defaultValue = true
            dependency = "global_enabled"
        }
        switch("charging_ripple_enabled") {
            titleRes = R.string.feature_charging_ripple
            summaryRes = R.string.feature_charging_ripple_desc
            iconRes = R.drawable.ic_fluent_battery_charge_24_regular
            defaultValue = true
            dependency = "global_enabled"
        }

        categoryHeader("tweaks") {
            titleRes = R.string.tweaks
        }
        switch("patterned_ripple_enabled") {
            titleRes = R.string.tweak_patterned_ripple
            summaryRes = R.string.tweak_patterned_ripple_desc
            iconRes = R.drawable.ic_fluent_tap_single_24_regular
            defaultValue = true
            dependency = "global_enabled"
        }
        pref("tweaks_info") {
            iconRes = R.drawable.ic_fluent_info_24_regular
            summaryRes = R.string.tweaks_info
            // Disabling the view makes the text contrast too low, so use our extension instead
            setInteractive(false)
        }

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

    // Set initial state to true to avoid a dialog flash
    val isXposedHooked = MutableLiveData(true)
    fun updateHookState() {
        viewModelScope.launch {
            isXposedHooked.value = Broadcasts.pingSysUi(app)
        }
    }

    init {
        prefs.registerOnSharedPreferenceChangeListener(prefChangeListener)
    }

    override fun onCleared() {
        prefs.unregisterOnSharedPreferenceChangeListener(prefChangeListener)
    }
}