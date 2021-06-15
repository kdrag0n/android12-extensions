package dev.kdrag0n.android12ext.core.data

import android.content.Context
import android.content.pm.PackageManager

fun Context.hasSystemUiGoogle(): Boolean {
    val apkPath = packageManager.getPackageInfo("com.android.systemui", PackageManager.GET_META_DATA)
        .applicationInfo.sourceDir

    return "SystemUIGoogle" in apkPath
}

fun Context.hasPixelLauncher(): Boolean {
    return try {
        packageManager.getPackageInfo("com.google.android.apps.nexuslauncher", 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}
