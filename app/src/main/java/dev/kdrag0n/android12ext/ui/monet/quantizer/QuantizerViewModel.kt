package dev.kdrag0n.android12ext.ui.monet.quantizer

import android.app.WallpaperColors
import android.app.WallpaperManager
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import androidx.core.content.getSystemService
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.kdrag0n.android12ext.monet.extraction.allColors
import dev.kdrag0n.android12ext.monet.extraction.mainColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class QuantizerViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {
    private val wallpaperManager = context.getSystemService<WallpaperManager>()!!

    val wallpaperDrawable = MutableLiveData<Drawable>()
    val wallpaperColors = MutableLiveData<List<Int>?>(null)

    private suspend fun updateWallpaper() {
        val drawable = wallpaperManager.drawable

        // Show the wallpaper first
        wallpaperDrawable.value = drawable

        // Quantization may take a while, so show progress first
        wallpaperColors.value = null
        withContext(Dispatchers.IO) {
            // Pre-render bitmap to avoid distorting benchmark
            val bitmap = drawable.toBitmap()

            val before = System.currentTimeMillis()
            val colors = WallpaperColors.fromBitmap(bitmap)
            val after = System.currentTimeMillis()
            Timber.i("Quantized wallpaper in ${after - before} ms")

            val colorInts = colors.mainColors
                .map { it.toArgb() }

            wallpaperColors.postValue(colorInts)
        }
    }

    private val colorsChangedListener = WallpaperManager.OnColorsChangedListener { _, which ->
        if (which != WallpaperManager.FLAG_SYSTEM) {
            return@OnColorsChangedListener
        }

        viewModelScope.launch {
            updateWallpaper()
        }
    }

    init {
        viewModelScope.launch {
            updateWallpaper()
        }

        wallpaperManager.addOnColorsChangedListener(colorsChangedListener, Handler(Looper.getMainLooper()))
    }

    override fun onCleared() {
        wallpaperManager.removeOnColorsChangedListener(colorsChangedListener)
    }
}
