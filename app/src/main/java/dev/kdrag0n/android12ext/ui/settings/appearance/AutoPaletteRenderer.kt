package dev.kdrag0n.android12ext.ui.settings.appearance

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import dev.kdrag0n.android12ext.core.data.SettingsRepository
import dev.kdrag0n.android12ext.monet.colors.Srgb
import dev.kdrag0n.android12ext.monet.theme.DynamicColorScheme
import dev.kdrag0n.android12ext.monet.theme.MaterialYouTargets
import dev.kdrag0n.android12ext.ui.monet.palette.PaletteActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.io.File
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AutoPaletteRenderer(
    private val fragment: Fragment,
) : KoinComponent {
    private val settingsRepo: SettingsRepository by inject()

    private var launchContinuation: Continuation<ActivityResult>? = null
    private val launcher = fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        launchContinuation?.resume(it)
        launchContinuation = null
    }

    private suspend fun startActivityForResult(intent: Intent) = suspendCoroutine<ActivityResult> { cont ->
        launchContinuation = cont
        launcher.launch(intent)
    }

    @SuppressLint("ApplySharedPref")
    private suspend fun doColor(context: Context, name: String, color: Int) {
        Timber.i("Rendering $name ${String.format("%08x", color)}")

        withContext(Dispatchers.IO) {
            settingsRepo.prefs.edit().apply {
                putInt("monet_custom_color_value", color)
                commit()
            }
        }

        val intent = Intent(context, PaletteActivity::class.java)
        intent.putExtra("screenshot_name", name)

        startActivityForResult(intent)
    }

    suspend fun doAllColors() {
        val context = fragment.requireContext()

        val sdcardDir = context.getExternalFilesDir(null)
        withContext(Dispatchers.IO) {
            File("${sdcardDir}/palette").mkdirs()
        }

        COLORS.forEach { (name, color) ->
            doColor(context, name, color)
        }
    }

    companion object {
        private const val TAG = "MonetBench"

        val COLORS = mapOf(
            "magenta" to -6543440,
            "violet" to -10011977,
            "blue" to -12627531,
            "green" to -11751600,
            "teal" to -16738680,
            "orange" to -26624,
            "red-orange" to -769226,
            "yellow" to -5317,
            "brown" to -8825528,
            "blue-gray" to -10453621,
            "pink" to -54125,

            "pure-red" to -65536,
            "pure-green" to -16711936,
            "pure-blue" to -16776961,
        )

        // Needs to work on release builds for performance reasons
        @SuppressLint("LogNotTimber")
        fun runBenchmark() {
            Log.i(TAG, "Warming up")
            (1..10000).forEach { _ ->
                COLORS.values.forEach { color ->
                    val colors = DynamicColorScheme(
                        targetColors = MaterialYouTargets(),
                        primaryColor = Srgb(color),
                    )
                    colors.accentColors
                    colors.neutralColors
                }
            }

            Log.i(TAG, "Benchmarking")
            var count = 0
            val before = System.nanoTime()
            (1..10000).forEach { _ ->
                COLORS.values.forEach { color ->
                    val colors = DynamicColorScheme(
                        targetColors = MaterialYouTargets(),
                        primaryColor = Srgb(color),
                    )
                    colors.accentColors
                    colors.neutralColors
                    count++
                }
            }
            val after = System.nanoTime()
            val timePerScheme = (after - before).toDouble() / 1e6 / count
            Log.i(TAG, "Done in $timePerScheme ms/color")
        }
    }
}
