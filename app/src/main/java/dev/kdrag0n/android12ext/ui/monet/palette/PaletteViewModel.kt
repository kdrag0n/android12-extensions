package dev.kdrag0n.android12ext.ui.monet.palette

import android.content.Context
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.kdrag0n.android12ext.data.SettingsRepository
import dev.kdrag0n.android12ext.monet.theme.SystemColorScheme
import dev.kdrag0n.android12ext.monet.theme.ColorSchemeFactory
import dev.kdrag0n.colorkt.rgb.Srgb
import javax.inject.Inject

@HiltViewModel
@RequiresApi(31)
class PaletteViewModel @Inject constructor(
    @ApplicationContext context: Context,
    settingsRepo: SettingsRepository,
) : ViewModel() {
    private val isDynamic = settingsRepo.prefs.getBoolean("generate_palette_dynamic", false)

    val seedColor = if (isDynamic) {
        settingsRepo.prefs.getInt("monet_custom_color_value", android.graphics.Color.BLUE)
    } else {
        0
    }

    private val scheme = if (isDynamic) {
        ColorSchemeFactory.getFactory(settingsRepo.prefs)
            .getColor(Srgb(seedColor))
    } else {
        SystemColorScheme(context)
    }

    val colors = mapOf(
        "accent1" to scheme.accent1,
        "accent2" to scheme.accent2,
        "accent3" to scheme.accent3,
        "neutral1" to scheme.neutral1,
        "neutral2" to scheme.neutral2,
    )
}
