package dev.kdrag0n.android12ext.ui.settings.system

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
import dev.kdrag0n.android12ext.ui.utils.featureSwitch
import dev.kdrag0n.android12ext.ui.utils.setInteractive
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SystemSettingsViewModel(private val app: Application) : AndroidViewModel(app) {
    private val prefs = app.createDeviceProtectedStorageContext()
        .getSharedPreferences(XposedPreferenceProvider.DEFAULT_PREFS, Context.MODE_PRIVATE)

    private val prefScreen = PreferenceScreen.Builder(app).run {
        Preference.Config.summaryMaxLines = 5

        featureSwitch(
            key = "monet",
            title = R.string.feature_monet,
            summary = R.string.feature_monet_desc,
            icon = R.drawable.ic_fluent_paint_brush_24_regular,
        )
        featureSwitch(
            key = "lockscreen",
            title = R.string.feature_lockscreen,
            summary = R.string.feature_lockscreen_desc,
            icon = R.drawable.ic_fluent_lock_closed_24_regular,
        )
        featureSwitch(
            key = "notification_shade",
            title = R.string.feature_notification_shade,
            summary = R.string.feature_notification_shade_desc,
            icon = R.drawable.ic_fluent_alert_24_regular,
        )
        featureSwitch(
            key = "quick_settings",
            title = R.string.feature_quick_settings,
            summary = R.string.feature_quick_settings_desc,
            icon = R.drawable.ic_fluent_table_settings_24_regular,
        )
        featureSwitch(
            key = "toast",
            title = R.string.feature_toast,
            summary = R.string.feature_toast_desc,
            icon = R.drawable.ic_fluent_badge_24_regular,
        )
        featureSwitch(
            key = "game_dashboard",
            title = R.string.feature_game_dashboard,
            summary = R.string.feature_game_dashboard_desc,
            icon = R.drawable.ic_fluent_games_24_regular,
        )
        featureSwitch(
            key = "privacy_indicators",
            title = R.string.feature_privacy_indicators,
            summary = R.string.feature_privacy_indicators_desc,
            icon = R.drawable.ic_fluent_incognito_24_regular,
        )
        featureSwitch(
            key = "charging_ripple",
            title = R.string.feature_charging_ripple,
            summary = R.string.feature_charging_ripple_desc,
            icon = R.drawable.ic_fluent_battery_charge_24_regular,
        )

        /*
        categoryHeader("tweaks") {
            title = R.string.tweaks
        }
        featureSwitch(
            key = "patterned_ripple",
            title = R.string.tweak_patterned_ripple,
            summary = R.string.tweak_patterned_ripple_desc,
            icon = R.drawable.ic_fluent_tap_single_24_regular,
        )
        pref("tweaks_info") {
            icon = R.drawable.ic_fluent_info_24_regular
            summary = R.string.tweaks_info
            // Disabling the view makes the text contrast too low, so use our extension instead
            setInteractive(false)
        }
*/
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
