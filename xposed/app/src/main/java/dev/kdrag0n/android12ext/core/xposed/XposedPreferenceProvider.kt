package dev.kdrag0n.android12ext.core.xposed

import androidx.lifecycle.MutableLiveData
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
        // Check for inclusion first to avoid posting too many unnecessary LiveData updates that
        // slow the UI down
        if (clientPackage != null && clientPackage !in clientsSet) {
            clientsSet += clientPackage
            clientsSeen.postValue(clientsSet)
        }

        // Only allow remote reads
        return !write
    }

    companion object {
        const val AUTHORITY = "dev.kdrag0n.android12ext.xposedpreferences"
        const val DEFAULT_PREFS = "${BuildConfig.APPLICATION_ID}_preferences"

        // Used by app UI
        private val clientsSet: MutableSet<String> = ConcurrentHashMap.newKeySet()
        val clientsSeen: MutableLiveData<Set<String>> = MutableLiveData(clientsSet)
    }
}