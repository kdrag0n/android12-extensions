package dev.kdrag0n.android12ext.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import java.lang.IllegalArgumentException

abstract class NavViewModel(app: Application) : AndroidViewModel(app) {
    val navDest = MutableLiveData<Int>(null)
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