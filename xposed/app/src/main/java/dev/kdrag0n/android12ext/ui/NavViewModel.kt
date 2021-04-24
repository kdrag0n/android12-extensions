package dev.kdrag0n.android12ext.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

abstract class NavViewModel(app: Application) : AndroidViewModel(app) {
    val navDest = MutableLiveData<Int>(null)
}