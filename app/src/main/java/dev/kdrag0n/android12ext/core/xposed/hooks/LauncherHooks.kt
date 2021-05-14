package dev.kdrag0n.android12ext.core.xposed.hooks

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import timber.log.Timber

object LauncherHooks {
    // com.android.launcher3.config.FeatureFlags$BooleanFlag
    private const val BOOLEAN_FLAG_CLASS = "T.a"
    // BooleanFlag.mCurrentValue
    private const val CURRENT_VALUE_FIELD = "c"

    val flagValues = mapOf(
        //"ENABLE_DATABASE_RESTORE" to true,
        //"ENABLE_SMARTSPACE_UNIVERSAL" to true,
        "ENABLE_SMARTSPACE_ENHANCED" to true,
        //"ALWAYS_USE_HARDWARE_OPTIMIZATION_FOR_FOLDER_ANIMATIONS" to true,
        //"SEPARATE_RECENTS_ACTIVITY" to true,
        //"ENABLE_MINIMAL_DEVICE" to true,
        "EXPANDED_SMARTSPACE" to true,
        "ENABLE_FOUR_COLUMNS" to true,
        "ENABLE_LAUNCHER_ACTIVITY_THEME_CROSSFADE" to true,
        "ENABLE_SPLIT_SELECT" to true,
        "PROMISE_APPS_IN_ALL_APPS" to true,
        //"UNSTABLE_SPRINGS" to true,
        "KEYGUARD_ANIMATION" to true,
        "ENABLE_QUICKSTEP_LIVE_TILE" to true,
        "ENABLE_DEVICE_SEARCH" to true,
        //"FORCE_LOCAL_OVERSCROLL_PLUGIN" to true,
        //"ASSISTANT_GIVES_LAUNCHER_FOCUS" to true,
        //"HOTSEAT_MIGRATE_TO_FOLDER" to true,
        "ENABLE_WIDGETS_PICKER_AIAI_SEARCH" to true,
        //"ENABLE_OVERVIEW_SHARE" to true,
        "ENABLE_OVERVIEW_SHARING_TO_PEOPLE" to true,
        "ENABLE_OVERVIEW_CONTENT_PUSH" to true,

        // Tablets only
        "ENABLE_TASKBAR" to true,
        "ENABLE_OVERVIEW_GRID" to true,
        "ENABLE_TWO_PANEL_HOME" to true,
    )

    fun applyFeatureFlags(lpparam: XC_LoadPackage.LoadPackageParam) {
        val hook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val key = param.args[0] as String
                Timber.i("Hooking launcher flag: $key")
                XposedHelpers.setBooleanField(param.thisObject, CURRENT_VALUE_FIELD, flagValues[key] ?: return)
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
}