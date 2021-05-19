package dev.kdrag0n.android12ext.core.monet.theme

import android.content.Context
import dev.kdrag0n.android12ext.core.monet.colors.LinearSrgb.Companion.toLinearSrgb
import dev.kdrag0n.android12ext.core.monet.colors.Oklab.Companion.toOklab
import dev.kdrag0n.android12ext.core.monet.colors.Oklch.Companion.toOklch
import dev.kdrag0n.android12ext.core.monet.colors.Srgb
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
            emitCodeLine("        override val $group = listOf(")

            ids.withIndex().map {
                val id = it.value
                val level = it.index * 100

                val hex = context.getColor(id)
                val srgb = Srgb(hex)
                val oklch = srgb.toLinearSrgb().toOklab().toOklch()

                Timber.i("$group $level = $oklch")

                // Remove alpha channel
                val hexRgb = hex and 0xffffff
                emitCodeLine("            0x${String.format("%06x", hexRgb)},")
            }

            emitCodeLine("        )")
            emitCodeLine("")
        }

        emitCodeLine("    }")
    }

    companion object {
        private val neutral1: List<Int> = listOf(
            android.R.color.system_neutral1_0,
            android.R.color.system_neutral1_100,
            android.R.color.system_neutral1_200,
            android.R.color.system_neutral1_300,
            android.R.color.system_neutral1_400,
            android.R.color.system_neutral1_500,
            android.R.color.system_neutral1_600,
            android.R.color.system_neutral1_700,
            android.R.color.system_neutral1_800,
            android.R.color.system_neutral1_900,
            android.R.color.system_neutral1_1000,
        )

        private val neutral2: List<Int> = listOf(
            android.R.color.system_neutral2_0,
            android.R.color.system_neutral2_100,
            android.R.color.system_neutral2_200,
            android.R.color.system_neutral2_300,
            android.R.color.system_neutral2_400,
            android.R.color.system_neutral2_500,
            android.R.color.system_neutral2_600,
            android.R.color.system_neutral2_700,
            android.R.color.system_neutral2_800,
            android.R.color.system_neutral2_900,
            android.R.color.system_neutral2_1000,
        )

        private val accent1: List<Int> = listOf(
            android.R.color.system_accent1_0,
            android.R.color.system_accent1_100,
            android.R.color.system_accent1_200,
            android.R.color.system_accent1_300,
            android.R.color.system_accent1_400,
            android.R.color.system_accent1_500,
            android.R.color.system_accent1_600,
            android.R.color.system_accent1_700,
            android.R.color.system_accent1_800,
            android.R.color.system_accent1_900,
            android.R.color.system_accent1_1000,
        )

        private val accent2: List<Int> = listOf(
            android.R.color.system_accent2_0,
            android.R.color.system_accent2_100,
            android.R.color.system_accent2_200,
            android.R.color.system_accent2_300,
            android.R.color.system_accent2_400,
            android.R.color.system_accent2_500,
            android.R.color.system_accent2_600,
            android.R.color.system_accent2_700,
            android.R.color.system_accent2_800,
            android.R.color.system_accent2_900,
            android.R.color.system_accent2_1000,
        )

        private val accent3: List<Int> = listOf(
            android.R.color.system_accent3_0,
            android.R.color.system_accent3_100,
            android.R.color.system_accent3_200,
            android.R.color.system_accent3_300,
            android.R.color.system_accent3_400,
            android.R.color.system_accent3_500,
            android.R.color.system_accent3_600,
            android.R.color.system_accent3_700,
            android.R.color.system_accent3_800,
            android.R.color.system_accent3_900,
            android.R.color.system_accent3_1000,
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