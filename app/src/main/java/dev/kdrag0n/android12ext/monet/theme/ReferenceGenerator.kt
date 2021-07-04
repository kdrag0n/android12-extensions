package dev.kdrag0n.android12ext.monet.theme

import android.content.Context
import dagger.Reusable
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.kdrag0n.android12ext.monet.colors.Oklab.Companion.toOklab
import dev.kdrag0n.android12ext.monet.colors.Oklch.Companion.toOklch
import dev.kdrag0n.android12ext.monet.colors.Srgb
import timber.log.Timber
import javax.inject.Inject

@Reusable
class ReferenceGenerator @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private fun emitCodeLine(line: String) {
        Timber.i("[CODE]$line")
    }

    fun generateTable() {
        emitCodeLine("    object NewColors : ColorScheme() {")

        COLOR_MAPS.map { (group, ids) ->
            emitCodeLine("        override val $group = mapOf(")

            ids.map { (shade, resId) ->
                val hex = context.getColor(resId)
                val srgb = Srgb(hex)
                val oklch = srgb.toLinearSrgb().toOklab().toOklch()

                Timber.i("$group $shade = $oklch")

                // Remove alpha channel
                val hexRgb = hex and 0xffffff
                emitCodeLine(String.format("            %-4d to Srgb(0x%06x),", shade, hexRgb))
            }

            emitCodeLine("        )")
            emitCodeLine("")
        }

        emitCodeLine("    }")
    }

    companion object {
        val COLOR_MAPS = mapOf(
            "accent1" to SystemColorScheme.ACCENT1_RES,
            "accent2" to SystemColorScheme.ACCENT2_RES,
            "accent3" to SystemColorScheme.ACCENT3_RES,
            "neutral1" to SystemColorScheme.NEUTRAL1_RES,
            "neutral2" to SystemColorScheme.NEUTRAL2_RES,
        )
    }
}
