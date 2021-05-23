package dev.kdrag0n.android12ext.monet.theme

import android.content.Context
import dev.kdrag0n.android12ext.monet.colors.Oklab.Companion.toOklab
import dev.kdrag0n.android12ext.monet.colors.Oklch.Companion.toOklch
import dev.kdrag0n.android12ext.monet.colors.Srgb
import timber.log.Timber

class ReferenceGenerator(
    val context: Context,
) {
    private fun emitCodeLine(line: String) {
        Timber.i("[CODE]$line")
    }

    fun generateTable() {
        emitCodeLine("    object NewColors : ColorScheme() {")

        colorLists.map { (group, ids) ->
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
        private val neutral1: Map<Int, Int> = mapOf(
            0    to android.R.color.system_neutral1_0,
            50   to android.R.color.system_neutral1_50,
            100  to android.R.color.system_neutral1_100,
            200  to android.R.color.system_neutral1_200,
            300  to android.R.color.system_neutral1_300,
            400  to android.R.color.system_neutral1_400,
            500  to android.R.color.system_neutral1_500,
            600  to android.R.color.system_neutral1_600,
            700  to android.R.color.system_neutral1_700,
            800  to android.R.color.system_neutral1_800,
            900  to android.R.color.system_neutral1_900,
            1000 to android.R.color.system_neutral1_1000,
        )

        private val neutral2: Map<Int, Int> = mapOf(
            0    to android.R.color.system_neutral2_0,
            50   to android.R.color.system_neutral2_50,
            100  to android.R.color.system_neutral2_100,
            200  to android.R.color.system_neutral2_200,
            300  to android.R.color.system_neutral2_300,
            400  to android.R.color.system_neutral2_400,
            500  to android.R.color.system_neutral2_500,
            600  to android.R.color.system_neutral2_600,
            700  to android.R.color.system_neutral2_700,
            800  to android.R.color.system_neutral2_800,
            900  to android.R.color.system_neutral2_900,
            1000 to android.R.color.system_neutral2_1000,
        )

        private val accent1: Map<Int, Int> = mapOf(
            0    to android.R.color.system_accent1_0,
            50   to android.R.color.system_accent1_50,
            100  to android.R.color.system_accent1_100,
            200  to android.R.color.system_accent1_200,
            300  to android.R.color.system_accent1_300,
            400  to android.R.color.system_accent1_400,
            500  to android.R.color.system_accent1_500,
            600  to android.R.color.system_accent1_600,
            700  to android.R.color.system_accent1_700,
            800  to android.R.color.system_accent1_800,
            900  to android.R.color.system_accent1_900,
            1000 to android.R.color.system_accent1_1000,
        )

        private val accent2: Map<Int, Int> = mapOf(
            0    to android.R.color.system_accent2_0,
            50   to android.R.color.system_accent2_50,
            100  to android.R.color.system_accent2_100,
            200  to android.R.color.system_accent2_200,
            300  to android.R.color.system_accent2_300,
            400  to android.R.color.system_accent2_400,
            500  to android.R.color.system_accent2_500,
            600  to android.R.color.system_accent2_600,
            700  to android.R.color.system_accent2_700,
            800  to android.R.color.system_accent2_800,
            900  to android.R.color.system_accent2_900,
            1000 to android.R.color.system_accent2_1000,
        )

        private val accent3: Map<Int, Int> = mapOf(
            0    to android.R.color.system_accent3_0,
            50   to android.R.color.system_accent3_50,
            100  to android.R.color.system_accent3_100,
            200  to android.R.color.system_accent3_200,
            300  to android.R.color.system_accent3_300,
            400  to android.R.color.system_accent3_400,
            500  to android.R.color.system_accent3_500,
            600  to android.R.color.system_accent3_600,
            700  to android.R.color.system_accent3_700,
            800  to android.R.color.system_accent3_800,
            900  to android.R.color.system_accent3_900,
            1000 to android.R.color.system_accent3_1000,
        )

        private val colorLists = mapOf(
            "neutral1" to neutral1,
            "neutral2" to neutral2,
            "accent1" to accent1,
            "accent2" to accent2,
            "accent3" to accent3,
        )
    }
}
