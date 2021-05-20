package dev.kdrag0n.android12ext

import android.app.Application
import de.Maxr1998.modernpreferences.Preference
import dev.kdrag0n.android12ext.core.BroadcastManager
import dev.kdrag0n.android12ext.core.data.SettingsRepository
import dev.kdrag0n.android12ext.monet.theme.ReferenceGenerator
import dev.kdrag0n.android12ext.ui.main.MainViewModel
import dev.kdrag0n.android12ext.ui.settings.mods.ModsSettingsViewModel
import dev.kdrag0n.android12ext.ui.settings.root.SettingsViewModel
import dev.kdrag0n.android12ext.ui.settings.system.SystemSettingsViewModel
import dev.kdrag0n.android12ext.ui.settings.tweaks.TweakSettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber

// Referenced in AndroidManifest.xml
@Suppress("Unused")
class CustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        commonInit()

        Preference.Config.summaryMaxLines = 5

        val koinModule = module {
            single { BroadcastManager(get()) }
            single { SettingsRepository(get()) }
            single { ReferenceGenerator(get()) }

            viewModel { MainViewModel(get(), get()) }
            viewModel { SettingsViewModel(get(), get(), get()) }
            viewModel { SystemSettingsViewModel(get(), get()) }
            viewModel { TweakSettingsViewModel(get(), get()) }
            viewModel { ModsSettingsViewModel(get(), get(), get()) }
        }

        startKoin {
            if (BuildConfig.DEBUG) {
                androidLogger()
            }

            androidContext(this@CustomApplication)
            modules(koinModule)
        }
    }

    companion object {
        fun commonInit() {
            if (BuildConfig.DEBUG) {
                Timber.plant(Timber.DebugTree())
            }
        }
    }
}