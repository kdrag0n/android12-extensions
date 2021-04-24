package dev.kdrag0n.android12ext.ui.main

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.kdrag0n.android12ext.core.BroadcastManager
import dev.kdrag0n.android12ext.core.data.SettingsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(
    private val settingsRepo: SettingsRepository,
    private val broadcastManager: BroadcastManager,
) : ViewModel() {
    // Set initial state to true to avoid a dialog flash
    val isXposedHooked = MutableLiveData(true)
    fun updateHookState() {
        viewModelScope.launch {
            isXposedHooked.value = broadcastManager.pingSysUi()
        }
    }

    // Doesn't need to be atomic because the viewModelScope dispatcher is single-threaded
    private var prefChangeCount = 0

    // Needs to be separate from registerOnSharedPreferenceChangeListener in order to hold a strong reference
    private val prefChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
        // Debounce restarts to mitigate excessive disruption
        viewModelScope.launch {
            showReloadWarning.value = false

            val startCount = ++prefChangeCount
            delay(BroadcastManager.RELOAD_DEBOUNCE_DELAY)

            // First debounce: show warning
            if (prefChangeCount == startCount) {
                showReloadWarning.value = true
                delay(BroadcastManager.RELOAD_WARNING_DURATION.toLong())

                // Second debounce: make sure warning is still shown *and* no pref changes were made
                if (prefChangeCount == startCount && showReloadWarning.value == true) {
                    broadcastManager.broadcastReload()

                    // Give time for SystemUI to restart
                    delay(BroadcastManager.RELOAD_RESTART_DELAY)
                    showReloadWarning.value = false
                }
            }
        }
    }
    val showReloadWarning = MutableLiveData(false)

    init {
        settingsRepo.prefs.registerOnSharedPreferenceChangeListener(prefChangeListener)
    }

    override fun onCleared() {
        settingsRepo.prefs.unregisterOnSharedPreferenceChangeListener(prefChangeListener)
    }
}