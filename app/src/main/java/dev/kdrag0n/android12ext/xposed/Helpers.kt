package dev.kdrag0n.android12ext.xposed

import android.annotation.SuppressLint
import android.content.Context
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import dev.kdrag0n.android12ext.utils.call
import dev.kdrag0n.android12ext.utils.callStatic
import dev.kdrag0n.android12ext.utils.getClass
import java.lang.reflect.Member

fun XC_LoadPackage.LoadPackageParam.hookMethod(
    className: String,
    hook: XC_MethodHook,
    methodName: String,
    vararg argTypes: Class<*>
) {
    getClass(className)
        .getDeclaredMethod(methodName, *argTypes)
        .let { method ->
            XposedBridge.hookMethod(method, hook)
        }
}

// OverlayManagerService has no public constant
@SuppressLint("WrongConstant")
fun Context.setOverlayEnabled(lpparam: XC_LoadPackage.LoadPackageParam, overlay: String, enabled: Boolean) {
    val classOverlayIdentifier = lpparam.getClass("android.content.om.OverlayIdentifier")
    val overlayId = classOverlayIdentifier.callStatic("fromString", overlay)!!

    val txBuilder = lpparam.getClass("android.content.om.OverlayManagerTransaction\$Builder").newInstance()
    txBuilder.call("setEnabled", overlayId, enabled, 0)
    val tx = txBuilder.call("build")!!

    val oms = getSystemService("overlay")
    oms.call("commit", tx)
}

// Use reflection to call XposedBridge#deoptimizeMethod(Member) because it's not part of the
// original Xposed API
fun deoptimizeMethod(member: Member) {
    XposedBridge::class.java.getDeclaredMethod("deoptimizeMethod", Member::class.java)
        .invoke(null, member)
}
