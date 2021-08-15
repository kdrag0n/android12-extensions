package dev.kdrag0n.android12ext.xposed.hooks

import android.content.SharedPreferences
import dev.kdrag0n.android12ext.core.data.SettingsRepository
import dev.kdrag0n.android12ext.monet.colors.CieLab
import dev.kdrag0n.android12ext.monet.colors.Color
import dev.kdrag0n.android12ext.monet.colors.Illuminants
import dev.kdrag0n.android12ext.monet.colors.Zcam
import dev.kdrag0n.android12ext.monet.theme.*

interface ColorSchemeFactory {
    fun getColor(color: Color): ColorScheme

    companion object {
        fun getFactory(
            useZcam: Boolean,
            // For all models
            chromaFactor: Double,
            accurateShades: Boolean,
            // ZCAM only
            whiteLuminance: Double,
            useLinearLightness: Boolean,
        ) = if (useZcam) {
            val cond = createZcamViewingConditions(whiteLuminance)

            object : ColorSchemeFactory {
                override fun getColor(color: Color) = ZcamDynamicColorScheme(
                    targets = ZcamMaterialYouTargets(
                        chromaFactor = chromaFactor,
                        useLinearLightness = useLinearLightness,
                        cond = cond,
                    ),
                    seedColor = color,
                    chromaFactor = chromaFactor,
                    cond = cond,
                    accurateShades = accurateShades,
                )
            }
        } else {
            object : ColorSchemeFactory {
                override fun getColor(color: Color) = DynamicColorScheme(
                    targets = MaterialYouTargets(chromaFactor),
                    seedColor = color,
                    chromaFactor = chromaFactor,
                    accurateShades = accurateShades,
                )
            }
        }

        fun getFactory(prefs: SharedPreferences) = getFactory(
            useZcam = prefs.getBoolean("custom_monet_zcam_enabled", true),
            chromaFactor = prefs.getInt("custom_monet_chroma_multiplier", 50).toDouble() / 50,
            accurateShades = prefs.getBoolean("custom_monet_accurate_shades_enabled", true),
            whiteLuminance = SettingsRepository.getWhiteLuminance(prefs),
            useLinearLightness = prefs.getBoolean("custom_monet_zcam_linear_lightness_enabled", false),
        )

        fun createZcamViewingConditions(whiteLuminance: Double) = Zcam.ViewingConditions(
            surroundFactor = Zcam.ViewingConditions.SURROUND_AVERAGE,
            // sRGB
            L_a = 0.4 * whiteLuminance,
            // Gray world
            Y_b = CieLab(
                L = 50.0,
                a = 0.0,
                b = 0.0,
            ).toCieXyz().y * whiteLuminance,
            referenceWhite = Illuminants.D65 * whiteLuminance,
            whiteLuminance = whiteLuminance,
        )
    }
}
