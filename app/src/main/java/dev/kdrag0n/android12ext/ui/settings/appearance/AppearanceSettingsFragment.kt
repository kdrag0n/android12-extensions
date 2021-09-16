package dev.kdrag0n.android12ext.ui.settings.appearance

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import dagger.hilt.android.AndroidEntryPoint
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.data.SettingsRepository
import dev.kdrag0n.android12ext.ui.monet.palette.PaletteActivity
import dev.kdrag0n.android12ext.ui.observeNav
import dev.kdrag0n.android12ext.ui.settings.BaseSettingsFragment
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AppearanceSettingsFragment : BaseSettingsFragment() {
    private val viewModel: AppearanceSettingsViewModel by viewModels()
    private val colorDialogViewModel: ColorDialogViewModel by activityViewModels()

    @Inject lateinit var settingsRepo: SettingsRepository
    private lateinit var paletteRenderer: AutoPaletteRenderer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel(viewModel)
        viewModel.navDest.observeNav(this)

        paletteRenderer = AutoPaletteRenderer(this, settingsRepo)

        viewModel.openColorPicker.observe(viewLifecycleOwner) { event ->
            if (event != null) {
                viewModel.openColorPicker.value = null

                val (dialogId, color) = event
                ColorPickerDialog.newBuilder().run {
                    setColor(color)
                    setDialogId(dialogId)
                    setDialogTitle(R.string.appearance_monet_custom_color_value)
                    show(activity)
                }
            }
        }

        viewModel.openPalette.observe(viewLifecycleOwner) { event ->
            if (event != null) {
                viewModel.openPalette.value = null
                startActivity(Intent(requireContext(), PaletteActivity::class.java))
            }
        }

        viewModel.renderPalettes.observe(viewLifecycleOwner) {event ->
            if (event != null) {
                viewModel.viewModelScope.launch {
                    paletteRenderer.doAllColors()
                }

                viewModel.renderPalettes.value = null
            }
        }

        colorDialogViewModel.selectedColor.observe(viewLifecycleOwner) { color ->
            viewModel.selectedColor.value = color
        }
    }
}
