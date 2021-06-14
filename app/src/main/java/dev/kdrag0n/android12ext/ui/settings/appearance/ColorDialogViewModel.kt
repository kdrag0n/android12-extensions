package dev.kdrag0n.android12ext.ui.settings.appearance

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ColorDialogViewModel : ViewModel() {
    val selectedColor = MutableLiveData<Int>()
}
