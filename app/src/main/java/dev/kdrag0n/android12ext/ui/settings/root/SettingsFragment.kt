package dev.kdrag0n.android12ext.ui.settings.root

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.ui.observeNav
import dev.kdrag0n.android12ext.ui.settings.BaseSettingsFragment

@AndroidEntryPoint
class SettingsFragment : BaseSettingsFragment(), Toolbar.OnMenuItemClickListener {
    private val viewModel: SettingsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel(viewModel)
        viewModel.navDest.observeNav(this)

        toolbarBinding.toolbar.inflateMenu(R.menu.menu_settings)
        toolbarBinding.toolbar.setOnMenuItemClickListener(this)
    }

    override fun onMenuItemClick(item: MenuItem) = when (item.itemId) {
        R.id.action_force_reload -> {
            viewModel.reload()
            true
        }
        R.id.action_about -> {
            findNavController().navigate(R.id.action_settings_root_to_about)
            true
        }
        else -> false
    }
}
