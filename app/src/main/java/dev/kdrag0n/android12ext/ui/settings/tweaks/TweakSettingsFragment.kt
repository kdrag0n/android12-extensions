package dev.kdrag0n.android12ext.ui.settings.tweaks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.kdrag0n.android12ext.ui.settings.BaseSettingsFragment

@AndroidEntryPoint
class TweakSettingsFragment : BaseSettingsFragment() {
    private val viewModel: TweakSettingsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel(viewModel)
    }
}
