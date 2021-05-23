package dev.kdrag0n.android12ext.ui.settings.root

import android.os.Bundle
import android.view.*
import androidx.navigation.fragment.findNavController
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.ui.observeNav
import dev.kdrag0n.android12ext.ui.settings.BaseSettingsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : BaseSettingsFragment() {
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel(viewModel)
        viewModel.navDest.observeNav(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_settings, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item) || when (item.itemId) {
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
}