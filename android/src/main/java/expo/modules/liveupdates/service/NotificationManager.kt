package expo.modules.liveupdates.service

import android.content.Context
import android.content.Intent
import android.os.Build
import expo.modules.liveupdates.LiveActivityState // Assuming LiveActivityState is in this package, if not, this will need adjustment
import expo.modules.liveupdates.LiveUpdatesForegroundService // Assuming LiveUpdatesForegroundService is in this package
import expo.modules.liveupdates.service.ServiceAction


class NotificationManager(
    private var context: Context
) {
    fun startForegroundService(state: LiveActivityState) {
        val serviceIntent = Intent(context, LiveUpdatesForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            updateNotification(state)
        }, 500) // 500 ms delay
    }

    fun stopForegroundService() {
        val serviceIntent =
            Intent(
                context,
                LiveUpdatesForegroundService::class.java
            )
        context.stopService(serviceIntent)
    }

    fun updateNotification(state: LiveActivityState) {
        val intent = Intent(ServiceAction.updateLiveUpdate)

        intent.putExtra(ServiceActionExtra.setTitle, state.title)
        intent.putExtra(ServiceActionExtra.setText, state.subtitle ?: "")

        intent.putExtra(ServiceActionExtra.date, state.date)
        intent.putExtra(ServiceActionExtra.imageName, state.imageName)
        intent.putExtra( ServiceActionExtra.dynamicIslandImageName, state.dynamicIslandImageName)

        context.sendBroadcast(intent)
    }
}
