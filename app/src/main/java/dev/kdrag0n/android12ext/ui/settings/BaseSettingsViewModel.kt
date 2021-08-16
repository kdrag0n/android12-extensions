package dev.kdrag0n.android12ext.ui.settings

import de.Maxr1998.modernpreferences.PreferencesAdapter
import dev.kdrag0n.android12ext.ui.NavViewModel

abstract class BaseSettingsViewModel : NavViewModel() {
    abstract val prefAdapter: PreferencesAdapter
}
