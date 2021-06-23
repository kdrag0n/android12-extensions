package dev.kdrag0n.android12ext.ui.settings.root

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.ui.observeNav
import dev.kdrag0n.android12ext.ui.settings.BaseSettingsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : BaseSettingsFragment(), Toolbar.OnMenuItemClickListener {
    private val viewModel: SettingsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel(viewModel)
        viewModel.navDest.observeNav(this)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.inflateMenu(R.menu.menu_settings)
        toolbar.setOnMenuItemClickListener(this)
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
