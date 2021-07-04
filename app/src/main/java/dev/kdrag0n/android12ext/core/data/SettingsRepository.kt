package dev.kdrag0n.android12ext.core.data

import android.content.Context
import android.content.SharedPreferences
import dagger.Reusable
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.helpers.screen
import dev.kdrag0n.android12ext.core.xposed.XposedPreferenceProvider
import javax.inject.Inject

@Reusable
class SettingsRepository @Inject constructor(
    @DeviceProtected val context: Context,
) {
    val prefs: SharedPreferences = context
        .getSharedPreferences(XposedPreferenceProvider.DEFAULT_PREFS, Context.MODE_PRIVATE)

    inline fun prefScreen(block: PreferenceScreen.Builder.() -> Unit) =
        screen(context, block)
}
