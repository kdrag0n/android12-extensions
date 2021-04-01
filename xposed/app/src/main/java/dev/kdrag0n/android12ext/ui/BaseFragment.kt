package dev.kdrag0n.android12ext.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialSharedAxis
import dev.kdrag0n.android12ext.R

open class BaseFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Default transitions similar to Android 10+ Activity transitions
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Workaround for AndroidX bug: https://github.com/material-components/material-components-android/issues/1984
        view.setBackgroundResource(R.drawable.solid_background)
    }
}