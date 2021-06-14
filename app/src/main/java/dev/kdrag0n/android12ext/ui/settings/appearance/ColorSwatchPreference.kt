package dev.kdrag0n.android12ext.ui.settings.appearance

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter
import dev.kdrag0n.android12ext.R

class ColorSwatchPreference(key: String) : Preference(key) {
    override fun getWidgetLayoutResource() = R.layout.color_sample

    override fun bindViews(holder: PreferencesAdapter.ViewHolder) {
        super.bindViews(holder)

        val color = getInt(Color.BLUE)
        if (enabled) {
            holder.widget!!.backgroundTintList = ColorStateList.valueOf(color)
        } else {
            holder.widget!!.visibility = View.INVISIBLE
        }
    }
}

inline fun PreferenceScreen.Appendable.colorPref(key: String, block: ColorSwatchPreference.() -> Unit): ColorSwatchPreference {
    return ColorSwatchPreference(key).apply(block).also(::addPreferenceItem)
}
