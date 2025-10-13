package expo.modules.liveupdates

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val FIREBASE_TAG = "FirebaseService"

enum class FirebaseNotificationEvent {
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
    val notificationId = message.data["notificationId"]?.toIntOrNull()
    val event = FirebaseNotificationEvent.entries.find { it.name == message.data["event"]?.uppercase() }

    Log.i(FIREBASE_TAG, "[${notificationId}] message received: $event")

    event?.let { event ->
      val notificationData =
        LiveUpdateState(
          title = "[$notificationId] ${message.data["title"] ?: "Live Update"}",
          subtitle = message.data["subtitle"],
        )

      when (event) {
        FirebaseNotificationEvent.START -> liveUpdatesManager.startLiveUpdateNotification(notificationData)
        FirebaseNotificationEvent.UPDATE -> notificationId?.let{liveUpdatesManager.updateLiveUpdateNotification(it, notificationData)}.run {Log.i(FIREBASE_TAG, "cannot update - notificationId is null")}
        FirebaseNotificationEvent.STOP -> notificationId?.let{liveUpdatesManager.stopNotification(it)}.run{Log.i(FIREBASE_TAG, "cannot stop - notificationId is null")}
      }
    }
  }
}
