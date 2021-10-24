package dev.kdrag0n.android12ext.xposed.hooks

import de.robv.android.xposed.callbacks.XC_LoadPackage
import dev.kdrag0n.android12ext.utils.setBool
import timber.log.Timber

class LauncherHooks(
    lpparam: XC_LoadPackage.LoadPackageParam,
) : BaseHooks(lpparam) {
    val flagValues = mutableMapOf<String, Boolean>()

    fun applyFeatureFlags() {
        hookAfterCons(BOOLEAN_FLAG_CLASS, String::class.java, Boolean::class.java) {
            val key = args[0] as String
            Timber.i("Hooking launcher flag: $key")
            thisObject.setBool(VALUE_FIELD, flagValues[key] ?: return@hookAfterCons)
        }
    }

    companion object {
        private const val BOOLEAN_FLAG_CLASS = "com.android.launcher3.config.FeatureFlags\$BooleanFlag"
        private const val VALUE_FIELD = "defaultValue"
    }
}
