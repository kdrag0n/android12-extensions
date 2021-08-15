package dev.kdrag0n.android12ext.monet.theme

import dev.kdrag0n.android12ext.monet.colors.*
import dev.kdrag0n.android12ext.monet.colors.CieXyz.Companion.toCieXyz
import dev.kdrag0n.android12ext.monet.colors.Zcam.Companion.toAbs
import dev.kdrag0n.android12ext.monet.colors.Zcam.Companion.toRel
import dev.kdrag0n.android12ext.monet.colors.Zcam.Companion.toZcam
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.sign
import kotlin.math.sqrt

class ZcamDynamicColorScheme(
    targets: ColorScheme,
    seedColor: Color,
    chromaFactor: Double = 1.0,
    private val cond: Zcam.ViewingConditions,
    private val accurateShades: Boolean = true,
) : ColorScheme() {
    private val seedNeutral = seedColor.toLinearSrgb().toCieXyz().toAbs(cond).toZcam(cond).let { lch ->
        lch.copy(chroma = lch.chroma * chromaFactor)
    }
    private val seedAccent = seedNeutral

    init {
        val seedRgb8 = seedColor.toLinearSrgb().toSrgb().quantize8()
        Timber.i("Seed color: %06x => $seedNeutral", seedRgb8)
    }

    // Main accent color. Generally, this is close to the seed color.
    override val accent1 by lazy(mode = LazyThreadSafetyMode.NONE) {
        transformSwatch(targets.accent1, seedAccent, targets.accent1)
    }

    // Secondary accent color. Darker shades of accent1.
    override val accent2 by lazy(mode = LazyThreadSafetyMode.NONE) {
        transformSwatch(targets.accent2, seedAccent, targets.accent1)
    }

    // Tertiary accent color. Seed color shifted to the next secondary color via hue offset.
    override val accent3 by lazy(mode = LazyThreadSafetyMode.NONE) {
        val seedA3 = seedAccent.copy(hueAngle = seedAccent.hueAngle + ACCENT3_HUE_SHIFT_DEGREES)
        transformSwatch(targets.accent3, seedA3, targets.accent1)
    }

    // Main background color. Tinted with the seed color.
    override val neutral1 by lazy(mode = LazyThreadSafetyMode.NONE) {
        transformSwatch(targets.neutral1, seedNeutral, targets.neutral1)
    }

    // Secondary background color. Slightly tinted with the seed color.
    override val neutral2 by lazy(mode = LazyThreadSafetyMode.NONE) {
        transformSwatch(targets.neutral2, seedNeutral, targets.neutral1)
    }

    private fun transformSwatch(
        swatch: ColorSwatch,
        seed: Zcam,
        referenceSwatch: ColorSwatch,
    ): ColorSwatch {
        return swatch.map { (shade, color) ->
            val target = color as? Zcam
                ?: color.toLinearSrgb().toCieXyz().toAbs(cond).toZcam(cond)
            val reference = referenceSwatch[shade]!! as? Zcam
                ?: color.toLinearSrgb().toCieXyz().toAbs(cond).toZcam(cond)
            val newLch = transformColor(target, seed, reference)
            val newSrgb = newLch.toLinearSrgb().toSrgb()

            val newRgb8 = newSrgb.quantize8()
            Timber.d("Transform: [$shade] $target => $newLch => %06x", newRgb8)
            shade to newSrgb
        }.toMap()
    }

    private fun transformColor(target: Zcam, seed: Zcam, reference: Zcam): Color {
        // Keep target lightness.
        val lightness = target.lightness
        // Allow colorless gray and low-chroma colors by clamping.
        // To preserve chroma ratios, scale chroma by the reference (A-1 / N-1).
        val scaleC = if (reference.chroma == 0.0) {
            // Zero reference chroma won't have chroma anyway, so use 0 to avoid a divide-by-zero
            0.0
        } else {
            // Non-zero reference chroma = possible chroma scale
            seed.chroma.coerceIn(0.0, reference.chroma) / reference.chroma
        }
        val chroma = target.chroma * scaleC
        // Use the seed color's hue, since it's the most prominent feature of the theme.
        val hueAngle = seed.hueAngle

        return if (accurateShades) {
            clipZcamJchToLinearSrgbLightness(lightness, chroma, hueAngle)
        } else {
            clipZcamJchToLinearSrgbAdaptive(lightness, chroma, hueAngle, 5.0)
        }
    }

    private fun zcamJchToLinearSrgb(
        lightness: Double,
        chroma: Double,
        hue: Double,
    ) = Zcam(
        lightness = lightness,
        chroma = chroma,
        hueAngle = hue,
        viewingConditions = cond,
    ).toCieXyz(
        luminanceSource = Zcam.LuminanceSource.LIGHTNESS,
        chromaSource = Zcam.ChromaSource.CHROMA,
    ).toRel(cond).toLinearSrgb()

    private fun evalLine(x: Double, slope: Double, intercept: Double) =
        slope * x + intercept

    // TODO: split this into a dedicated file
    private fun clipZcamJchToLinearSrgb(
        lightness: Double,
        chroma: Double,
        hue: Double,
        // Target point *within* gamut
        l0: Double,
        c0: Double,
    ): LinearSrgb {
        val initialResult = zcamJchToLinearSrgb(lightness, chroma, hue)

        return when {
            initialResult.isInGamut() -> initialResult
            // Avoid searching black and white for performance
            lightness <= EPSILON -> LinearSrgb(0.0, 0.0, 0.0)
            lightness >= 100.0 - EPSILON -> LinearSrgb(1.0, 1.0, 1.0)

            // Clip with gamut intersection
            else -> {
                // Create a line - x=C, y=L - intersecting a hue plane
                val l1 = lightness
                val c1 = chroma
                val slope = (l1 - l0) / (c1 - c0)
                val intercept = l0 - slope * c0

                var lo = 0.0
                var hi = chroma

                var newLinearSrgb = initialResult
                while (abs(hi - lo) > EPSILON) {
                    val midC = (lo + hi) / 2
                    val midL = evalLine(midC, slope, intercept)

                    newLinearSrgb = zcamJchToLinearSrgb(midL, midC, hue)

                    //Timber.i("Search for Cz: ($lo, $hi)=>$mid  inGamut=${newLinearSrgb.isInGamut()}")

                    if (!newLinearSrgb.isInGamut()) {
                        hi = midC
                    } else {
                        val midC2 = midC + EPSILON
                        val midL2 = evalLine(midC2, slope, intercept)

                        val ptOutside = zcamJchToLinearSrgb(midL2, midC2, hue)
                        if (ptOutside.isInGamut()) {
                            lo = midC
                        } else {
                            break
                        }
                    }
                }

                newLinearSrgb
            }
        }
    }

    private fun clipZcamJchToLinearSrgbLightness(
        lightness: Double,
        chroma: Double,
        hue: Double,
    ) = clipZcamJchToLinearSrgb(lightness, chroma, hue, lightness, 0.0)

    private fun clipZcamJchToLinearSrgbAdaptive(
        lightness: Double,
        chroma: Double,
        hue: Double,
        alpha: Double,
    ): LinearSrgb {
        // Scale values for adaptive L
        val L = lightness / 100.0
        val C = chroma / 100.0

        val Ld = L - 0.5
        val e1 = 0.5 + abs(Ld) + alpha * C
        val l0 = 0.5*(1.0 + sign(Ld) *(e1 - sqrt(e1*e1 - 2.0 *abs(Ld))))

        val adaptiveL0 = l0 * 100.0
        return clipZcamJchToLinearSrgb(lightness, chroma, hue, adaptiveL0, 0.0)
    }

    companion object {
        // Hue shift for the tertiary accent color (accent3), in degrees.
        // 60 degrees = shifting by a secondary color
        private const val ACCENT3_HUE_SHIFT_DEGREES = 60.0

        private const val EPSILON = 0.0001

        private fun LinearSrgb.isInGamut() = !r.isNaN() && !g.isNaN() && !b.isNaN() &&
                r in 0.0..1.0 && g in 0.0..1.0 && b in 0.0..1.0
    }
}
