package dev.kdrag0n.android12ext.data

import android.content.Context
import android.content.SharedPreferences
import dagger.Reusable
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.helpers.screen
import dev.kdrag0n.android12ext.xposed.XposedPreferenceProvider
import javax.inject.Inject
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

@Reusable
class SettingsRepository @Inject constructor(
    @DeviceProtected val context: Context,
) {
    val prefs: SharedPreferences = context
        .getSharedPreferences(XposedPreferenceProvider.DEFAULT_PREFS, Context.MODE_PRIVATE)

    inline fun prefScreen(block: PreferenceScreen.Builder.() -> Unit) =
        screen(context, block)

    companion object {
        private const val WHITE_LUMINANCE_MIN = 1.0
        private const val WHITE_LUMINANCE_MAX = 10000.0
        const val WHITE_LUMINANCE_USER_MAX = 1000
        const val WHITE_LUMINANCE_USER_STEP = 25 // both max and default must be divisible by this
        const val WHITE_LUMINANCE_USER_DEFAULT = 425 // ~200.0 divisible by step (decoded = 199.526)

        private fun parseWhiteLuminanceUser(userValue: Int): Double {
            val userSrc = userValue.toDouble() / WHITE_LUMINANCE_USER_MAX
            val userInv = 1.0 - userSrc
            return (10.0).pow(userInv * log10(WHITE_LUMINANCE_MAX))
                .coerceAtLeast(WHITE_LUMINANCE_MIN)
        }

        private fun encodeWhiteLuminanceUser(luminance: Double): Int {
            val userInv = log10(luminance.coerceAtLeast(WHITE_LUMINANCE_MIN)) /
                    log10(WHITE_LUMINANCE_MAX)
            return ((1.0 - userInv) * WHITE_LUMINANCE_USER_MAX).roundToInt()
        }

        // TODO: make this an instance method
        fun getWhiteLuminance(prefs: SharedPreferences): Double {
            val userValue = prefs.getInt("custom_monet_zcam_white_luminance_user", WHITE_LUMINANCE_USER_DEFAULT)
            return parseWhiteLuminanceUser(userValue)
        }
    }
}
