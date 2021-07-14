package dev.kdrag0n.android12ext.core.ripple

const val RIPPLE_SHADER_NO_SPARKLES = """
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

float softCircle(vec2 uv, vec2 xy, float radius, float blur) {
    float blurHalf = blur * 0.5;
    float d = distance(uv, xy);
    return 1. - smoothstep(1. - blurHalf, 1. + blurHalf, d / radius);
}
float subProgress(float start, float end, float progress) {
    float sub = clamp(progress, start, end);
    return (sub - start) / (end - start); 
}

vec4 main(vec2 p) {
    float fadeIn = subProgress(0., 0.13, in_progress);
    float scaleIn = subProgress(0., 1.0, in_progress);
    float fadeOutRipple = subProgress(0.4, 1., in_progress);
    vec2 center = mix(in_touch, in_origin, saturate(in_progress * 2.0));
    vec2 uv = p * in_resolutionScale;
    float fade = min(fadeIn, 1. - fadeOutRipple);
    float waveAlpha = softCircle(p, center, in_maxRadius * scaleIn, 1.) * fade * in_color.a;
    vec4 waveColor = vec4(in_color.rgb * waveAlpha, waveAlpha);
    float mask = in_hasMask == 1. ? sample(in_shader, p).a > 0. ? 1. : 0. : 1.;
    return waveColor * mask;
}
"""
