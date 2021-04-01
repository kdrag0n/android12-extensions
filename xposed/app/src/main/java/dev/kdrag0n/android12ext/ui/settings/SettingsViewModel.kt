package dev.kdrag0n.android12ext.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.*
import dev.kdrag0n.android12ext.R

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    val prefAdapter = PreferencesAdapter(screen(app) {
        switch("global_enabled") {
            titleRes = R.string.enabled
            summaryRes = R.string.enabled_desc
            iconRes = R.drawable.ic_fluent_checkmark_circle_24_regular
            defaultValue = true
        }

        categoryHeader("features") {
            titleRes = R.string.features
        }
        switch("monet_enabled") {
            titleRes = R.string.feature_monet
            summaryRes = R.string.feature_monet_desc
            iconRes = R.drawable.ic_fluent_paint_brush_24_regular
            defaultValue = true
        }
        switch("people_enabled") {
            titleRes = R.string.feature_people
            summaryRes = R.string.feature_people_desc
            iconRes = R.drawable.ic_fluent_people_24_regular
            defaultValue = true
        }
        switch("lockscreen_enabled") {
            titleRes = R.string.feature_lockscreen
            summaryRes = R.string.feature_lockscreen_desc
            iconRes = R.drawable.ic_fluent_lock_closed_24_regular
            defaultValue = true
        }
        switch("notification_shade_enabled") {
            titleRes = R.string.feature_notification_shade
            summaryRes = R.string.feature_notification_shade_desc
            iconRes = R.drawable.ic_fluent_alert_24_regular
            defaultValue = true
        }
        switch("toast_enabled") {
            titleRes = R.string.feature_toast
            summaryRes = R.string.feature_toast_desc
            iconRes = R.drawable.ic_fluent_badge_24_regular
            defaultValue = true
        }
        switch("game_dashboard_enabled") {
            titleRes = R.string.feature_game_dashboard
            summaryRes = R.string.feature_game_dashboard_desc
            iconRes = R.drawable.ic_fluent_games_24_regular
            defaultValue = true
        }
        switch("privacy_indicators_enabled") {
            titleRes = R.string.feature_privacy_indicators
            summaryRes = R.string.feature_privacy_indicators_desc
            iconRes = R.drawable.ic_fluent_inprivate_account_24_regular
            defaultValue = true
        }

        categoryHeader("tweaks") {
            titleRes = R.string.tweaks
        }
        switch("patterned_ripple_enabled") {
            titleRes = R.string.tweak_patterned_ripple
            summaryRes = R.string.tweak_patterned_ripple_desc
            iconRes = R.drawable.ic_fluent_tap_single_24_regular
            defaultValue = true
        }
        switch("overscroll_bounce_enabled") {
            titleRes = R.string.tweak_overscroll_bounce
            summaryRes = R.string.tweak_overscroll_bounce_desc
            iconRes = R.drawable.ic_fluent_phone_vertical_scroll_24_regular
            defaultValue = true
        }
    })
}