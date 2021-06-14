package dev.kdrag0n.android12ext.monet.extraction

import android.app.WallpaperColors
import android.graphics.Color

@Suppress("unchecked_cast")
val WallpaperColors.mainColors: List<Color>
    get() {
        return this::class.java.getDeclaredMethod("getMainColors")
            .invoke(this) as List<Color>
    }

@Suppress("unchecked_cast")
val WallpaperColors.allColors: Map<Int, Int>
    get() {
        return this::class.java.getDeclaredMethod("getAllColors")
            .invoke(this) as Map<Int, Int>
    }
