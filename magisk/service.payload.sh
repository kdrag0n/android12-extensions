#!/system/bin/sh

set_flags() {
    # Scrolling screenshots
    device_config put systemui enable_screenshot_scrolling true

    # Restore quick wallet from Android 11
    device_config put systemui quick_access_wallet_enabled true

    # Privacy indicators and dashboard (same as Android 11 qpr1)
    # Only dashboard works on DP1
    device_config put privacy location_indicators_enabled true
    device_config put privacy permissions_hub_enabled true
    device_config put privacy permissions_hub_2_enabled true
    device_config put privacy camera_mic_icons_enabled true

    # Camera and microphone toggles (QS tiles)
    device_config put privacy camera_toggle_enabled true
    device_config put privacy mic_toggle_enabled true

    # Machine-learning back gesture
    device_config put systemui use_back_gesture_ml_model true
    device_config put systemui back_gesture_ml_model_threshold 0.9

    # New PiP features
    device_config put systemui pip_pinch_resize true
    device_config put systemui pip_stashing true

    # Automatic notification ranking
    settings put global notification_feedback_enabled 1

    # Remove partial notification swipe actions
    settings put global show_new_notif_dismiss 1

    # 1 = with mirror (preview), 2 = without mirror
    settings put secure sysui_thick_brightness 1
}

while true
do
    set_flags

    if ps -Ao CMD | grep -q ndroid.systemui; then
        break
    fi

    sleep 0.1
done
