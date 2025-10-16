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
  const val SHORT_CRITICAL_TEXT = "shortCriticalText"
}

class FirebaseService : FirebaseMessagingService() {

  private lateinit var liveUpdatesManager: LiveUpdatesManager
  val tokenChangeHandler: TokenChangeHandler = TokenChangeHandler()

  @RequiresApi(Build.VERSION_CODES.O)
  override fun onCreate() {
    liveUpdatesManager = LiveUpdatesManager(this)
  }

  override fun onNewToken(token: String) = tokenChangeHandler.onNewToken(token)

  @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
  override fun onMessageReceived(message: RemoteMessage) {
    Log.i(FIREBASE_TAG, "Message received with event: ${message.data[FirebaseMessageProps.EVENT]}")

    try {
      val event = getFirebaseMessageEvent(message)
      val notificationId = message.data[FirebaseMessageProps.NOTIFICATION_ID]?.toIntOrNull()

      when (event) {
        FirebaseMessageEvent.START -> {
          require(notificationId == null) {
            "Passing ${FirebaseMessageProps.NOTIFICATION_ID} to start Live Update is prohibited - it will be generated automatically."
          }

          val state = getLiveUpdateState(message)
          liveUpdatesManager.startLiveUpdateNotification(state)
        }
        FirebaseMessageEvent.UPDATE,
        FirebaseMessageEvent.STOP -> {
          requireNotNull(notificationId) {
            getMissingOrInvalidErrorMessage(FirebaseMessageProps.NOTIFICATION_ID)
          }

          if (event == FirebaseMessageEvent.UPDATE) {
            val state = getLiveUpdateState(message)
            liveUpdatesManager.updateLiveUpdateNotification(notificationId, state, null) // TODO
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
    val event =
      FirebaseMessageEvent.entries.find {
        it.name == message.data[FirebaseMessageProps.EVENT]?.uppercase()
      }

    return requireNotNull(event) { getMissingOrInvalidErrorMessage(FirebaseMessageProps.EVENT) }
  }

  private fun getLiveUpdateState(message: RemoteMessage): LiveUpdateState {
    val title = message.data[FirebaseMessageProps.TITLE]

    return LiveUpdateState(
      title = requireNotNull(title) { getMissingOrInvalidErrorMessage(FirebaseMessageProps.TITLE) },
      subtitle = message.data[FirebaseMessageProps.SUBTITLE],
      shortCriticalText = message.data[FirebaseMessageProps.SHORT_CRITICAL_TEXT]
    )
  }

  private fun getMissingOrInvalidErrorMessage(propName: String): String {
    return "Property $propName is missing or invalid."
  }
}
