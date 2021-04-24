package dev.kdrag0n.android12ext.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController

abstract class NavViewModel(app: Application) : AndroidViewModel(app) {
    val navDest = MutableLiveData<Int>(null)
}

fun MutableLiveData<Int>.observeNav(fragment: BaseFragment) {
    observe(fragment.viewLifecycleOwner) { dest ->
        if (dest != null) {
            fragment.findNavController().navigate(dest)
            value = null
        }
    }
}