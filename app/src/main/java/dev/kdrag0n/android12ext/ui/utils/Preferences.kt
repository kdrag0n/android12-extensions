package dev.kdrag0n.android12ext.ui.utils

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.helpers.onClick
import de.Maxr1998.modernpreferences.helpers.pref
import de.Maxr1998.modernpreferences.helpers.switch
import de.Maxr1998.modernpreferences.preferences.SwitchPreference
import dev.kdrag0n.android12ext.ui.NavViewModel

fun Preference.setInteractive(interactive: Boolean) {
    if (!interactive) {
        // This intentionally blocks touches and clicks to prevent interactivity
        @SuppressLint("ClickableViewAccessibility")
        preBindListener = Preference.OnPreBindListener { _, holder ->
            holder.itemView.setOnTouchListener { _, _ ->
                true
            }
        }
    }
}

fun PreferenceScreen.Appendable.featureSwitch(
    key: String,
    @StringRes title: Int,
    @StringRes summary: Int,
    @DrawableRes icon: Int,
    default: Boolean = true,
    dependency: String? = null,
    enabled: Boolean = true,
    block: SwitchPreference.() -> Unit = { },
) {
    switch("${key}_enabled") {
        titleRes = title
        summaryRes = summary
        iconRes = icon
        defaultValue = default
        this.dependency = dependency
        this.enabled = enabled
        block()
    }
}

fun PreferenceScreen.Appendable.navPref(
    key: String,
    @StringRes title: Int,
    @StringRes summary: Int? = null,
    @DrawableRes icon: Int,
    @IdRes action: Int,
    vm: NavViewModel,
    dependency: String? = null,
) {
    pref("nav_${key}_enabled") {
        titleRes = title
        if (summary != null) {
            summaryRes = summary
        }
        iconRes = icon
        persistent = false
        this.dependency = dependency

        onClick {
            vm.navDest.value = action
            false
        }
    }
}
