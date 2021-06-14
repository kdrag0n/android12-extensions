package dev.kdrag0n.android12ext.ui.settings.appearance

import android.os.Bundle
import android.view.View
import dev.kdrag0n.android12ext.ui.observeNav
import dev.kdrag0n.android12ext.ui.settings.BaseSettingsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class AppearanceSettingsFragment : BaseSettingsFragment() {
    private val viewModel: AppearanceSettingsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel(viewModel)
        viewModel.navDest.observeNav(this)
    }
}
