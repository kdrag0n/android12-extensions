package dev.kdrag0n.android12ext.ui.settings.launcher

import android.os.Bundle
import android.view.View
import dev.kdrag0n.android12ext.ui.settings.BaseSettingsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class LauncherSettingsFragment : BaseSettingsFragment() {
    private val viewModel: LauncherSettingsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel(viewModel)
    }
}
