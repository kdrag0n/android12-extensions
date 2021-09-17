package dev.kdrag0n.android12ext.xposed

import android.app.Instrumentation
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.crossbowffs.remotepreferences.RemotePreferences
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import dev.kdrag0n.android12ext.BuildConfig
import dev.kdrag0n.android12ext.core.BroadcastManager
import timber.log.Timber
import kotlin.system.exitProcess

// This entry point handles Context, SharedPreferences, and broadcast setup.
class XposedEntryPoint : IXposedHookLoadPackage {
    private lateinit var hook: XposedHook

    private val reloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            exitProcess(0)
        }
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "dev.kdrag0n.android12ext") {
            val hook2 = object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    Log.d("DebugHook", "SARU beforeHookedMethod @ ${param.thisObject::class.java.name} / ${param.method}")
                }

                override fun afterHookedMethod(param: MethodHookParam) {
                    Log.d("DebugHook", "SARU afterHookedMethod @ ${param.thisObject::class.java.name} / ${param.method}")
                }
            }
            //val createAnimationProperties = Class.forName("android.graphics.drawable.RippleDrawable")
            //    .getDeclaredMethod("createAnimationProperties", Float::class.java, Float::class.java, Float::class.java, Float::class.java, Float::class.java, Float::class.java)
            //XposedBridge.hookMethod(createAnimationProperties, hook2)
            XposedBridge.hookAllConstructors(Class.forName("android.graphics.drawable.RippleShader"), hook2)
            XposedBridge.hookAllConstructors(Class.forName("android.graphics.RuntimeShader"), hook2)
            XposedBridge.hookAllConstructors(Class.forName("android.graphics.Shader"), hook2)
            Timber.d("SARU installed second hook")
        } else {
            return
        }

        // Don't hook system_sever unless it's for debugging.
        // Users should never need it, and it makes reloads much more disruptive.
        // XposedHook isn't very reliable in system_server, so check it here instead.
        if (!BuildConfig.DEBUG && lpparam.packageName == "android") {
            return
        }

        val contextHook = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                // Make sure we don't initialize twice
                if (::hook.isInitialized) {
                    return
                }

                val context = param.result as Context

                val broadcastManager = BroadcastManager(context)
                context.registerReceiver(
                    reloadReceiver,
                    IntentFilter(BroadcastManager.RELOAD_ACTION),
                    BroadcastManager.MANAGER_PERMISSION,
                    null
                )

                val prefs = RemotePreferences(
                    context,
                    XposedPreferenceProvider.AUTHORITY,
                    XposedPreferenceProvider.DEFAULT_PREFS,
                    true
                )

                hook = XposedHook(context, lpparam, prefs, broadcastManager)
                hook.applyAll()
            }
        }

        // Wait to get a Context reference before initializing other hooks
        XposedBridge.hookAllMethods(
            Instrumentation::class.java,
            "newApplication",
            contextHook,
        )
    }
}
