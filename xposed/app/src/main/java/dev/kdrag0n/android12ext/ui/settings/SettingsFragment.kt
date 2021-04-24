package dev.kdrag0n.android12ext.ui.settings

import android.os.Bundle
import android.view.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dev.chrisbanes.insetter.applyInsetter
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.ui.BaseFragment
import dev.kdrag0n.android12ext.ui.observeNav
import dev.kdrag0n.android12ext.ui.utils.NoSwipeBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : BaseFragment() {
    private val viewModel: SettingsViewModel by viewModel()

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

            applyInsetter {
                type(navigationBars = true) {
                    padding()
                }
            }
        }

        viewModel.showReloadWarning.observe(viewLifecycleOwner) { shouldReload ->
            reloadSnackbar?.dismiss()
            reloadSnackbar = null

            if (shouldReload) {
                reloadSnackbar = Snackbar.make(
                        view,
                        R.string.applying_changes,
                        // We take care of showing and dismissing it
                        BaseTransientBottomBar.LENGTH_INDEFINITE
                ).apply {
                    setAction(R.string.cancel) {
                        viewModel.showReloadWarning.value = false
                    }
                    behavior = NoSwipeBehavior()
                    show()
                }
            }
        }

        viewModel.navDest.observeNav(this)
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
                findNavController().navigate(R.id.action_settings_root_to_about)
                true
            }
            else -> false
        }
    }
}