package expo.modules.liveupdates

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

const val TAG = "LiveUpdatesForegroundService"
const val CHANNEL_ID = "LiveUpdatesServiceChannel"
const val NOTIFICATION_ID = 1

class LiveUpdatesForegroundService : Service() {
    val broadcastReceiver =
        object : BroadcastReceiver() {
            @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
            override fun onReceive(
                context: Context,
                intent: Intent
            ) {
                Log.i(
                    TAG,
                    "Intent received  ${intent.action}"
                )
                when (intent.action) {
                    ServiceAction.updateDistance -> updateNotificationContent(intent.extras)
                }
            }
        }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(
        intent: Intent,
        flags: Int,
        startId: Int
    ): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.registerReceiver(
                broadcastReceiver,
                IntentFilter(ServiceAction.updateDistance),
                RECEIVER_EXPORTED
            )
        }
        val notification = createNotification("Starting Live Updates...", "")
        startForeground(NOTIFICATION_ID, notification)

        // Perform your long-running task here
        return START_STICKY
    }

    override fun onDestroy() {
        this.unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun updateNotificationContent(extras: Bundle?) {
        Log.i(TAG, "Update notification")

        val text = extras?.getString(ServiceActionExtra.setText) ?: "[text placeholder]"
        val title = extras?.getString(ServiceActionExtra.setTitle) ?: "[title placeholder]"

        val notification = createNotification(title, text)
        val notificationManager = NotificationManagerCompat.from(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

    private fun createNotification(
        title: String,
        text: String,
    ): Notification {
        val notificationIntent =
            Intent(
                "android.intent.action.MAIN"
            )
        notificationIntent.setComponent(
            ComponentName(
                "expo.modules.liveupdates.example",
                "expo.modules.liveupdates.example.MainActivity"
            )
        )
        notificationIntent.addCategory("android.intent.category.LAUNCHER")
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val notificationBuilder =
            NotificationCompat
                .Builder(
                    this,
                    CHANNEL_ID
                ).setContentTitle(title)
                .setSmallIcon(android.R.drawable.star_on)
                .setProgress(100, 40, false)
                .setContentText(text)
                .setColor(0x00238440)
                .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            notificationBuilder.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
        }
        return notificationBuilder.build()
    }
}
