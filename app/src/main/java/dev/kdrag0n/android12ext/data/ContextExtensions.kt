package dev.kdrag0n.android12ext.data

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DeviceProtected

@Module
@InstallIn(SingletonComponent::class)
object DeviceContextModule {
    @Provides
    @DeviceProtected
    fun provideDeviceProtectedContext(app: Application): Context =
        app.createDeviceProtectedStorageContext()
}

private fun isGoogleVariant(context: Context, pkg: String) =
    "Google" in context.packageManager.getPackageInfo(pkg, PackageManager.GET_META_DATA)
        .applicationInfo.sourceDir

fun Context.hasSystemUiGoogle() = isGoogleVariant(this, "com.android.systemui")

fun Context.hasSettingsGoogle() = isGoogleVariant(this, "com.android.settings")

fun Context.hasPixelLauncher(): Boolean {
    return try {
        packageManager.getPackageInfo("com.google.android.apps.nexuslauncher", 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}
