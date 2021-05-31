package dev.kdrag0n.android12ext.ui.settings.mods

import android.os.Bundle
import android.view.View
import dev.kdrag0n.android12ext.ui.observeNav
import dev.kdrag0n.android12ext.ui.settings.BaseSettingsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ModsSettingsFragment : BaseSettingsFragment() {
    private val viewModel: ModsSettingsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel(viewModel)
        viewModel.navDest.observeNav(this)
    }
}
