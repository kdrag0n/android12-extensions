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
// Specialized for mean=0 for performance
const float SQRT_2PI = 2.506628274631000241612355;
float gaussian_pdf(float stddev, float x) {
    float a = x / stddev;
    return exp(-0.5 * a*a) / (stddev * SQRT_2PI);
}

// Circular wave with Gaussian blur
float softWave(vec2 uv, vec2 center, float maxRadius, float radius, float blur) {
    // Distance from the center of the circle (touch point), normalized to [0, 1]  radius)
    float dNorm = distance(uv, center) / maxRadius;
    // Apply Gaussian blur with dynamic standard deviation, and scale to reduce lightness
    return gaussian_pdf(0.05 + 0.15 * blur, radius - dNorm) * 0.4;
}

float subProgress(float start, float end, float progress) {
    return saturate((progress - start) / (end - start));
}

// Animation curves
const float PI = 3.141592653589793;
float easeOutSine(float x) {
    return sin((x * PI) / 2.0);
}
float easeOutCubic(float x) {
    return 1.0 - pow(1.0 - x, 3.0);
}
float easeOutQuint(float x) {
    return 1.0 - pow(1.0 - x, 5.0);
}
float easeOutCirc(float x) {
    return sqrt(1.0 - pow(x - 1.0, 2.0));
}
float easeOutQuad(float x) {
    return 1.0 - (1.0 - x) * (1.0 - x);
}
float easeInOutSine(float x) {
    return -(cos(PI * x) - 1.0) / 2.0;
}
float easeInOutCubic(float x) {
    return x < 0.5 ? 4.0 * x * x * x : 1.0 - pow(-2.0 * x + 2.0, 3.0) / 2.0;
}
float easeInOutCirc(float x) {
    return x < 0.5
        ? (1.0 - sqrt(1.0 - pow(2.0 * x, 2.0))) / 2.0
        : (sqrt(1.0 - pow(-2.0 * x + 2.0, 2.0)) + 1.0) / 2.0;
}

vec4 main(vec2 pos) {
    // Curve the linear animation progress for responsiveness
    float progress = easeOutSine(in_progress);

    // Show highlight immediately instead of fading in for instant feedback
    // Fade the entire ripple out, including base highlight
    float fadeOut = subProgress(0.5, 1.0, progress);
    float fade = 1.0 - fadeOut;

    // Turbulence phase = time. Unlike progress, it continues moving when the
    // ripple is held between enter and exit animations, so we can use it to
    // make a hold animation.

    // Hold time increases the radius slightly to progress the animation.
    float timeOffsetMs = 0.0;
    float waveProgress = progress + timeOffsetMs / 60.0;
    // Blur radius decreases as the animation progresses, but increases with hold time
    // as part of gradually spreading out.
    float waveBlur = 1.3 - waveProgress + (timeOffsetMs / 15.0);
    // The wave also fades out with hold time.
    float waveFade = saturate(1.0 - timeOffsetMs / 20.0);
    // Calculate wave color, excluding fade
    float waveAlpha = softWave(pos, in_touch, in_maxRadius / 2.3, waveProgress, waveBlur);

    // Dither with triangular white noise. Unfortunately, we can't use blue noise
    // because RuntimeShader doesn't allow us to add custom textures.
    float dither = triangleNoise(pos) / 128.0;

    // 0.5 base highlight + foreground ring
    float finalAlpha = (0.5 + waveAlpha * waveFade) * fade * in_color.a + dither;
    vec4 finalColor = vec4(in_color.rgb * finalAlpha, finalAlpha);

    float mask = in_hasMask == 1.0 ? sample(in_shader, pos).a > 0.0 ? 1.0 : 0.0 : 1.0;
    return finalColor * mask;
}
