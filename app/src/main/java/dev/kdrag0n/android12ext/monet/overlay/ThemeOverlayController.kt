package dev.kdrag0n.android12ext.monet.overlay

import android.app.WallpaperColors
import android.content.Context
import androidx.annotation.ColorInt
import dev.kdrag0n.android12ext.monet.theme.ColorSchemeFactory
import dev.kdrag0n.colorkt.Color
import dev.kdrag0n.colorkt.conversion.ConversionGraph.convert
import dev.kdrag0n.colorkt.rgb.Srgb
import timber.log.Timber

class ThemeOverlayController(
    private val context: Context,
    private val colorSchemeFactory: ColorSchemeFactory,
) {
    fun getNeutralColor(colors: WallpaperColors) = colors.getSeedColor(context)
    fun getAccentColor(colors: WallpaperColors) = getNeutralColor(colors)

    // com.android.systemui.theme.ThemeOverlayController#getOverlay(int, int)
    fun getOverlay(primaryColor: Int, type: Int): Any {
        // Generate color scheme
        val colorScheme = colorSchemeFactory.getColor(Srgb(primaryColor))

        val (groupKey, colorsList) = when (type) {
            TYPE_NEUTRAL -> "neutral" to colorScheme.neutralColors
            TYPE_ACCENT -> "accent" to colorScheme.accentColors
            else -> error("Unknown type $type")
        }

        return FabricatedOverlay.Builder("com.android.systemui", groupKey, "android").run {
            colorsList.withIndex().forEach { listEntry ->
                val group = "$groupKey${listEntry.index + 1}"

                listEntry.value.forEach { (shade, color) ->
                    val colorSrgb = color.convert<Srgb>()
                    Timber.d("Color $group $shade = ${String.format("%06x", colorSrgb.toRgb8())}")

                    setColor("system_${group}_$shade", colorSrgb)
                }
            }

            // Override special modulated surface colors
            if (type == TYPE_NEUTRAL) {
                // surface light = neutral1 20 (L* 98)
                colorsList[0][20]?.let { setColor("surface_light", it) }

                // surface highlight dark = neutral1 650 (L* 35)
                colorsList[0][650]?.let { setColor("surface_highlight_dark", it) }
            }

            build()
        }
    }

    companion object {
        private const val TYPE_NEUTRAL = 0
        private const val TYPE_ACCENT = 1

        private fun FabricatedOverlay.Builder.setColor(name: String, @ColorInt color: Int) =
            setResourceValue("android:color/$name", FabricatedOverlay.DATA_TYPE_COLOR, color)

        private fun FabricatedOverlay.Builder.setColor(name: String, color: Color): FabricatedOverlay.Builder {
            val rgb = color.convert<Srgb>().toRgb8()
            val argb = rgb or (0xff shl 24)
            return setColor(name, argb)
        }
    }
}
