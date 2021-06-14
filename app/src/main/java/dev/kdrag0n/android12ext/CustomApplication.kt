package dev.kdrag0n.android12ext

import android.app.Application
import com.topjohnwu.superuser.Shell
import de.Maxr1998.modernpreferences.Preference
import dev.kdrag0n.android12ext.core.BroadcastManager
import dev.kdrag0n.android12ext.core.data.SettingsRepository
import dev.kdrag0n.android12ext.monet.theme.ReferenceGenerator
import dev.kdrag0n.android12ext.ui.main.MainViewModel
import dev.kdrag0n.android12ext.ui.monet.palette.PaletteViewModel
import dev.kdrag0n.android12ext.ui.monet.quantizer.QuantizerViewModel
import dev.kdrag0n.android12ext.ui.settings.appearance.AppearanceSettingsViewModel
import dev.kdrag0n.android12ext.ui.settings.appearance.ColorDialogViewModel
import dev.kdrag0n.android12ext.ui.settings.root.SettingsViewModel
import dev.kdrag0n.android12ext.ui.settings.system.SystemSettingsViewModel
import dev.kdrag0n.android12ext.ui.settings.tweaks.TweakSettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.lsposed.hiddenapibypass.HiddenApiBypass
import timber.log.Timber

// Referenced in AndroidManifest.xml
@Suppress("Unused")
class CustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        commonInit()

        Preference.Config.summaryMaxLines = 5

        HiddenApiBypass.addHiddenApiExemptions("L")

        val koinModule = module {
            single { BroadcastManager(get()) }
            single { SettingsRepository(get()) }
            single { ReferenceGenerator(get()) }

            viewModel { MainViewModel(get(), get()) }
            viewModel { SettingsViewModel(get(), get(), get()) }
            viewModel { SystemSettingsViewModel(get(), get()) }
            viewModel { TweakSettingsViewModel(get(), get()) }
            viewModel { AppearanceSettingsViewModel(get(), get(), get()) }
            viewModel { QuantizerViewModel(get()) }
            viewModel { ColorDialogViewModel() }
            viewModel { PaletteViewModel() }
        }

        startKoin {
            if (BuildConfig.DEBUG) {
                androidLogger()
            }

            androidContext(this@CustomApplication)
            modules(koinModule)
        }

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
