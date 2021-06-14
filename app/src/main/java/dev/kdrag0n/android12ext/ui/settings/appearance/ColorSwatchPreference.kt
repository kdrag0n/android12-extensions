package dev.kdrag0n.android12ext.ui.settings.appearance

import android.content.res.ColorStateList
import android.view.View
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter
import dev.kdrag0n.android12ext.R

class ColorSwatchPreference(key: String) : Preference(key) {
    override fun getWidgetLayoutResource() = R.layout.color_sample

    override fun bindViews(holder: PreferencesAdapter.ViewHolder) {
        super.bindViews(holder)

        val color = getInt(-1)
        if (!enabled || color == -1) {
            holder.widget!!.visibility = View.INVISIBLE
        } else {
            holder.widget!!.backgroundTintList = ColorStateList.valueOf(color)
        }
    }
}

inline fun PreferenceScreen.Appendable.colorPref(key: String, block: ColorSwatchPreference.() -> Unit): ColorSwatchPreference {
    return ColorSwatchPreference(key).apply(block).also(::addPreferenceItem)
}
