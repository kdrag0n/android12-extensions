package dev.kdrag0n.android12ext.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.transition.MaterialSharedAxis
import dev.kdrag0n.android12ext.R

open class BaseFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyTransitions()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyTransitionsViewCreated()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, findNavController()) ||
                super.onOptionsItemSelected(item)
    }
}

fun Fragment.applyTransitions() {
    // Default transitions similar to Android 10+ Activity transitions
    enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
    returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
    reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
}

fun Fragment.applyTransitionsViewCreated() {
    // Workaround for AndroidX bug: https://github.com/material-components/material-components-android/issues/1984
    view?.setBackgroundResource(R.drawable.solid_background)
}
