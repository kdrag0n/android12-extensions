package dev.kdrag0n.android12ext.core.hooks

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers

object SystemUIHooks {
    val featureFlag = object : XC_MethodReplacement() {
        override fun replaceHookedMethod(param: MethodHookParam) = true
    }

    val gameDashboard = object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            param.thisObject.javaClass.getDeclaredField("DISABLED").let {
                it.isAccessible = true
                it.set(null, java.lang.Boolean.FALSE)
            }

            XposedHelpers.setBooleanField(param.thisObject, "mShouldShow", true)
        }
    }

    val roundedScreenshot = object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            param.thisObject.javaClass.getDeclaredField("DEBUG_COLOR").let {
                it.isAccessible = true
                it.setBoolean(null, false)
            }
        }
    }

    val privacyIndicators = object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            XposedHelpers.setBooleanField(param.thisObject, "allIndicatorsAvailable", true)
            XposedHelpers.setBooleanField(param.thisObject, "micCameraAvailable", true)
            XposedHelpers.setBooleanField(param.thisObject, "locationAvailable", true)
        }
    }
}