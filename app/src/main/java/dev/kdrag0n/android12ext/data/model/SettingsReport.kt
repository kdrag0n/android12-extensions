package dev.kdrag0n.android12ext.data.model

data class SettingsReport(
    // Anonymous install ID
    val ssaid: String,
    // App info
    val versionCode: Int,
    // Settings
    val settings: Map<String, Any>,
)
