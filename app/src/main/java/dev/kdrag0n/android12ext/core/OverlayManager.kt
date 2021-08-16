package dev.kdrag0n.android12ext.core

import android.content.Context
import com.topjohnwu.superuser.Shell
import dagger.Reusable
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.kdrag0n.android12ext.data.SettingsRepository
import dev.kdrag0n.android12ext.data.hasSystemUiGoogle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Reusable
class OverlayManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepo: SettingsRepository,
) {
    suspend fun updateCircleOverlay() {
        // AOSP only
        if (context.hasSystemUiGoogle()) {
            return
        }

        withContext(Dispatchers.IO) {
            val action = if (settingsRepo.prefs.getBoolean("aosp_circle_icons2_enabled", false)) {
                "enable"
            } else {
                "disable"
            }

            Shell.su("cmd overlay $action com.android.theme.icon.circle").submit()
        }
    }
}
