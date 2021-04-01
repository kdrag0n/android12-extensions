package dev.kdrag0n.android12ext.ui

import android.content.SharedPreferences
import de.Maxr1998.modernpreferences.PreferenceScreen

// We need this in order to use device-encrypted preferences with ModernAndroidPreferences
fun PreferenceScreen.Builder.buildWithPrefs(prefs: SharedPreferences): PreferenceScreen {
    javaClass.getDeclaredField("prefs").let { field ->
        field.isAccessible = true
        field.set(this, prefs)
    }

    javaClass.getDeclaredField("context").let { field ->
        field.isAccessible = true
        field.set(this, null)
    }

    return PreferenceScreen::class.java.getDeclaredConstructor(PreferenceScreen.Builder::class.java)
        .newInstance(this)
}