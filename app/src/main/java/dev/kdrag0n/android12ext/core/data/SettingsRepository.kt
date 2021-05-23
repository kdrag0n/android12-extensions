package dev.kdrag0n.android12ext.core.data

import android.content.Context
import android.content.SharedPreferences
import dev.kdrag0n.android12ext.core.xposed.XposedPreferenceProvider

class SettingsRepository(
    context: Context,
) {
    val prefs: SharedPreferences = context.createDeviceProtectedStorageContext()
        .getSharedPreferences(XposedPreferenceProvider.DEFAULT_PREFS, Context.MODE_PRIVATE)
}
