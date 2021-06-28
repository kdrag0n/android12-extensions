package dev.kdrag0n.android12ext.ui.settings.system

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.kdrag0n.android12ext.ui.settings.BaseSettingsFragment

@AndroidEntryPoint
class SystemSettingsFragment : BaseSettingsFragment() {
    private val viewModel: SystemSettingsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel(viewModel)
    }
}
