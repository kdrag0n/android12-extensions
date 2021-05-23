package dev.kdrag0n.android12ext.core.xposed

import android.app.Instrumentation
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.crossbowffs.remotepreferences.RemotePreferences
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import dev.kdrag0n.android12ext.core.BroadcastManager
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