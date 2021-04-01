package dev.kdrag0n.android12ext.ui.settings

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.ui.BaseFragment
import dev.kdrag0n.android12ext.ui.NoSwipeBehavior

private const val XPOSED_MANAGER_PACKAGE = "org.lsposed.manager"

class SettingsFragment : BaseFragment() {
    private val viewModel: SettingsViewModel by viewModels()

    private var reloadSnackbar: Snackbar? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<RecyclerView>(R.id.preferences_view).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = viewModel.prefAdapter

            viewModel.prefAdapter.restoreAndObserveScrollPosition(this)
        }

        viewModel.requestReload.observe(viewLifecycleOwner) { shouldReload ->
            if (shouldReload) {
                reloadSnackbar = Snackbar.make(
                    view,
                    R.string.applying_changes,
                    // We take care of showing and dismissing it
                    BaseTransientBottomBar.LENGTH_INDEFINITE
                ).apply {
                    setAction(R.string.cancel) {
                        viewModel.requestReload.value = false
                    }
                    behavior = NoSwipeBehavior()
                    show()
                }
            } else {
                reloadSnackbar?.dismiss()
                reloadSnackbar = null
            }
        }

        if (!viewModel.isXposedHooked) {
            MaterialAlertDialogBuilder(requireContext()).run {
                setTitle(R.string.error_xposed_module_missing)
                setMessage(R.string.error_xposed_module_missing_desc)
                setCancelable(false)
                // Empty callback because we override it later
                setPositiveButton(R.string.enable) { _, _ -> }
                show()
            }.apply {
                // Override button callback to stop it from dismissing the dialog
                getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    val intent = requireContext().packageManager.getLaunchIntentForPackage(XPOSED_MANAGER_PACKAGE)
                    if (intent == null) {
                        Toast.makeText(requireContext(), R.string.error_xposed_manager_not_installed, Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        startActivity(intent)
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_settings, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item) || when (item.itemId) {
            R.id.action_force_reload -> {
                viewModel.broadcastReload()
                true
            }
            R.id.action_about -> {
                findNavController().navigate(R.id.action_settings_to_about)
                true
            }
            else -> false
        }
    }
}