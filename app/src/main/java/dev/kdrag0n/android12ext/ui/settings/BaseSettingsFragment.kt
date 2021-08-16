package dev.kdrag0n.android12ext.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dev.chrisbanes.insetter.applyInsetter
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.databinding.ContentSettingsBinding
import dev.kdrag0n.android12ext.ui.BaseToolbarFragment

abstract class BaseSettingsFragment : BaseToolbarFragment() {
    private var _binding: ContentSettingsBinding? = null
    private val settingsBinding get() = _binding!!

    override fun onCreateContentView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ContentSettingsBinding.inflate(inflater, container, false)
        return settingsBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        settingsBinding.preferencesView.adapter = null
        _binding = null
    }

    protected fun initViewModel(viewModel: BaseSettingsViewModel) {
        settingsBinding.preferencesView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = viewModel.prefAdapter
            viewModel.prefAdapter.restoreAndObserveScrollPosition(this)

            applyInsetter {
                type(navigationBars = true) {
                    padding()
                }
            }
        }

        viewModel.settingsReportStatus.observe(viewLifecycleOwner) { success ->
            if (success != null) {
                viewModel.settingsReportStatus.value = null

                val msg = if (success) R.string.telemetry_settings_report_success else R.string.telemetry_settings_report_error
                Snackbar.make(view ?: return@observe, msg, Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}
