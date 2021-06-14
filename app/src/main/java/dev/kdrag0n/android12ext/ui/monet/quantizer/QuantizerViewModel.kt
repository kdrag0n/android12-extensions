package dev.kdrag0n.android12ext.ui.monet.quantizer

import android.app.Application
import android.app.WallpaperManager
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import androidx.core.content.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import dev.kdrag0n.android12ext.monet.extraction.mainColors

class QuantizerViewModel(app: Application) : AndroidViewModel(app) {
    private val wallpaperManager = app.getSystemService<WallpaperManager>()!!

    val wallpaperDrawable = MutableLiveData<Drawable>()
    val wallpaperColors = MutableLiveData<List<Int>>()

    private fun updateWallpaper() {
        val colorInts = wallpaperManager.getWallpaperColors(WallpaperManager.FLAG_SYSTEM)!!.mainColors
            .map { it.toArgb() }

        wallpaperDrawable.value = wallpaperManager.drawable
        wallpaperColors.value = colorInts
    }

    private val colorsChangedListener = WallpaperManager.OnColorsChangedListener { _, which ->
        if (which != WallpaperManager.FLAG_SYSTEM) {
            return@OnColorsChangedListener
        }

        updateWallpaper()
    }

    init {
        updateWallpaper()
        wallpaperManager.addOnColorsChangedListener(colorsChangedListener, Handler(Looper.getMainLooper()))
    }

    override fun onCleared() {
        wallpaperManager.removeOnColorsChangedListener(colorsChangedListener)
    }
}
