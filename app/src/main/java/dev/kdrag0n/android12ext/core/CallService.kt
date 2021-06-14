package dev.kdrag0n.android12ext.core

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import dev.kdrag0n.android12ext.R
import dev.kdrag0n.android12ext.ui.main.MainActivity

@RequiresApi(31)
class CallService : Service() {
    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationManager = getSystemService<NotificationManager>()!!

        // Channel
        val channel = NotificationChannel("test", "Test", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)

        // Caller
        val caller = Person.Builder().run {
            setName("John Doe")
            setImportant(true)
            build()
        }

        // Notification
        val notification = Notification.Builder(this, "test").run {
            val pendingIntent = Intent(this@CallService, MainActivity::class.java).let {
                PendingIntent.getActivity(this@CallService, 100, it, 0)
            }

            setContentIntent(pendingIntent)
            setSmallIcon(R.drawable.ic_fluent_call_24_filled)
            style = Notification.CallStyle.forOngoingCall(caller, pendingIntent)
            addPerson(caller)
            build()
        }

        // Post
        startForeground(1, notification)

        return super.onStartCommand(intent, flags, startId)
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, CallService::class.java)
            context.startForegroundService(intent)
        }
    }
}
