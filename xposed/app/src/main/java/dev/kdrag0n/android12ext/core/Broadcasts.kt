package dev.kdrag0n.android12ext.core

import android.content.Context
import android.content.Intent

object Broadcasts {
    const val PERMISSION = "dev.kdrag0n.android12ext.BROADCAST_PERMISSION"

    const val RELOAD_ACTION = "dev.kdrag0n.android12ext.RELOAD_SETTINGS"
    const val RELOAD_DEBOUNCE_DELAY = 1500L
    const val RELOAD_WARNING_DURATION = 1500
    const val RELOAD_RESTART_DELAY = 1000L
}

fun Context.sendBroadcast(action: String) {
    Intent().let {
        it.action = action
        sendBroadcast(it)
    }
}