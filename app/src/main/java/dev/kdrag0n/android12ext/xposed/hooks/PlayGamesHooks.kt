package dev.kdrag0n.android12ext.xposed.hooks

import android.os.Build

class PlayGamesHooks {
    fun applyPreviewSdk() {
        val field = Build.VERSION::class.java.getDeclaredField("PREVIEW_SDK_INT")
        field.isAccessible = true
        field.set(null, 0)
    }
}
