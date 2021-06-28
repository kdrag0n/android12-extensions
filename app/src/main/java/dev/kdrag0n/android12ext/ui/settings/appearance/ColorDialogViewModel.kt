package dev.kdrag0n.android12ext.ui.settings.appearance

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ColorDialogViewModel @Inject constructor() : ViewModel() {
    val selectedColor = MutableLiveData<Int>()
}
