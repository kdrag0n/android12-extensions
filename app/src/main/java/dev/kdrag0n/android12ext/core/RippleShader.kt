package dev.kdrag0n.android12ext.core

object RippleShader {
    val SHADER = """
        uniform vec2 in_origin;
        uniform vec2 in_touch;
        uniform float in_progress;
        uniform float in_maxRadius;
        uniform vec2 in_resolutionScale;
        uniform vec2 in_noiseScale;
        uniform float in_hasMask;
        uniform float in_noisePhase;
        uniform float in_turbulencePhase;
        uniform vec2 in_tCircle1;
        uniform vec2 in_tCircle2;
        uniform vec2 in_tCircle3;
        uniform vec2 in_tRotation1;
        uniform vec2 in_tRotation2;
        uniform vec2 in_tRotation3;
        uniform vec4 in_color;
        uniform vec4 in_sparkleColor;
        uniform shader in_shader;

        // White noise with triangular distribution
        float triangleNoise(vec2 n) {
            n  = fract(n * vec2(5.3987, 5.4421));
            n += dot(n.yx, n.xy + vec2(21.5351, 14.3137));
            float xy = n.x * n.y;
            return fract(xy * 95.4307) + fract(xy * 75.04961) - 1.0;
        }

        // PDF for Gaussian blur
        const float SQRT_2PI = 2.506628274631000241612355;
        float gaussian_pdf(float mean, float stddev, float x) {
            float a = (x - mean) / stddev;
            return 1.0 / (stddev * SQRT_2PI) * exp(-0.5 * a*a);
        }

        // Circular wave with Gaussian blur
        float softWave(vec2 uv, vec2 center, float radius, float blur) {
            // Distance from the center of the circle (touch point), normalized to [0, 1]  radius)
            float dNorm = distance(uv, center) / radius;
            // Ring position within full circle = progress
            float ringX = in_progress;
            // Apply Gaussian blur with dynamic standard deviation, and scale to reduce lightness
            float ring = gaussian_pdf(0.0, 0.05 + 0.15 * blur, ringX - dNorm) * 0.4;

            // 0.5 base highlight + foreground ring
            return 0.5 + ring;
        }

        float subProgress(float start, float end, float progress) {
            return saturate((progress - start) / (end - start));
        }

        vec4 main(vec2 pos) {
            // Fade the entire ripple in and out, including base highlight
            float fadeIn = subProgress(0.0, 0.1, in_progress);
            float fadeOut = subProgress(0.5, 1.0, in_progress);
            float fade = min(fadeIn, 1.0 - fadeOut);

            // Dither with triangular white noise. Unfortunately, we can't use blue noise
            // because RuntimeShader doesn't allow us to add custom textures.
            float dither = triangleNoise(pos) / 255.0;
            float waveAlpha = softWave(pos, in_touch, in_maxRadius, 1.3 - in_progress) * fade * in_color.a + dither;
            vec4 waveColor = vec4(in_color.rgb * waveAlpha, waveAlpha);

            float mask = in_hasMask == 1.0 ? sample(in_shader, pos).a > 0.0 ? 1.0 : 0.0 : 1.0;
            return waveColor * mask;
        }
    """.trimIndent()
}
