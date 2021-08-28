package dev.kdrag0n.android12ext.ui.settings.root

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.data.SettingsRepository
import dev.kdrag0n.android12ext.ui.observeNav
import dev.kdrag0n.android12ext.ui.settings.BaseSettingsFragment
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : BaseSettingsFragment(), Toolbar.OnMenuItemClickListener {
    private val viewModel: SettingsViewModel by viewModels()

    @Inject lateinit var settingsRepo: SettingsRepository

    private var telemetryDialog: AlertDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel(viewModel)
        viewModel.navDest.observeNav(this)

        toolbarBinding.toolbar.inflateMenu(R.menu.menu_settings)
        toolbarBinding.toolbar.setOnMenuItemClickListener(this)

        viewModel.showTelemetryPrompt.observe(viewLifecycleOwner) { showPrompt ->
            telemetryDialog?.dismiss()
            telemetryDialog = null

            if (showPrompt) {
                telemetryDialog = MaterialAlertDialogBuilder(context ?: return@observe).run {
                    setTitle(R.string.telemetry_prompt_send_settings_report)
                    setMessage(R.string.telemetry_prompt_send_settings_report_desc)

                    setNegativeButton(R.string.deny) { _, _ ->
                        viewModel.setTelemetryConsent(false)
                    }
                    setPositiveButton(R.string.allow) { _, _ ->
                        viewModel.setTelemetryConsent(true)
                    }
                    show()
                }
            }
        }
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
        R.id.action_report_settings -> {
            lifecycleScope.launch {
                settingsRepo.reportSettings()
            }
            true
        }
        else -> false
    }
}
