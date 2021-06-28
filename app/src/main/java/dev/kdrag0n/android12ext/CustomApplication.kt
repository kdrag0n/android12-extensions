package dev.kdrag0n.android12ext

import android.app.Application
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.HiltAndroidApp
import de.Maxr1998.modernpreferences.Preference
import org.lsposed.hiddenapibypass.HiddenApiBypass
import timber.log.Timber

// Referenced in AndroidManifest.xml
@Suppress("Unused")
@HiltAndroidApp
class CustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        commonInit()

        Preference.Config.summaryMaxLines = 5

        HiddenApiBypass.addHiddenApiExemptions("L")

        Shell.enableVerboseLogging = BuildConfig.DEBUG
        Shell.setDefaultBuilder(Shell.Builder.create().apply {
            setFlags(Shell.FLAG_REDIRECT_STDERR)
        })
    }

    companion object {
        fun commonInit() {
            if (BuildConfig.DEBUG) {
                Timber.plant(Timber.DebugTree())
            }
        }
    }
}
