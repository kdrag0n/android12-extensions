package dev.kdrag0n.android12ext.ui.monet.palette

import androidx.lifecycle.ViewModel
import dev.kdrag0n.android12ext.monet.theme.ReferenceGenerator

class PaletteViewModel : ViewModel() {
    val colors = ReferenceGenerator.COLOR_MAPS
}
