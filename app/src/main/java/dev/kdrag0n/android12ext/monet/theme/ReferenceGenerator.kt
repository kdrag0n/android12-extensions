package dev.kdrag0n.android12ext.monet.theme

import android.content.Context
import dagger.Reusable
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.kdrag0n.android12ext.data.SettingsRepository
import dev.kdrag0n.colorkt.cam.Zcam.Companion.toZcam
import dev.kdrag0n.colorkt.conversion.ConversionGraph.convert
import dev.kdrag0n.colorkt.rgb.LinearSrgb.Companion.toLinear
import dev.kdrag0n.colorkt.rgb.Srgb
import dev.kdrag0n.colorkt.tristimulus.CieXyz.Companion.toXyz
import dev.kdrag0n.colorkt.tristimulus.CieXyzAbs.Companion.toAbs
import dev.kdrag0n.monet.theme.ColorScheme
import timber.log.Timber
import javax.inject.Inject

@Reusable
class ReferenceGenerator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepo: SettingsRepository,
) {
    private fun emitCodeLine(line: String) {
        Timber.i("[CODE]$line")
    }

    fun generateTable() {
        val cond = ColorSchemeFactory.createZcamViewingConditions(SRGB_WHITE_LUMINANCE)

        emitCodeLine("    object NewColors : ColorScheme() {")

        COLOR_MAPS.map { (group, ids) ->
            emitCodeLine("        override val $group = mapOf(")

            ids.map { (shade, resId) ->
                val hex = context.getColor(resId)
                val srgb = Srgb(hex)
                val zcam = srgb.toLinear().toXyz().toAbs(cond.referenceWhite.y).toZcam(cond)

                Timber.i("$group $shade = $zcam")

                // Remove alpha channel
                val hexRgb = hex and 0xffffff
                emitCodeLine(String.format("            %-4d to Srgb(0x%06x),", shade, hexRgb))
            }

            emitCodeLine("        )")
            emitCodeLine("")
        }

        emitCodeLine("    }")
    }

    fun generateDynamicXml(): String {
        val isDynamic = settingsRepo.prefs.getBoolean("generate_palette_dynamic", false)

        val seedColor = if (isDynamic) {
            settingsRepo.prefs.getInt("monet_custom_color_value", android.graphics.Color.BLUE)
        } else {
            0
        }

        val scheme = if (isDynamic) {
            ColorSchemeFactory.getFactory(settingsRepo.prefs)
                .getColor(Srgb(seedColor))
        } else {
            SystemColorScheme(context)
        }

        return mapColorScheme(scheme).map { (group, colors) ->
            val groupDesc = when (group) {
                "accent1" -> "accent"
                "accent2" -> "secondary accent"
                "accent3" -> "tertiary accent"
                "neutral1" -> "neutral"
                "neutral2" -> "secondary neutral"
                else -> error("Invalid group $group")
            }

            colors.filterKeys { it != 950 && it != 650 && it != 20 }.map { (shade, color) ->
                val hex = color.convert<Srgb>().toHex()
                val shadeDesc = when (shade) {
                    0 -> "Lightest shade of the $groupDesc color used by the system. White."
                    500 -> "Shade of the $groupDesc system color at 49% lightness."
                    1000 -> "Darkest shade of the $groupDesc color used by the system. Black."
                    else -> "Shade of the $groupDesc system color at ${(1000 - shade) / 10}% lightness."
                }

                """    <!-- $shadeDesc
     This value can be overlaid at runtime by OverlayManager RROs. -->
    <color name="system_${group}_$shade">$hex</color>"""
            }.joinToString("\n")
        }.joinToString("\n\n")
    }

    companion object {
        private const val SRGB_WHITE_LUMINANCE = 200.0 // cd/m^2

        val COLOR_MAPS = mapOf(
            "accent1" to SystemColorScheme.ACCENT1_RES,
            "accent2" to SystemColorScheme.ACCENT2_RES,
            "accent3" to SystemColorScheme.ACCENT3_RES,
            "neutral1" to SystemColorScheme.NEUTRAL1_RES,
            "neutral2" to SystemColorScheme.NEUTRAL2_RES,
        )

        private fun mapColorScheme(colors: ColorScheme) = mapOf(
            "accent1" to colors.accent1,
            "accent2" to colors.accent2,
            "accent3" to colors.accent3,
            "neutral1" to colors.neutral1,
            "neutral2" to colors.neutral2,
        )
    }
}
