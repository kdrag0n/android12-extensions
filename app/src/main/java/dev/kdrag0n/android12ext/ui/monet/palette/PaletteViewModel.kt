package dev.kdrag0n.android12ext.ui.monet.palette

import androidx.lifecycle.ViewModel
import dev.kdrag0n.android12ext.core.data.SettingsRepository
import dev.kdrag0n.android12ext.monet.colors.Srgb
import dev.kdrag0n.android12ext.monet.theme.DynamicColorScheme
import dev.kdrag0n.android12ext.monet.theme.MaterialYouTargets

class PaletteViewModel(
    settingsRepo: SettingsRepository,
) : ViewModel() {
    private val scheme = DynamicColorScheme(
        targets = MaterialYouTargets(),
        seedColor = Srgb(settingsRepo.prefs.getInt("monet_custom_color_value", android.graphics.Color.BLUE)),
    )

    val colors = mapOf(
        "accent1" to scheme.accent1,
        "accent2" to scheme.accent2,
        "accent3" to scheme.accent3,
        "neutral1" to scheme.neutral1,
        "neutral2" to scheme.neutral2,
    )
}
