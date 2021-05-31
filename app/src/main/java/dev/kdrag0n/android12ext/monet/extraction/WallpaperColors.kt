package dev.kdrag0n.android12ext.monet.extraction

import android.app.WallpaperColors
import android.graphics.Color

val WallpaperColors.mainColors: List<Color>
    get() {
        return this::class.java.getDeclaredMethod("getMainColors")
            .invoke(this) as List<Color>
    }
