package dev.kdrag0n.android12ext.xposed.hooks

import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import dev.kdrag0n.android12ext.monet.theme.ColorSchemeFactory
import dev.kdrag0n.colorkt.conversion.ConversionGraph.convert
import dev.kdrag0n.colorkt.rgb.Srgb
import java.util.concurrent.atomic.AtomicInteger

class ColorSchemeHooks(
    lpparam: XC_LoadPackage.LoadPackageParam,
) : BaseHooks(lpparam) {
    private val lastCamColor = AtomicInteger()

    fun applyColorScheme(colorSchemeFactory: ColorSchemeFactory, shadesOfName: String) {
        hookBefore("com.android.internal.graphics.cam.Cam", "fromInt") {
            lastCamColor.set(args[0] as Int)
        }

        try {
            hookReplace("com.google.material.monet.Shades", shadesOfName) {
                val chroma = args[1] as Float
                val theme = colorSchemeFactory.getColor(Srgb(lastCamColor.get()))

                val swatch = when {
                    chroma >= 48.0f -> theme.accent1
                    chroma == 16.0f -> theme.accent2
                    chroma == 32.0f -> theme.accent3
                    chroma == 4.0f -> theme.neutral1
                    chroma == 8.0f -> theme.neutral2
                    else -> error("Unknown chroma $chroma")
                }

                swatch.entries
                    .filter { it.key in GOOGLE_SHADES }
                    .sortedBy { it.key }
                    .map { it.value.convert<Srgb>().toRgb8() or (0xff shl 24) }
                    .toIntArray()
            }
        } catch (e: XposedHelpers.ClassNotFoundError) {
            // Ignore: only present on Pixel stock
        }
    }

    companion object {
        private val GOOGLE_SHADES = setOf(10, 50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000)
    }
}
