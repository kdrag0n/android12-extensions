package dev.kdrag0n.android12ext.ui.settings.appearance

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.ui.monet.palette.PaletteActivity
import dev.kdrag0n.android12ext.ui.observeNav
import dev.kdrag0n.android12ext.ui.settings.BaseSettingsFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AppearanceSettingsFragment : BaseSettingsFragment() {
    private val viewModel: AppearanceSettingsViewModel by viewModel()
    private val colorDialogViewModel: ColorDialogViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel(viewModel)
        viewModel.navDest.observeNav(this)

        viewModel.openColorPicker.observe(viewLifecycleOwner) { color ->
            if (color != null) {
                viewModel.openColorPicker.value = null

                ColorPickerDialog.newBuilder().run {
                    if (color != -1) {
                        setColor(color)
                    }

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

        colorDialogViewModel.selectedColor.observe(viewLifecycleOwner) { color ->
            viewModel.selectedColor.value = color
        }
    }
}
