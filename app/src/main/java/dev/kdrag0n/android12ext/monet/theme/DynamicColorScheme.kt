package dev.kdrag0n.android12ext.monet.theme

import dev.kdrag0n.android12ext.monet.colors.CieLab.Companion.toCieLab
import dev.kdrag0n.android12ext.monet.colors.CieXyz.Companion.toCieXyz
import dev.kdrag0n.android12ext.monet.colors.Color
import dev.kdrag0n.android12ext.monet.colors.Lch
import dev.kdrag0n.android12ext.monet.colors.Oklab.Companion.toOklab
import dev.kdrag0n.android12ext.monet.colors.Oklch
import dev.kdrag0n.android12ext.monet.colors.Oklch.Companion.toOklch
import dev.kdrag0n.android12ext.monet.colors.Srgb
import timber.log.Timber
import kotlin.math.abs

class DynamicColorScheme(
    targetColors: ColorScheme,
    primaryColor: Color,
    chromaMultiplier: Double = 1.0,
) : ColorScheme() {
    private val primaryNeutral = primaryColor.toLinearSrgb().toOklab().toOklch().let { lch ->
        lch.copy(C = lch.C * chromaMultiplier)
    }
    private val primaryAccent = primaryNeutral

    init {
        val primaryRgb8 = primaryColor.toLinearSrgb().toSrgb().quantize8()
        Timber.i("Primary color: ${String.format("%06x", primaryRgb8)} => $primaryNeutral")
    }

    // Main background color. Tinted with the primary color.
    override val neutral1 by lazy(mode = LazyThreadSafetyMode.NONE) {
        transformSwatch(targetColors.neutral1, primaryNeutral)
    }

    // Secondary background color. Slightly tinted with the primary color.
    override val neutral2 by lazy(mode = LazyThreadSafetyMode.NONE) {
        transformSwatch(targetColors.neutral2, primaryNeutral)
    }

    // Main accent color. Generally, this is close to the primary color.
    override val accent1 by lazy(mode = LazyThreadSafetyMode.NONE) {
        transformSwatch(targetColors.accent1, primaryAccent)
    }

    // Secondary accent color. Darker shades of accent1.
    override val accent2 by lazy(mode = LazyThreadSafetyMode.NONE) {
        transformSwatch(targetColors.accent2, primaryAccent)
    }

    // Tertiary accent color. Primary color shifted to the next secondary color via hue offset.
    override val accent3 by lazy(mode = LazyThreadSafetyMode.NONE) {
        transformSwatch(targetColors.accent3, primaryAccent) { lch ->
            lch.copy(h = lch.h + ACCENT3_HUE_SHIFT_DEGREES)
        }
    }

    private fun transformSwatch(
        swatch: Map<Int, Color>,
        primary: Lch,
        colorFilter: (Oklch) -> Oklch = { it },
    ): Map<Int, Color> {
        return swatch.map { (shade, color) ->
            val target = color as? Lch
                ?: color.toLinearSrgb().toOklab().toOklch()
            val targetLstar = TargetColors.LSTAR_LIGHTNESS_MAP[shade]!!
            val newLch = colorFilter(transformColor(target, primary, targetLstar))
            val newSrgb = newLch.toOklab().toLinearSrgb().toSrgb()

            val newRgb8 = newSrgb.quantize8()
            Timber.d("Transform: [$shade] $target => $newLch => ${String.format("%06x", newRgb8)}")
            shade to newSrgb
        }.toMap()
    }

    private fun transformColor(target: Lch, primary: Lch, targetLstar: Double): Oklch {
        // Allow colorless gray.
        val C = primary.C.coerceIn(0.0, target.C)
        // Use the primary color's hue, since it's the most prominent feature of the theme.
        val h = primary.h
        // Binary search for the target lightness
        val L = searchLstar(targetLstar, C, h)

        return Oklch(L, C, h)
    }

    private fun searchLstar(targetLstar: Double, C: Double, h: Double): Double {
        // Some colors result in imperfect blacks (e.g. #000002) if we don't account for
        // negative lightness.
        var min = -0.5
        // Colors can also be overexposed to better match CIELAB targets.
        var max = 1.5

        // Keep track of the best L value found.
        // This will be returned if the search fails to converge.
        var bestL = Double.NaN
        var bestLDelta = Double.POSITIVE_INFINITY

        while (true) {
            val mid = (min + max) / 2

            // The search must be done in 8-bpc sRGB to account for the effects of clipping.
            // Otherwise, results at lightness extremes (especially ~shade 10) are quite far
            // off after quantization and clipping.
            val srgbClipped = Oklch(mid, C, h).toOklab().toLinearSrgb().toSrgb().quantize8()

            // Convert back to Color and compare CIELAB L*
            val lstar = Srgb(srgbClipped).toLinearSrgb().toCieXyz().toCieLab().L
            val delta = abs(lstar - targetLstar)

            if (delta < bestLDelta) {
                bestL = mid
                bestLDelta = delta
            }

            Timber.i("Search for L*: [target=$targetLstar] ($min, $max)=>$mid  L*=$lstar delta=$delta  (best=$bestL delta=$bestLDelta)")

            when {
                // If L* ~= target, consider the result good enough
                delta <= TARGET_LSTAR_THRESHOLD -> return mid
                // If min ~= max, we're unlikely to make any more progress
                abs(min - max) <= TARGET_L_EPSILON -> return bestL

                // Divide and continue
                lstar < targetLstar -> min = mid
                lstar > targetLstar -> max = mid
            }
        }
    }

    companion object {
        // Hue shift for the tertiary accent color (accent3), in degrees.
        // 60 degrees = shifting by a secondary color
        private const val ACCENT3_HUE_SHIFT_DEGREES = 60.0

        // Threshold for matching CIELAB L* targets. Colors with lightness delta
        // under this value are considered to match the reference lightness.
        private const val TARGET_LSTAR_THRESHOLD = 0.01

        // Threshold for terminating the binary search if min and max are too close.
        // The search is very unlikely to make progress after this point, so we
        // just terminate it and return the best L* value found.
        private const val TARGET_L_EPSILON = 0.001
    }
}
