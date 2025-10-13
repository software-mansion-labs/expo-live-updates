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
    val event = FirebaseMessageEvent.entries.find { it.name == message.data["event"]?.uppercase() }
    Log.i(FIREBASE_TAG, "Message received: $event")

    event
      ?.let {
        val state = getLiveUpdateState(message)

        when (event) {
          FirebaseMessageEvent.START -> liveUpdatesManager.startLiveUpdateNotification(state)
          FirebaseMessageEvent.UPDATE,
          FirebaseMessageEvent.STOP -> {
            val notificationId = message.data["notificationId"]?.toIntOrNull()

            notificationId
              ?.let { notificationId ->
                if (event == FirebaseMessageEvent.UPDATE) {
                  liveUpdatesManager.updateLiveUpdateNotification(notificationId, state)
                } else {
                  liveUpdatesManager.stopNotification(notificationId)
                }
              }
              .run {
                Log.i(
                  FIREBASE_TAG,
                  "Cannot $event notification - notificationId is invalid or missing.",
                )
              }
          }
        }
      }
      .run { Log.i(FIREBASE_TAG, "Received message with invalid or missing event.") }
  }

  fun getLiveUpdateState(message: RemoteMessage): LiveUpdateState {
    return LiveUpdateState(
      title = message.data["title"] ?: "Live Update",
      subtitle = message.data["subtitle"],
    )
  }
}
