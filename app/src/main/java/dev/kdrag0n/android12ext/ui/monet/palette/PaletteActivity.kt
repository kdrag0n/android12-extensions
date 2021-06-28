package dev.kdrag0n.android12ext.ui.monet.palette

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import dev.kdrag0n.android12ext.R
import java.io.FileOutputStream

@AndroidEntryPoint
class PaletteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_palette)

        window.setDecorFitsSystemWindows(false)
        window.insetsController?.let { controller ->
            controller.hide(WindowInsets.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        window.decorView.post {
            intent.extras?.getString("screenshot_name")?.let { name ->
                val bitmap = takeScreenshot()
                saveScreenshot(bitmap, name)
                finish()
            }
        }
    }

    private fun takeScreenshot(): Bitmap {
        val view = findViewById<View>(android.R.id.content)
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        return bitmap
    }

    private fun saveScreenshot(bitmap: Bitmap, name: String) {
        FileOutputStream("${getExternalFilesDir(null)}/palette/$name.png").use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, it)
        }
    }
}
