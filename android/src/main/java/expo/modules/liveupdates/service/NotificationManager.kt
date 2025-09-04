package expo.modules.liveupdates

import android.content.Context
import android.content.Intent
import android.os.Build

class NotificationManager(
    private var context: Context
) {
    fun startForegroundService() {
        val serviceIntent = Intent(context, LiveUpdatesForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    fun stopForegroundService() {
        val serviceIntent =
            Intent(
                context,
                LiveUpdatesForegroundService::class.java
            )
        context.stopService(serviceIntent)
    }

    var prevDistance: String? = null

    fun updateNotification() {
        val intent = Intent(ServiceAction.updateDistance)

        val timestamp = System.currentTimeMillis().toString()

        intent.putExtra(ServiceActionExtra.setTitle, timestamp)
        intent.putExtra(ServiceActionExtra.setText, timestamp)
        context.sendBroadcast(intent)

    }
}
