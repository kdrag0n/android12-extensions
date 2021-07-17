package dev.kdrag0n.android12ext.ui.monet.quantizer

import android.annotation.SuppressLint
import android.app.WallpaperColors
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
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
import dev.kdrag0n.android12ext.monet.extraction.mainColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.sqrt

@HiltViewModel
class QuantizerViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {
    private val wallpaperManager = context.getSystemService<WallpaperManager>()!!
    private val colorSchemeClass = context.createPackageContext(
        "com.android.systemui",
        Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY,
    ).classLoader.let { loader ->
        try {
            loader.loadClass("com.google.material.monet.ColorScheme")
        } catch (e: ClassNotFoundException) {
            null
        }
    }

    val wallpaperBitmap = MutableLiveData<Bitmap>()
    val wallpaperColors = MutableLiveData<List<Int>?>(null)
    val imageRect = MutableLiveData<Rect?>(null)

    private val quantizerLock = Mutex()

    // Only for debugging purposes
    @SuppressLint("MissingPermission")
    private suspend fun updateWallpaper(rect: Rect? = null) = quantizerLock.withLock {
        val drawable = wallpaperManager.drawable

        // Show the wallpaper first if not a rect update
        if (rect == null) {
            // Make a copy of the bitmap because SubsamplingScaleImageView assumes ownership and recycles it
            val orig = drawable.toBitmap()
            wallpaperBitmap.value = orig.copy(orig.config, false)
        }

        // Quantization may take a while, so show progress first
        wallpaperColors.value = null
        withContext(Dispatchers.IO) {
            // Pre-render bitmap to avoid distorting benchmark
            val bitmap = drawable.toBitmap().let {
                val bitmap = if (rect == null) {
                    it
                } else {
                    Bitmap.createBitmap(it, rect.left, rect.top, rect.width(), rect.height())
                }

                // Scale with the same logic as WallpaperColors
                val requestedArea = it.width * it.height
                if (requestedArea > MAX_WALLPAPER_EXTRACTION_AREA) {
                    val scale = sqrt(MAX_WALLPAPER_EXTRACTION_AREA.toDouble() / requestedArea)
                    val newWidth = (it.width * scale).toInt().coerceAtLeast(1)
                    val newHeight = (it.height * scale).toInt().coerceAtLeast(1)

                    Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false)
                } else {
                    bitmap
                }
            }

            val before = System.currentTimeMillis()
            val colors = WallpaperColors.fromBitmap(bitmap)
            val after = System.currentTimeMillis()
            Timber.i("Quantized wallpaper in ${after - before} ms. rect=$rect - width=${bitmap.width} height=${bitmap.height}")

            val colorInts = if (colorSchemeClass != null) {
                val companion = colorSchemeClass.getDeclaredField("Companion").get(null)
                val seedColors = companion::class.java
                    .getDeclaredMethod("getSeedColors", WallpaperColors::class.java)
                    .invoke(companion, colors) as List<Int>

                seedColors.map { it }
            } else {
                // Not exactly the same, but it's close enough for AOSP
                colors.mainColors.map { it.toArgb() }
            }

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
        private const val MAX_WALLPAPER_EXTRACTION_AREA = 12544
    }
}
