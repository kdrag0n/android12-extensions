package dev.kdrag0n.android12ext.monet.colors

import dev.kdrag0n.android12ext.monet.colors.Oklab.Companion.toOklab
import kotlin.math.*

/**
 * Ported from the original C++ implementation:
 *
 * Copyright (c) 2021 Björn Ottosson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
// Renaming variables hurts the readability of math code
@Suppress("LocalVariableName")
object OklabGamut {
    // Finds the maximum saturation possible for a given hue that fits in sRGB
    // Saturation here is defined as S = C/L
    // a and b must be normalized so a^2 + b^2 == 1
    private fun computeMaxSaturation(a: Double, b: Double): Double {
        // Max saturation will be when one of r, g or b goes below zero.

        // Select different coefficients depending on which component goes below zero first
        val k0: Double
        val k1: Double
        val k2: Double
        val k3: Double
        val k4: Double
        val wl: Double
        val wm: Double
        val ws: Double

        when {
            -1.88170328 * a - 0.80936493 * b > 1 -> {
                // Red component
                k0 = +1.19086277
                k1 = +1.76576728
                k2 = +0.59662641
                k3 = +0.75515197
                k4 = +0.56771245
                wl = +4.0767416621
                wm = -3.3077115913
                ws = +0.2309699292
            }
            1.81444104 * a - 1.19445276 * b > 1 -> {
                // Green component
                k0 = +0.73956515
                k1 = -0.45954404
                k2 = +0.08285427
                k3 = +0.12541070
                k4 = +0.14503204
                wl = -1.2681437731
                wm = +2.6097574011
                ws = -0.3413193965
            }
            else -> {
                // Blue component
                k0 = +1.35733652
                k1 = -0.00915799
                k2 = -1.15130210
                k3 = -0.50559606
                k4 = +0.00692167
                wl = -0.0041960863
                wm = -0.7034186147
                ws = +1.7076147010
            }
        }

        // Approximate max saturation using a polynomial:
        val S = k0 + k1 * a + k2 * b + k3 * a * a + k4 * a * b

        // Do one step Halley's method to get closer
        // this gives an error less than 10e6, except for some blue hues where the dS/dh is close to infinite
        // this should be sufficient for most applications, otherwise do two/three steps

        val k_l = +0.3963377774 * a + 0.2158037573 * b
        val k_m = -0.1055613458 * a - 0.0638541728 * b
        val k_s = -0.0894841775 * a - 1.2914855480 * b

        run {
            val l_ = 1 + S * k_l
            val m_ = 1 + S * k_m
            val s_ = 1 + S * k_s

            val l = l_ * l_ * l_
            val m = m_ * m_ * m_
            val s = s_ * s_ * s_

            val l_dS = 3 * k_l * l_ * l_
            val m_dS = 3 * k_m * m_ * m_
            val s_dS = 3 * k_s * s_ * s_

            val l_dS2 = 6 * k_l * k_l * l_
            val m_dS2 = 6 * k_m * k_m * m_
            val s_dS2 = 6 * k_s * k_s * s_

            val f  = wl * l     + wm * m     + ws * s
            val f1 = wl * l_dS  + wm * m_dS  + ws * s_dS
            val f2 = wl * l_dS2 + wm * m_dS2 + ws * s_dS2

            return S - f * f1 / (f1*f1 - 0.5 * f * f2)
        }
    }

    // finds L_cusp and C_cusp for a given hue
    // a and b must be normalized so a^2 + b^2 == 1
    private fun findCusp(a: Double, b: Double): DoubleArray
    {
        // First, find the maximum saturation (saturation S = C/L)
        val S_cusp = computeMaxSaturation(a, b)

        // Convert to linear sRGB to find the first point where at least one of r,g or b >= 1:
        val rgb_at_max = Oklab(1.0, S_cusp * a, S_cusp * b).toLinearSrgb()
        val L_cusp = Math.cbrt(1.0 / max(max(rgb_at_max.r, rgb_at_max.g), rgb_at_max.b))
        val C_cusp = L_cusp * S_cusp

        return doubleArrayOf(L_cusp, C_cusp)
    }

    // Finds intersection of the line defined by
    // L = L0 * (1 - t) + t * L1
    // C = t * C1
    // a and b must be normalized so a^2 + b^2 == 1
    private fun findGamutIntersection(a: Double, b: Double, L1: Double, C1: Double, L0: Double): Double
    {
        // Find the cusp of the gamut triangle
        val (cuspL, cuspC) = findCusp(a, b)

        // Find the intersection for upper and lower half seprately
        var t: Double
        if (((L1 - L0) * cuspC - (cuspL - L0) * C1) <= 0) {
            // Lower half

            t = cuspC * L0 / (C1 * cuspL + cuspC * (L0 - L1))
        } else {
            // Upper half

            // First intersect with triangle
            t = cuspC * (L0 - 1) / (C1 * (cuspL - 1) + cuspC * (L0 - L1))

            // Then one step Halley's method
            run {
                val dL = L1 - L0
                val dC = C1

                val k_l = +0.3963377774 * a + 0.2158037573 * b
                val k_m = -0.1055613458 * a - 0.0638541728 * b
                val k_s = -0.0894841775 * a - 1.2914855480 * b

                val l_dt = dL + dC * k_l
                val m_dt = dL + dC * k_m
                val s_dt = dL + dC * k_s

                // If higher accuracy is required, 2 or 3 iterations of the following block can be used:
                run {
                    val L = L0 * (1.0 - t) + t * L1
                    val C = t * C1

                    val l_ = L + C * k_l
                    val m_ = L + C * k_m
                    val s_ = L + C * k_s

                    val l = l_ * l_ * l_
                    val m = m_ * m_ * m_
                    val s = s_ * s_ * s_

                    val ldt = 3 * l_dt * l_ * l_
                    val mdt = 3 * m_dt * m_ * m_
                    val sdt = 3 * s_dt * s_ * s_

                    val ldt2 = 6 * l_dt * l_dt * l_
                    val mdt2 = 6 * m_dt * m_dt * m_
                    val sdt2 = 6 * s_dt * s_dt * s_

                    val r = 4.0767416621 * l - 3.3077115913 * m + 0.2309699292 * s - 1
                    val r1 = 4.0767416621 * ldt - 3.3077115913 * mdt + 0.2309699292 * sdt
                    val r2 = 4.0767416621 * ldt2 - 3.3077115913 * mdt2 + 0.2309699292 * sdt2

                    val u_r = r1 / (r1 * r1 - 0.5 * r * r2)
                    var t_r = -r * u_r

                    val g = -1.2681437731 * l + 2.6097574011 * m - 0.3413193965 * s - 1
                    val g1 = -1.2681437731 * ldt + 2.6097574011 * mdt - 0.3413193965 * sdt
                    val g2 = -1.2681437731 * ldt2 + 2.6097574011 * mdt2 - 0.3413193965 * sdt2

                    val u_g = g1 / (g1 * g1 - 0.5 * g * g2)
                    var t_g = -g * u_g

                    val b = -0.0041960863 * l - 0.7034186147 * m + 1.7076147010 * s - 1
                    val b1 = -0.0041960863 * ldt - 0.7034186147 * mdt + 1.7076147010 * sdt
                    val b2 = -0.0041960863 * ldt2 - 0.7034186147 * mdt2 + 1.7076147010 * sdt2

                    val u_b = b1 / (b1 * b1 - 0.5 * b * b2)
                    var t_b = -b * u_b

                    t_r = if (u_r >= 0) t_r else Double.MAX_VALUE
                    t_g = if (u_g >= 0) t_g else Double.MAX_VALUE
                    t_b = if (u_b >= 0) t_b else Double.MAX_VALUE

                    t += min(t_r, min(t_g, t_b))
                }
            }
        }

        return t
    }

    fun gamutClipPreserveLightness(rgb: LinearSrgb): LinearSrgb
    {
        if (rgb.r < 1 && rgb.g < 1 && rgb.b < 1 && rgb.r > 0 && rgb.g > 0 && rgb.b > 0)
            return rgb

        val lab = rgb.toOklab()

        val L = lab.L
        val eps = 0.00001
        val C = max(eps, sqrt(lab.a * lab.a + lab.b * lab.b))
        val a_ = lab.a / C
        val b_ = lab.b / C

        val L0 = L.coerceIn(0.0..1.0)

        val t = findGamutIntersection(a_, b_, L, C, L0)
        val L_clipped = L0 * (1 - t) + t * L
        val C_clipped = t * C

        return Oklab(L_clipped, C_clipped * a_, C_clipped * b_).toLinearSrgb()
    }

    fun gamutClipProjectTo0p5(rgb: LinearSrgb): LinearSrgb
    {
        if (rgb.r < 1 && rgb.g < 1 && rgb.b < 1 && rgb.r > 0 && rgb.g > 0 && rgb.b > 0)
            return rgb

        val lab = rgb.toOklab()

        val L = lab.L
        val eps = 0.00001
        val C = max(eps, sqrt(lab.a * lab.a + lab.b * lab.b))
        val a_ = lab.a / C
        val b_ = lab.b / C

        val L0 = 0.5

        val t = findGamutIntersection(a_, b_, L, C, L0)
        val L_clipped = L0 * (1 - t) + t * L
        val C_clipped = t * C

        return Oklab(L_clipped, C_clipped * a_, C_clipped * b_).toLinearSrgb()
    }

    fun gamutClipProjectToLcusp(rgb: LinearSrgb): LinearSrgb
    {
        if (rgb.r < 1 && rgb.g < 1 && rgb.b < 1 && rgb.r > 0 && rgb.g > 0 && rgb.b > 0)
            return rgb

        val lab = rgb.toOklab()

        val L = lab.L
        val eps = 0.00001
        val C = max(eps, sqrt(lab.a * lab.a + lab.b * lab.b))
        val a_ = lab.a / C
        val b_ = lab.b / C

        // The cusp is computed here and in findGamutIntersection, an optimized solution would only compute it once.
        val (cuspL, cuspC) = findCusp(a_, b_)

        val L0 = cuspL

        val t = findGamutIntersection(a_, b_, L, C, L0)

        val L_clipped = L0 * (1 - t) + t * L
        val C_clipped = t * C

        return Oklab(L_clipped, C_clipped * a_, C_clipped * b_).toLinearSrgb()
    }

    fun gamutClipAdaptiveL0Towards0p5(rgb: LinearSrgb, alpha: Double = 0.05): LinearSrgb
    {
        if (rgb.r < 1 && rgb.g < 1 && rgb.b < 1 && rgb.r > 0 && rgb.g > 0 && rgb.b > 0)
            return rgb

        val lab = rgb.toOklab()

        val L = lab.L
        val eps = 0.00001
        val C = max(eps, sqrt(lab.a * lab.a + lab.b * lab.b))
        val a_ = lab.a / C
        val b_ = lab.b / C

        val Ld = L - 0.5
        val e1 = 0.5 + abs(Ld) + alpha * C
        val L0 = 0.5*(1.0 + sign(Ld)*(e1 - sqrt(e1*e1 - 2.0 *abs(Ld))))

        val t = findGamutIntersection(a_, b_, L, C, L0)
        val L_clipped = L0 * (1.0 - t) + t * L
        val C_clipped = t * C

        return Oklab(L_clipped, C_clipped * a_, C_clipped * b_).toLinearSrgb()
    }

    fun gamutClipAdaptiveL0TowardsLcusp(rgb: LinearSrgb, alpha: Double = 0.05): LinearSrgb
    {
        if (rgb.r < 1 && rgb.g < 1 && rgb.b < 1 && rgb.r > 0 && rgb.g > 0 && rgb.b > 0)
            return rgb

        val lab = rgb.toOklab()

        val L = lab.L
        val eps = 0.00001
        val C = max(eps, sqrt(lab.a * lab.a + lab.b * lab.b))
        val a_ = lab.a / C
        val b_ = lab.b / C

        // The cusp is computed here and in findGamutIntersection, an optimized solution would only compute it once.
        val (cuspL, cuspC) = findCusp(a_, b_)

        val Ld = L - cuspL
        val k = 2.0 * (if (Ld > 0) 1.0 - cuspL else cuspL)

        val e1 = 0.5*k + abs(Ld) + alpha * C/k
        val L0 = cuspL + 0.5 * (sign(Ld) * (e1 - sqrt(e1 * e1 - 2.0 * k * abs(Ld))))

        val t = findGamutIntersection(a_, b_, L, C, L0)
        val L_clipped = L0 * (1.0 - t) + t * L
        val C_clipped = t * C

        return Oklab(L_clipped, C_clipped * a_, C_clipped * b_).toLinearSrgb()
    }
}
