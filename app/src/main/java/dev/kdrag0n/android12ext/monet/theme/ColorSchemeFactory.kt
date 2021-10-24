package dev.kdrag0n.android12ext.monet.theme

import android.content.SharedPreferences
import dev.kdrag0n.android12ext.data.SettingsRepository
import dev.kdrag0n.colorkt.Color
import dev.kdrag0n.colorkt.cam.Zcam
import dev.kdrag0n.colorkt.data.Illuminants
import dev.kdrag0n.colorkt.rgb.Srgb
import dev.kdrag0n.colorkt.tristimulus.CieXyzAbs.Companion.toAbs
import dev.kdrag0n.colorkt.ucs.lab.CieLab
import dev.kdrag0n.monet.theme.ColorScheme
import dev.kdrag0n.monet.theme.DynamicColorScheme
import dev.kdrag0n.monet.theme.MaterialYouTargets

interface ColorSchemeFactory {
    fun getColor(color: Color): ColorScheme

    companion object {
        fun getFactory(
            // For all models
            chromaFactor: Double,
            accurateShades: Boolean,
            useComplementColor: Boolean,
            complementColorHex: Int,
            // ZCAM only
            whiteLuminance: Double,
            useLinearLightness: Boolean,
        ) = object : ColorSchemeFactory {
            private val cond = createZcamViewingConditions(whiteLuminance)
            private val complementColor = if (!useComplementColor || complementColorHex == 0) {
                null
            } else {
                Srgb(complementColorHex)
            }

            override fun getColor(color: Color) = DynamicColorScheme(
                targets = MaterialYouTargets(
                    chromaFactor = chromaFactor,
                    useLinearLightness = useLinearLightness,
                    cond = cond,
                ),
                seedColor = color,
                chromaFactor = chromaFactor,
                cond = cond,
                accurateShades = accurateShades,
                complementColor = complementColor,
            )
        }

        fun getFactory(prefs: SharedPreferences) = getFactory(
            chromaFactor = prefs.getInt("custom_monet_chroma_multiplier", 50).toDouble() / 50,
            accurateShades = prefs.getBoolean("custom_monet_accurate_shades_enabled", true),
            useComplementColor = prefs.getBoolean("monet_custom_color3_enabled", false),
            complementColorHex = prefs.getInt("monet_custom_color3_value", 0),
            whiteLuminance = SettingsRepository.getWhiteLuminance(prefs),
            useLinearLightness = prefs.getBoolean("custom_monet_zcam_linear_lightness_enabled", false),
        )

        fun createZcamViewingConditions(whiteLuminance: Double) = Zcam.ViewingConditions(
            surroundFactor = Zcam.ViewingConditions.SURROUND_AVERAGE,
            // sRGB
            adaptingLuminance = 0.4 * whiteLuminance,
            // Gray world
            backgroundLuminance = CieLab(
                L = 50.0,
                a = 0.0,
                b = 0.0,
            ).toXyz().y * whiteLuminance,
            referenceWhite = Illuminants.D65.toAbs(whiteLuminance),
        )
    }
}
