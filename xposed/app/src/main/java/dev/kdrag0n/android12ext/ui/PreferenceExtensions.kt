package dev.kdrag0n.android12ext.ui

import android.content.SharedPreferences
import androidx.annotation.DrawableRes
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.helpers.onClick
import de.Maxr1998.modernpreferences.preferences.TwoStatePreference

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

private val prefsField by lazy(LazyThreadSafetyMode.NONE) {
    Preference::class.java.getDeclaredField("prefs").also { field ->
        field.isAccessible = true
    }
}

fun TwoStatePreference.setDynamicIcon(@DrawableRes regularRes: Int, @DrawableRes filledRes: Int) {
    preBindListener = Preference.OnPreBindListener { preference, _ ->
        val prefs = prefsField.get(this) as SharedPreferences

        preference.iconRes = if (prefs.getBoolean(key, defaultValue) &&
            (dependency == null || prefs.getBoolean(dependency, true))) {
            filledRes
        } else {
            regularRes
        }
    }

    // Rebind on update
    onClick {
        true
    }
}