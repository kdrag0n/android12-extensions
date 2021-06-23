package dev.kdrag0n.android12ext.ui.settings.settings

import android.os.Bundle
import android.view.View
import dev.kdrag0n.android12ext.ui.settings.BaseSettingsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class AndroidSettingsSettingsFragment : BaseSettingsFragment() {
    private val viewModel: AndroidSettingsSettingsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel(viewModel)
    }
}
