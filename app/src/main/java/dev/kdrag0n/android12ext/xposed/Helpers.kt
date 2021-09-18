package dev.kdrag0n.android12ext.xposed

import android.annotation.SuppressLint
import android.content.Context
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.lang.reflect.Member

fun XC_LoadPackage.LoadPackageParam.hookMethod(
    className: String,
    hook: XC_MethodHook,
    methodName: String,
    vararg argTypes: Class<*>
) {
    XposedHelpers.findClass(className, classLoader)
        .getDeclaredMethod(methodName, *argTypes)
        .let { method ->
            XposedBridge.hookMethod(method, hook)
        }
}

// OverlayManagerService has no public constant
@SuppressLint("WrongConstant")
fun Context.setOverlayEnabled(lpparam: XC_LoadPackage.LoadPackageParam, overlay: String, enabled: Boolean) {
    val classOverlayIdentifier = XposedHelpers.findClass("android.content.om.OverlayIdentifier", lpparam.classLoader)
    val overlayId = XposedHelpers.callStaticMethod(classOverlayIdentifier, "fromString", overlay)

    val txBuilder = XposedHelpers.newInstance(
        XposedHelpers.findClass("android.content.om.OverlayManagerTransaction\$Builder", lpparam.classLoader),
    )
    XposedHelpers.callMethod(
        txBuilder,
        "setEnabled",
        overlayId,
        enabled,
        0,
    )
    val tx = XposedHelpers.callMethod(txBuilder, "build")

    val oms = getSystemService("overlay")
    XposedHelpers.callMethod(oms, "commit", tx)
}

// Use reflection to call XposedBridge#deoptimizeMethod(Member) because it's not part of the
// original Xposed API
fun deoptimizeMethod(member: Member) {
    XposedBridge::class.java.getDeclaredMethod("deoptimizeMethod", Member::class.java)
        .invoke(null, member)
}
