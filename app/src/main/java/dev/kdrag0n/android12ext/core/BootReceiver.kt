package dev.kdrag0n.android12ext.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    @Inject lateinit var overlayManager: OverlayManager

    @DelicateCoroutinesApi
    override fun onReceive(context: Context, intent: Intent) {
        // Defer until after CE unlock so the user can allow root
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Timber.i("Updating circle overlay state")

            GlobalScope.launch {
                overlayManager.updateCircleOverlay()
            }
        }
    }
}
