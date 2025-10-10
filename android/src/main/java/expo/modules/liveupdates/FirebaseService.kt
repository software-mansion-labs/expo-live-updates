package expo.modules.liveupdates

import android.Manifest
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import expo.modules.liveupdates.service.LiveUpdatesManager
import expo.modules.liveupdates.service.TokenChangeHandler

const val FIREBASE_TAG = "FIREBASE SERVICE"

class FirebaseService : FirebaseMessagingService() {

  private var liveUpdatesManager: LiveUpdatesManager? = null
  val tokenChangeHandler: TokenChangeHandler = TokenChangeHandler()

  @RequiresApi(Build.VERSION_CODES.O)
  override fun onCreate() {
    liveUpdatesManager = LiveUpdatesManager(this, CHANNEL_ID)
  }

  override fun onNewToken(token: String) = tokenChangeHandler.onNewToken(token)

  @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
  override fun onMessageReceived(message: RemoteMessage) {
    Log.i(FIREBASE_TAG, "message received")

    val state =
      LiveUpdateState(
        title = message.data["title"] ?: "Live Update",
        subtitle = message.data["subtitle"],
      )

    liveUpdatesManager?.let { manager ->
      val notificationId = message.data["notificationId"]?.toInt() ?: NOTIFICATION_ID
      manager.updateLiveUpdateNotification(notificationId, state)
    }
  }
}
