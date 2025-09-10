package expo.modules.liveupdates

import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import android.app.NotificationChannel
import androidx.core.content.ContextCompat.getSystemService
import android.os.Build
import expo.modules.kotlin.records.Record
import expo.modules.liveupdates.service.NotificationManager
import expo.modules.kotlin.records.Field

data class LiveUpdateState(
    @Field val title: String,
    @Field val subtitle: String? = null,
    @Field val date: Long? = null,
    @Field val imageName: String? = null,
    @Field val dynamicIslandImageName: String? = null
) : Record

data class LiveUpdateConfig(
    @Field val backgroundColor: String? = null
) : Record

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

        AsyncFunction("init") { channelId: String, channelName: String ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val serviceChannel =
                    NotificationChannel(
                        channelId,
                        channelName,
                        android.app.NotificationManager.IMPORTANCE_DEFAULT
                    )
                serviceChannel.importance = android.app.NotificationManager.IMPORTANCE_LOW

                val androidNotificationManager =
                    getSystemService(context, android.app.NotificationManager::class.java)
                androidNotificationManager?.createNotificationChannel(serviceChannel)
            }

            val notifManager = NotificationManager(context, channelId)
            notificationManager = notifManager
        }

        Function("startForegroundService") { state: LiveUpdateState, config: LiveUpdateConfig ->
            notificationManager?.startForegroundService(state, config)
        }
        Function("stopForegroundService") {
            notificationManager?.stopForegroundService()
        }
        Function("updateForegroundService") { state: LiveUpdateState ->
            notificationManager?.updateNotification(state)
        }
    }


    private val context
        get() = requireNotNull(appContext.reactContext)
}


