package expo.modules.liveupdates

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val FIREBASE_TAG = "FirebaseService"

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
    Log.i(FIREBASE_TAG, "message received")

    val state =
      LiveUpdateState(
        title = message.data[LiveUpdateState.Props.TITLE] ?: "Live Update",
        subtitle = message.data[LiveUpdateState.Props.SUBTITLE],
      )
    val config =
      LiveUpdateConfig(
        message.data[LiveUpdateConfig.Props.BACKGROUND_COLOR],
        message.data[LiveUpdateConfig.Props.SHORT_CRITICAL_TEXT],
      )

    liveUpdatesManager.startLiveUpdateNotification(state, config)
  }
}
