package dev.kdrag0n.android12ext.monet.overlay

import android.app.WallpaperColors
import android.graphics.Color
import dev.kdrag0n.android12ext.monet.colors.Srgb
import dev.kdrag0n.android12ext.monet.theme.ColorScheme
import dev.kdrag0n.android12ext.monet.theme.DynamicColorScheme
import timber.log.Timber

class ThemeOverlayController(
    private val targetColors: ColorScheme,
    private val boostAccentChroma: Boolean,
    private val multiColor: Boolean,
) {
    private lateinit var colorScheme: DynamicColorScheme

    fun getNeutralColor(colors: WallpaperColors) = colors.primaryColor.toArgb()
    fun getAccentColor(colors: WallpaperColors) = if (multiColor) {
        // Not all colors will always be present
        (colors.secondaryColor
            ?: colors.tertiaryColor
            ?: colors.primaryColor).toArgb()
    } else {
        getNeutralColor(colors)
    }

    // com.android.systemui.theme.ThemeOverlayController#getOverlay(int, int)
    fun getOverlay(primaryColor: Int, isAccent: Int): Any {
        // Generate color scheme
        colorScheme = DynamicColorScheme(targetColors, primaryColor, boostAccentChroma)

        val groupKey = when (isAccent) {
            1 -> "accent"
            else -> "neutral"
        }

        return FabricatedOverlay.Builder("com.android.systemui", groupKey, "android").run {
            val colorsList = when (isAccent) {
                1 -> colorScheme.accentColors
                else -> colorScheme.neutralColors
            }

            colorsList.withIndex().forEach { listEntry ->
                val group = "$groupKey${listEntry.index + 1}"

                listEntry.value.forEach { (shade, color) ->
                    val colorSrgb = color as? Srgb
                        ?: color.toLinearSrgb().toSrgb()
                    val colorRgb8 = colorSrgb.quantize8()
                    Timber.d("Color $group $shade = ${String.format("%06x", colorRgb8)}")

                    // Set alpha to 255
                    val argbColor = Color.argb(255, 0, 0, 0) or colorRgb8

                    val resKey = "android:color/system_${group}_$shade"
                    setResourceValue(resKey, FabricatedOverlay.DATA_TYPE_COLOR, argbColor)
                }
            }

            build()
        }
    }
}