package dev.kdrag0n.android12ext.ui.settings

import android.app.Application
import de.Maxr1998.modernpreferences.PreferencesAdapter
import dev.kdrag0n.android12ext.ui.NavViewModel

abstract class BaseSettingsViewModel(app: Application) : NavViewModel(app) {
    abstract val prefAdapter: PreferencesAdapter
}