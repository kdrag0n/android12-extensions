package dev.kdrag0n.android12ext

import android.app.Application
import timber.log.Timber

// Referenced in AndroidManifest.xml
@Suppress("Unused")
class CustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        commonInit()
    }

    companion object {
        fun commonInit() {
            if (BuildConfig.DEBUG) {
                Timber.plant(Timber.DebugTree())
            }
        }
    }
}