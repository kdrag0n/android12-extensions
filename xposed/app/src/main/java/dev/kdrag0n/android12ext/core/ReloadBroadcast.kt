package dev.kdrag0n.android12ext.core

import android.content.Context
import android.content.Intent

const val BROADCAST_PERMISSION = "dev.kdrag0n.android12ext.BROADCAST_PERMISSION"
const val RELOAD_BROADCAST_ACTION = "dev.kdrag0n.android12ext.RELOAD_SETTINGS"

const val RELOAD_DEBOUNCE_DELAY = 1500L
const val RELOAD_WARNING_DURATION = 1500
const val RELOAD_RESTART_DELAY = 1000L

fun Context.sendReloadBroadcast() {
    Intent().run {
        action = RELOAD_BROADCAST_ACTION
        sendBroadcast(this)
    }
}