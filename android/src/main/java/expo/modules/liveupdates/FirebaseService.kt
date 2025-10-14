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

      when (event) {
        FirebaseMessageEvent.START -> {
          require(message.data[FirebaseMessageProps.NOTIFICATION_ID].isNullOrBlank()) {
            "Passing notificationId to start live update is prohibited - it will be generated automatically."
          }

          liveUpdatesManager.startLiveUpdateNotification(state)
        }
        FirebaseMessageEvent.UPDATE,
        FirebaseMessageEvent.STOP -> {
          val notificationId = getNotificationId(message)

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

  private fun getFirebaseMessageEvent(message: RemoteMessage): FirebaseMessageEvent =
    FirebaseMessageEvent.entries.find {
      it.name == message.data[FirebaseMessageProps.EVENT]?.uppercase()
    } ?: error("Invalid or missing event type")

  private fun getLiveUpdateState(message: RemoteMessage): LiveUpdateState =
    LiveUpdateState(
      title =
        requireNotNull(message.data[FirebaseMessageProps.TITLE]) {
          "Missing property: ${FirebaseMessageProps.TITLE}"
        },
      subtitle = message.data[FirebaseMessageProps.SUBTITLE],
    )

  private fun getNotificationId(message: RemoteMessage): Int =
    message.data[FirebaseMessageProps.NOTIFICATION_ID]?.toIntOrNull()
      ?: error("Missing or invalid notificationId")
}
