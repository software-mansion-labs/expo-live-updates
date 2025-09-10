package expo.modules.liveupdates.service

import android.content.Context
import android.content.Intent
import android.os.Build
import expo.modules.liveupdates.LiveUpdateConfig
import expo.modules.liveupdates.LiveUpdateState // Assuming LiveUpdateState is in this package, if not, this will need adjustment
import expo.modules.liveupdates.LiveUpdatesForegroundService // Assuming LiveUpdatesForegroundService is in this package
import expo.modules.liveupdates.service.ServiceAction

class NotificationManager(
    private var context: Context
) {
    fun startForegroundService(state: LiveUpdateState, config: LiveUpdateConfig) {
        val serviceIntent = Intent(context, LiveUpdatesForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            updateNotification(state, config)
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

    var lastConfig: LiveUpdateConfig? = null

    fun updateNotification(state: LiveUpdateState, config: LiveUpdateConfig? = null) {
        if(config !== null) {
            lastConfig = config
        }
        val intent = Intent(ServiceAction.updateLiveUpdate)

        intent.putExtra(ServiceActionExtra.title, state.title)
        intent.putExtra(ServiceActionExtra.text, state.subtitle ?: "")
    println("==================dsadsadsaa")
        println(state.title)

        intent.putExtra(ServiceActionExtra.date, state.date)
        intent.putExtra(ServiceActionExtra.imageName, state.imageName)
        intent.putExtra( ServiceActionExtra.dynamicIslandImageName, state.dynamicIslandImageName)
        intent.putExtra( ServiceActionExtra.backgroundColor, lastConfig?.backgroundColor?: "000")

        context.sendBroadcast(intent)
    }
}
