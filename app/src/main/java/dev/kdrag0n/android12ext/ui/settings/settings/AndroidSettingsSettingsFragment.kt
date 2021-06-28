package dev.kdrag0n.android12ext.ui.settings.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.kdrag0n.android12ext.ui.settings.BaseSettingsFragment

@AndroidEntryPoint
class AndroidSettingsSettingsFragment : BaseSettingsFragment() {
    private val viewModel: AndroidSettingsSettingsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel(viewModel)
    }
}
