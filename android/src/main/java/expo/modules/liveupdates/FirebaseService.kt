package expo.modules.liveupdates

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val FIREBASE_TAG = "FirebaseService"

enum class FirebaseMessageEvent {
  START,
  UPDATE,
  STOP,
}

data object FirebaseMessageProps {
  const val NOTIFICATION_ID = "notificationId"
  const val EVENT = "event"
  const val TITLE = "title"
  const val SUBTITLE = "subtitle"
}

class FirebaseService : FirebaseMessagingService() {

  private lateinit var liveUpdatesManager: LiveUpdatesManager
  val tokenChangeHandler: TokenChangeHandler = TokenChangeHandler()

  @RequiresApi(Build.VERSION_CODES.O)
  override fun onCreate() {
    liveUpdatesManager = LiveUpdatesManager(this, CHANNEL_ID)
  }

  override fun onNewToken(token: String) = tokenChangeHandler.onNewToken(token)

  @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
  override fun onMessageReceived(message: RemoteMessage) {
    Log.i(FIREBASE_TAG, "Message received: ${message.data[FirebaseMessageProps.EVENT]}")

    try {
      val event = getFirebaseMessageEvent(message)
      val state = getLiveUpdateState(message)
      val mapNotificationId = { message.data[FirebaseMessageProps.NOTIFICATION_ID]?.toIntOrNull() }

      when (event) {
        FirebaseMessageEvent.START -> {
          val notificationId = mapNotificationId()
          notificationId?.let {
            throw Exception(
              "Passing notificationId to start live update is prohibited - it will be generated automatically."
            )
          }

          liveUpdatesManager.startLiveUpdateNotification(state)
        }
        FirebaseMessageEvent.UPDATE,
        FirebaseMessageEvent.STOP -> {
          val notificationId =
            mapPropertyNotNull(FirebaseMessageProps.NOTIFICATION_ID, mapNotificationId)

          if (event == FirebaseMessageEvent.UPDATE) {
            liveUpdatesManager.updateLiveUpdateNotification(notificationId, state)
          } else {
            liveUpdatesManager.stopNotification(notificationId)
          }
        }
      }
    } catch (e: Exception) {
      Log.e(FIREBASE_TAG, e.message.toString())
    }
  }

  private fun getFirebaseMessageEvent(message: RemoteMessage): FirebaseMessageEvent {
    return mapPropertyNotNull(FirebaseMessageProps.EVENT) {
      FirebaseMessageEvent.entries.find {
        it.name == message.data[FirebaseMessageProps.EVENT]?.uppercase()
      }
    }
  }

  private fun getLiveUpdateState(message: RemoteMessage): LiveUpdateState {
    return LiveUpdateState(
      title =
        mapPropertyNotNull(FirebaseMessageProps.TITLE) { message.data[FirebaseMessageProps.TITLE] },
      subtitle = message.data[FirebaseMessageProps.SUBTITLE],
    )
  }

  private fun <T> mapPropertyNotNull(propertyName: String, mapProperty: () -> T?): T {
    return mapProperty() ?: throw Exception("Property $propertyName is missing or invalid.")
  }
}
