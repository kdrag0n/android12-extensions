package dev.kdrag0n.android12ext.ui.settings.launcher

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.kdrag0n.android12ext.ui.settings.BaseSettingsFragment

@AndroidEntryPoint
class LauncherSettingsFragment : BaseSettingsFragment() {
    private val viewModel: LauncherSettingsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel(viewModel)
    }
}
