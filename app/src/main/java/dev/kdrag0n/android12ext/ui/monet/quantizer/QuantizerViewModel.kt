package dev.kdrag0n.android12ext.ui.monet.quantizer

import android.app.WallpaperManager
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import androidx.core.content.getSystemService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.kdrag0n.android12ext.monet.extraction.allColors
import dev.kdrag0n.android12ext.monet.extraction.mainColors
import javax.inject.Inject

@HiltViewModel
class QuantizerViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {
    private val wallpaperManager = context.getSystemService<WallpaperManager>()!!

    val wallpaperDrawable = MutableLiveData<Drawable>()
    val wallpaperColors = MutableLiveData<List<Int>>()

    private fun updateWallpaper() {
        val colorInts = wallpaperManager.getWallpaperColors(WallpaperManager.FLAG_SYSTEM)!!.allColors
            .entries
            .sortedByDescending { it.value }
            .map { it.key }

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
