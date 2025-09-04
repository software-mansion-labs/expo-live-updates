package expo.modules.liveupdates

import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import java.net.URL
import android.app.NotificationChannel
import androidx.core.content.ContextCompat.getSystemService
import java.util.Timer
import java.util.TimerTask
import android.os.Build

class ExpoLiveUpdatesModule : Module() {
    private var notificationManager: NotificationManager? = null

    // Each module class must implement the definition function. The definition consists of components
    // that describes the module's functionality and behavior.
    // See https://docs.expo.dev/modules/module-api for more details about available components.
    override fun definition() = ModuleDefinition {
        // Sets the name of the module that JavaScript code will use to refer to the module. Takes a string as an argument.
        // Can be inferred from module's class name, but it's recommended to set it explicitly for clarity.
        // The module will be accessible from `requireNativeModule('ExpoLiveUpdatesModule')` in JavaScript.
        Name("ExpoLiveUpdatesModule")
        OnCreate {
            // prepare notification channel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val serviceChannel =
                    NotificationChannel(
                        CHANNEL_ID,
                        "Android Live Updates",
                        android.app.NotificationManager.IMPORTANCE_DEFAULT
                    )
                serviceChannel.importance = android.app.NotificationManager.IMPORTANCE_LOW

                val notificationManager =
                    getSystemService(context, android.app.NotificationManager::class.java)
                notificationManager?.createNotificationChannel(serviceChannel)
            }
        }
        AsyncFunction("init") {
            val notifManager = NotificationManager(context)
            notificationManager = notifManager
        }

        Function("startService") {
            notificationManager?.startForegroundService()
        }
        Function("stopService") {
            notificationManager?.stopForegroundService()
        }
        Function("updateNotification") {
            notificationManager?.updateNotification()
        }
        Function("updateNotificationInterval") {
            val timer = Timer()
            val task = object : TimerTask() {
                override fun run() {
                    notificationManager?.updateNotification()
                }
            }

            timer.scheduleAtFixedRate(task, 0L, 5000L)
        }


    }


    private val context
        get() = requireNotNull(appContext.reactContext)
}


