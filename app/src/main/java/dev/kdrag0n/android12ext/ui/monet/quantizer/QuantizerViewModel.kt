package dev.kdrag0n.android12ext.ui.monet.quantizer

import android.app.WallpaperColors
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import androidx.core.content.getSystemService
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
    val imageRect = MutableLiveData<Rect?>(null)

    private suspend fun updateWallpaper(rect: Rect? = null) {
        val drawable = wallpaperManager.drawable

        // Show the wallpaper first if not a rect update
        if (rect == null) {
            wallpaperDrawable.value = drawable
        }

        // Quantization may take a while, so show progress first
        wallpaperColors.value = null
        withContext(Dispatchers.IO) {
            // Pre-render bitmap to avoid distorting benchmark
            val bitmap = drawable.toBitmap().let {
                if (rect == null) {
                    it
                } else {
                    Bitmap.createBitmap(it, rect.left, rect.top, rect.width(), rect.height())
                }
            }

            val before = System.currentTimeMillis()
            val colors = WallpaperColors.fromBitmap(bitmap)
            val after = System.currentTimeMillis()
            Timber.i("Quantized wallpaper in ${after - before} ms. rect=$rect - width=${bitmap.width} height=${bitmap.height}")

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


    private val rectObserver = Observer<Rect?> {
        viewModelScope.launch {
            delay(UPDATE_RECT_DEBOUNCE_DELAY)

            if (imageRect.value == it) {
                updateWallpaper(rect = it)
            }
        }
    }

    init {
        viewModelScope.launch {
            updateWallpaper()
        }

        imageRect.observeForever(rectObserver)
        wallpaperManager.addOnColorsChangedListener(colorsChangedListener, Handler(Looper.getMainLooper()))
    }

    override fun onCleared() {
        imageRect.removeObserver(rectObserver)
        wallpaperManager.removeOnColorsChangedListener(colorsChangedListener)
    }

    companion object {
        private const val UPDATE_RECT_DEBOUNCE_DELAY = 250L
    }
}
