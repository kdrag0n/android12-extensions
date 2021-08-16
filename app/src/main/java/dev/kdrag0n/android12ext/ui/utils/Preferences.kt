package dev.kdrag0n.android12ext.ui.utils

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.helpers.categoryHeader
import de.Maxr1998.modernpreferences.helpers.onClick
import de.Maxr1998.modernpreferences.helpers.pref
import de.Maxr1998.modernpreferences.helpers.switch
import de.Maxr1998.modernpreferences.preferences.SwitchPreference
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.data.SettingsRepository
import dev.kdrag0n.android12ext.ui.NavViewModel
import dev.kdrag0n.android12ext.ui.settings.BaseSettingsViewModel
import kotlinx.coroutines.launch

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
    @DrawableRes icon: Int? = null,
    @IdRes action: Int,
    dependency: String? = null,
    vm: NavViewModel,
) {
    pref("nav_${key}_enabled") {
        titleRes = title
        if (summary != null) {
            summaryRes = summary
        }
        if (icon != null) {
            iconRes = icon
        }
        persistent = false
        this.dependency = dependency

        onClick {
            vm.navDest.value = action
            false
        }
    }
}

fun PreferenceScreen.Appendable.telemetryPrefs(
    viewModel: BaseSettingsViewModel,
    settingsRepo: SettingsRepository,
) {
    categoryHeader("telemetry") {
        titleRes = R.string.appearance_advanced_telemetry
    }
    pref("send_settings_report") {
        titleRes = R.string.appearance_advanced_send_settings_report
        summaryRes = R.string.appearance_advanced_send_settings_report_desc
        onClick {
            viewModel.viewModelScope.launch {
                val resp = settingsRepo.reportSettings()
                viewModel.settingsReportStatus.value = resp.getOrNull()?.isSuccessful ?: false
            }
            false
        }
    }
}
