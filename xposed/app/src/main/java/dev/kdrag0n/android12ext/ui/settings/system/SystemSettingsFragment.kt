package dev.kdrag0n.android12ext.ui.settings.system

import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.chrisbanes.insetter.applyInsetter
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.ui.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SystemSettingsFragment : BaseFragment() {
    private val viewModel: SystemSettingsViewModel by viewModel()

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
    }
}