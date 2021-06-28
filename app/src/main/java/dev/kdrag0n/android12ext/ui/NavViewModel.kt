package dev.kdrag0n.android12ext.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController

abstract class NavViewModel : ViewModel() {
    open val navDest = MutableLiveData<Int>(null)
}

fun MutableLiveData<Int>.observeNav(fragment: BaseFragment) {
    observe(fragment.viewLifecycleOwner) { dest ->
        if (dest != null) {
            try {
                fragment.findNavController().navigate(dest)
            } catch (e: IllegalArgumentException) {
                // AndroidX bug: https://github.com/android/sunflower/issues/239
            }

            value = null
        }
    }
}
