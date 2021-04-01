package dev.kdrag0n.android12ext.ui.settings

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.ui.BaseFragment
import dev.kdrag0n.android12ext.ui.NoSwipeBehavior

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