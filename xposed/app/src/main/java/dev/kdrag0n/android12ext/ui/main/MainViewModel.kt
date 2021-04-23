package dev.kdrag0n.android12ext.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dev.kdrag0n.android12ext.core.Broadcasts
import kotlinx.coroutines.launch

class MainViewModel(private val app: Application) : AndroidViewModel(app) {
    // Set initial state to true to avoid a dialog flash
    val isXposedHooked = MutableLiveData(true)
    fun updateHookState() {
        viewModelScope.launch {
            isXposedHooked.value = Broadcasts.pingSysUi(app)
        }
    }
}