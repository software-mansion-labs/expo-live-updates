package expo.modules.liveupdates

import android.app.NotificationChannel
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.google.firebase.messaging.FirebaseMessaging
import expo.modules.kotlin.Promise
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import expo.modules.kotlin.records.Field
import expo.modules.kotlin.records.Record
import expo.modules.liveupdates.service.NotificationManager

data class LiveUpdateState(
  @Field val title: String,
  @Field val subtitle: String? = null,
  @Field val date: Long? = null,
  @Field val imageName: String? = null,
  @Field val smallImageName: String? = null,
) : Record

data class LiveUpdateConfig(@Field val backgroundColor: String? = null) : Record

private const val GET_PUSH_TOKEN_FAILED_CODE = "GET_PUSH_TOKEN_FAILED"
const val NOTIFICATION_ID = 3

// TODO: delete CHANNEL_ID and CHANNEL_NAME - make notification channel id and name configurable
const val CHANNEL_ID = "Notifications channel"
const val CHANNEL_NAME = "Channel to handle notifications for Live Updates"

class ExpoLiveUpdatesModule : Module() {
  private var notificationManager: NotificationManager? = null

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
      val notifManager = NotificationManager(context, CHANNEL_ID)

      notificationManager = notifManager
      notificationManager?.startLiveUpdatesService()
    }

    Function("startLiveUpdate") { state: LiveUpdateState, config: LiveUpdateConfig ->
      notificationManager?.startNotification(state, config)
    }
    Function("stopLiveUpdate") { notificationId: Int ->
      notificationManager?.stopNotification(notificationId)
    }
    Function("updateLiveUpdate") { notificationId: Int, state: LiveUpdateState ->
      notificationManager?.updateNotification(notificationId, state)
    }
    AsyncFunction("getDevicePushTokenAsync") { promise: Promise ->
      FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (!task.isSuccessful) {
          val exception = task.exception
          promise.reject(
            GET_PUSH_TOKEN_FAILED_CODE,
            "Fetching the token failed: ${exception?.message ?: "unknown"}",
            exception,
          )
          return@addOnCompleteListener
        }
        val token =
          task.result
            ?: run {
              promise.reject(
                GET_PUSH_TOKEN_FAILED_CODE,
                "Fetching the token failed. Invalid token.",
                null,
              )
              return@addOnCompleteListener
            }

        promise.resolve(token)
      }
    }
  }

  private val context
    get() = requireNotNull(appContext.reactContext)
}
