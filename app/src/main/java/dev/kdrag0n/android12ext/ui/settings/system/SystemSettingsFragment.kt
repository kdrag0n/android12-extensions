package dev.kdrag0n.android12ext.ui.settings.system

import android.os.Bundle
import android.view.*
import dev.kdrag0n.android12ext.ui.settings.BaseSettingsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SystemSettingsFragment : BaseSettingsFragment() {
    private val viewModel: SystemSettingsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel(viewModel)
    }
}