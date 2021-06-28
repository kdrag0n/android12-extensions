package dev.kdrag0n.android12ext.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.chrisbanes.insetter.applyInsetter
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.ui.BaseToolbarFragment

abstract class BaseSettingsFragment : BaseToolbarFragment() {
    override fun onCreateContentView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.content_settings, container, false)
    }

    protected fun initViewModel(viewModel: BaseSettingsViewModel) {
        requireView().findViewById<RecyclerView>(R.id.preferences_view).apply {
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
