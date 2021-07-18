package dev.kdrag0n.android12ext.monet.overlay

import android.app.WallpaperColors
import android.content.Context
import android.graphics.Color
import dev.kdrag0n.android12ext.monet.extraction.mainColors

fun WallpaperColors.getSeedColor(context: Context): Int {
    val sysuiContext = context.createPackageContext(
        "com.android.systemui",
        Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY,
    )

    val colorInts = try {
        val clazz = sysuiContext.classLoader.loadClass("com.google.material.monet.ColorScheme")
        val companion = clazz.getDeclaredField("Companion").get(null)
        val seedColors = companion::class.java
            .getDeclaredMethod("getSeedColors", WallpaperColors::class.java)
            .invoke(companion, this) as List<Int>

        seedColors.map { it }
    } catch (e: ClassNotFoundException) {
        // Not exactly the same, but it's close enough for AOSP
        mainColors.map { it.toArgb() }
    }

    return colorInts.firstOrNull()
        ?: Color.BLUE
}
