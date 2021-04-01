package dev.kdrag0n.android12ext.core

import com.crossbowffs.remotepreferences.RemotePreferenceFile
import com.crossbowffs.remotepreferences.RemotePreferenceProvider
import dev.kdrag0n.android12ext.BuildConfig
import java.util.concurrent.ConcurrentHashMap

class XposedPreferenceProvider : RemotePreferenceProvider(
    AUTHORITY,
    arrayOf(
        RemotePreferenceFile(DEFAULT_PREFS, true)
    )
) {
    override fun checkAccess(prefFileName: String, prefKey: String, write: Boolean): Boolean {
        // Record client access
        val clientPackage = callingPackage
        if (clientPackage != null) {
            clientsSeen += clientPackage
        }

        // Only allow remote reads
        return !write
    }

    companion object {
        const val AUTHORITY = "dev.kdrag0n.android12ext.xposedpreferences"
        const val DEFAULT_PREFS = "${BuildConfig.APPLICATION_ID}_preferences"

        // Used by app UI
        val clientsSeen: MutableSet<String> = ConcurrentHashMap.newKeySet()
    }
}