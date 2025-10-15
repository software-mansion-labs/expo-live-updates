package expo.modules.liveupdates

import android.app.NotificationChannel
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import expo.modules.kotlin.records.Field
import expo.modules.kotlin.records.Record
import expo.modules.liveupdates.TokenChangeHandler.Companion.setHandlerSendEvent

data class LiveUpdateState(
  @Field val title: String,
  @Field val subtitle: String? = null,
  @Field val imageName: String? = null,
  @Field val smallImageName: String? = null,
) : Record

data class LiveUpdateConfig(
  @Field val backgroundColor: String? = null,
  @Field val deepLinkUrl: String? = null,
) : Record

const val NOTIFICATION_ID = 1

// TODO: delete CHANNEL_ID and CHANNEL_NAME - make notification channel id and name configurable
const val CHANNEL_ID = "Notifications channel"
const val CHANNEL_NAME = "Channel to handle notifications for Live Updates"

class ExpoLiveUpdatesModule : Module() {
  private lateinit var liveUpdatesManager: LiveUpdatesManager

  // Each module class must implement the definition function. The definition consists of components
  // that describes the module's functionality and behavior.
  // See https://docs.expo.dev/modules/module-api for more details about available components.
  override fun definition() = ModuleDefinition {
    // Sets the name of the module that JavaScript code will use to refer to the module. Takes a
    // string as an argument.
    // Can be inferred from module's class name, but it's recommended to set it explicitly for
    // clarity.
    // The module will be accessible from `requireNativeModule('ExpoLiveUpdatesModule')` in
    // JavaScript.
    Name("ExpoLiveUpdatesModule")

    Events(LiveUpdatesEvents.onNotificationStateChange, LiveUpdatesEvents.onTokenChange)

    AsyncFunction("init") { channelId: String, channelName: String ->
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val serviceChannel =
          NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            android.app.NotificationManager.IMPORTANCE_DEFAULT,
          )

        val androidNotificationManager =
          getSystemService(context, android.app.NotificationManager::class.java)
        androidNotificationManager?.createNotificationChannel(serviceChannel)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
          val canPostLiveUpdates = androidNotificationManager?.canPostPromotedNotifications()

          if (canPostLiveUpdates == true) {
            Log.i("ExpoLiveUpdatesModule", "✅ can post live updates")
          } else {
            Log.i("ExpoLiveUpdatesModule", "❌ cannot post live updates")
          }
        }
      }

      liveUpdatesManager = LiveUpdatesManager(context, CHANNEL_ID)
      NotificationStateEventEmitter.setInstance(NotificationStateEventEmitter(::sendEvent))

      setHandlerSendEvent(this@ExpoLiveUpdatesModule::sendEvent)
    }

    Function("startLiveUpdate") { state: LiveUpdateState, config: LiveUpdateConfig ->
      liveUpdatesManager.startLiveUpdateNotification(state, config)
    }
    Function("stopLiveUpdate") { notificationId: Int ->
      liveUpdatesManager.stopNotification(notificationId)
    }
    Function("updateLiveUpdate") { notificationId: Int, state: LiveUpdateState ->
      liveUpdatesManager.updateLiveUpdateNotification(notificationId, state)
    }

    OnNewIntent { intent -> emitNotificationClickedEventIf(intent) }
  }

  private fun emitNotificationClickedEventIf(intent: Intent) {
    val action: NotificationAction? =
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        intent.getSerializableExtra("notificationAction", NotificationAction::class.java)
      } else {
        @Suppress("DEPRECATION")
        intent.getSerializableExtra("notificationAction") as? NotificationAction
      }
    val notificationId = intent.getIntExtra("notificationId", -1)

    notificationId
      .takeIf { it != -1 && action == NotificationAction.CLICKED }
      ?.let { id ->
        NotificationStateEventEmitter.emitNotificationStateChange(id, NotificationAction.CLICKED)
      }
  }

  private val context
    get() = requireNotNull(appContext.reactContext)
}
