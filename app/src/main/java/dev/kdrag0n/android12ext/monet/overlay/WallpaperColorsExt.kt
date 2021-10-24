package dev.kdrag0n.android12ext.monet.overlay

import android.app.WallpaperColors
import android.content.Context
import android.graphics.Color
import dev.kdrag0n.android12ext.monet.extraction.mainColors
import dev.kdrag0n.android12ext.utils.call

fun WallpaperColors.getColorInts(context: Context): List<Int> {
    val sysuiContext = context.createPackageContext(
        "com.android.systemui",
        Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY,
    )

    return try {
        val clazz = sysuiContext.classLoader.loadClass("com.google.material.monet.ColorScheme")
        val companion = clazz.getDeclaredField("Companion").get(null)
        val seedColors = companion!!.call<List<Int>>("getSeedColors", this)

        seedColors.map { it }
    } catch (e: ClassNotFoundException) {
        // Not exactly the same, but it's close enough for AOSP
        mainColors.map { it.toArgb() }
    }
}

fun WallpaperColors.getSeedColor(context: Context) = getColorInts(context).firstOrNull()
    ?: Color.BLUE
