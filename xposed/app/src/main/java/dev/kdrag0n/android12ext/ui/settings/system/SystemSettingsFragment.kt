package dev.kdrag0n.android12ext.ui.settings.system

import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dev.chrisbanes.insetter.applyInsetter
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.ui.BaseFragment
import dev.kdrag0n.android12ext.ui.utils.NoSwipeBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel

class SystemSettingsFragment : BaseFragment() {
    private val viewModel: SystemSettingsViewModel by viewModel()

    private var reloadSnackbar: Snackbar? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
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
    }
}