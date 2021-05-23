package dev.kdrag0n.android12ext.core.data

import android.content.Context
import android.content.pm.PackageManager

fun Context.hasSystemUiGoogle(): Boolean {
    val apkPath = packageManager.getPackageInfo("com.android.systemui", PackageManager.GET_META_DATA)
        .applicationInfo.sourceDir

    return "SystemUIGoogle" in apkPath
}
