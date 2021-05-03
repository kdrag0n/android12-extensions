package dev.kdrag0n.android12ext.core.xposed

import com.crossbowffs.remotepreferences.RemotePreferenceFile
import com.crossbowffs.remotepreferences.RemotePreferenceProvider
import dev.kdrag0n.android12ext.BuildConfig

class XposedPreferenceProvider : RemotePreferenceProvider(
    AUTHORITY,
    arrayOf(
        RemotePreferenceFile(DEFAULT_PREFS, true)
    )
) {
    override fun checkAccess(prefFileName: String, prefKey: String, write: Boolean): Boolean {
        // Only allow remote reads
        return !write
    }

    companion object {
        const val AUTHORITY = "dev.kdrag0n.android12ext.xposedpreferences"
        const val DEFAULT_PREFS = "${BuildConfig.APPLICATION_ID}_preferences"
    }
}