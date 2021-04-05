package dev.kdrag0n.android12ext.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import dev.kdrag0n.android12ext.BuildConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber

object Broadcasts {
    private const val SYSUI_PERMISSION = "com.android.systemui.permission.SELF"
    const val MANAGER_PERMISSION = "${BuildConfig.APPLICATION_ID}.BROADCAST_PERMISSION"

    const val RELOAD_ACTION = "${BuildConfig.APPLICATION_ID}.RELOAD_SETTINGS"
    const val RELOAD_DEBOUNCE_DELAY = 1500L
    const val RELOAD_WARNING_DURATION = 1500
    const val RELOAD_RESTART_DELAY = 1000L

    private const val PING_ACTION = "${BuildConfig.APPLICATION_ID}.REMOTE_PING"
    private const val PONG_ACTION = "${BuildConfig.APPLICATION_ID}.REMOTE_PONG"
    private const val PING_TIMEOUT = 50L
    private val pingLock = Mutex()

    suspend fun pingSysUi(context: Context): Boolean {
        Timber.i("Pinging System UI")

        // Without a lock, multiple pings at the same time could break
        pingLock.withLock {
            // Register pong receiver first to avoid race
            var pongReceived = false
            val pongReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    Timber.d("Received pong broadcast")
                    pongReceived = true
                }
            }

            Timber.d("Registering pong receiver")
            context.registerReceiver(pongReceiver, IntentFilter(PONG_ACTION), SYSUI_PERMISSION, null)
            try {
                Timber.d("Sending ping broadcast")
                context.sendBroadcast(Intent(PING_ACTION), SYSUI_PERMISSION)

                // Simple, naïve timeout because we don't need to return ASAP
                delay(PING_TIMEOUT)
                Timber.d("System UI ping result = $pongReceived")
                return pongReceived
            } finally {
                Timber.d("Unregistering pong receiver")
                context.unregisterReceiver(pongReceiver)
            }
        }
    }

    fun listenForPings(context: Context) {
        val pingReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Timber.d("Received ping, sending pong broadcast")
                context.sendBroadcast(Intent(PONG_ACTION), MANAGER_PERMISSION)
            }
        }

        Timber.d("Registering ping receiver")
        context.registerReceiver(pingReceiver, IntentFilter(PING_ACTION), MANAGER_PERMISSION, null)
    }
}