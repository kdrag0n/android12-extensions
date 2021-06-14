package dev.kdrag0n.android12ext.core.xposed.hooks

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import timber.log.Timber

class LauncherHooks(
    private val lpparam: XC_LoadPackage.LoadPackageParam,
) {
    val flagValues = mutableMapOf<String, Boolean>()

    fun applyFeatureFlags() {
        val hook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val key = param.args[0] as String
                Timber.i("Hooking launcher flag: $key")
                XposedHelpers.setBooleanField(param.thisObject, VALUE_FIELD, flagValues[key] ?: return)
            }
        }

        XposedHelpers.findAndHookConstructor(
            BOOLEAN_FLAG_CLASS,
            lpparam.classLoader,
            String::class.java,
            Boolean::class.java,
            hook,
        )
    }

    companion object {
        private const val BOOLEAN_FLAG_CLASS = "com.android.launcher3.config.FeatureFlags\$BooleanFlag"
        private const val VALUE_FIELD = "defaultValue"
    }
}
