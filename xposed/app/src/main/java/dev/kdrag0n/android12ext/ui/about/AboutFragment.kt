package dev.kdrag0n.android12ext.ui.about

import android.os.Bundle
import android.view.View
import com.mikepenz.aboutlibraries.ui.LibsSupportFragment
import dev.kdrag0n.android12ext.ui.applyTransitions
import dev.kdrag0n.android12ext.ui.applyTransitionsViewCreated

class AboutFragment : LibsSupportFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyTransitions()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyTransitionsViewCreated()
    }
}