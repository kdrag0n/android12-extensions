package dev.kdrag0n.android12ext.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.transition.MaterialSharedAxis

abstract class BaseFragment(
    @LayoutRes private val layoutId: Int,
) : Fragment() {
    // Workaround for Hilt Gradle Plugin's lack of support for default arguments
    constructor() : this(0)

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyTransitions()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return if (layoutId == 0) {
            null
        } else {
            inflater.inflate(layoutId, container, false)
        }
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyTransitionsViewCreated()
    }

    @CallSuper
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, findNavController()) ||
                super.onOptionsItemSelected(item)
    }
}

fun Fragment.applyTransitions() {
    // Match Android 12 Settings app (as of Beta 2), but without Google's config
    // For reference, the Settings config is:
    //     450 ms
    //     96dp primary slide
    //     0.22 progress threshold for secondary fade
    //     fast_out_slow_in curve

    enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
    returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
    reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
}

fun Fragment.applyTransitionsViewCreated() {
    // Workaround for AndroidX bug: https://github.com/material-components/material-components-android/issues/1984
    view?.setBackgroundResource(android.R.color.transparent)
}
