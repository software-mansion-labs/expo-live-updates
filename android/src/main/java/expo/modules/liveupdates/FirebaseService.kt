package expo.modules.liveupdates

import android.Manifest
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.text.toBooleanStrictOrNull
import kotlin.text.toIntOrNull
import kotlinx.serialization.json.Json

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
  const val TEXT = "text"
  const val SUB_TEXT = "subText"
  const val IMAGE_URL = "imageUrl"
  const val ICON_URL = "iconUrl"
  const val PROGRESS_MAX = "progressMax"
  const val PROGRESS_VALUE = "progressValue"
  const val PROGRESS_INDETERMINATE = "progressIndeterminate"
  const val PROGRESS_POINTS = "progressPoints"
  const val PROGRESS_SEGMENTS = "progressSegments"
  const val SHORT_CRITICAL_TEXT = "shortCriticalText"
  const val ICON_BACKGROUND_COLOR = "iconBackgroundColor"
  const val DEEP_LINK_URL = "deepLinkUrl"
  const val SHOW_TIME = "showTime"
  const val TIME = "time"
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
    if (!checkPostNotificationPermission(this)) {
      Log.e(FIREBASE_TAG, "POST_NOTIFICATIONS permission is not granted. Cannot display message.")
      return
    }

    Log.i(FIREBASE_TAG, "Message received with event: ${message.data[FirebaseMessageProps.EVENT]}")

    try {
      val event = getFirebaseMessageEvent(message)
      val notificationId = message.data[FirebaseMessageProps.NOTIFICATION_ID]?.toIntOrNull()

      when (event) {
        FirebaseMessageEvent.START -> {
          require(notificationId == null) {
            "Passing ${FirebaseMessageProps.NOTIFICATION_ID} to start Live Update is prohibited - it will be generated automatically."
          }

          val (state, config) = getLiveUpdatesNotificationData(message)
          liveUpdatesManager.startLiveUpdateNotification(state, config)
        }
        FirebaseMessageEvent.UPDATE,
        FirebaseMessageEvent.STOP -> {
          requireNotNull(notificationId) {
            getMissingOrInvalidErrorMessage(FirebaseMessageProps.NOTIFICATION_ID)
          }

          if (event == FirebaseMessageEvent.UPDATE) {
            val (state, config) = getLiveUpdatesNotificationData(message)
            liveUpdatesManager.updateLiveUpdateNotification(notificationId, state, config)
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

  private fun getLiveUpdatesNotificationData(
    message: RemoteMessage
  ): Pair<LiveUpdateState, LiveUpdateConfig> {
    return getLiveUpdateState(message) to getLiveUpdateConfig(message)
  }

  private fun getLiveUpdateState(message: RemoteMessage): LiveUpdateState {
    val title = message.data[FirebaseMessageProps.TITLE]
    val progress = getProgress(message)

    val image = message.data[FirebaseMessageProps.IMAGE_URL]?.let { LiveUpdateImage(it, true) }
    val icon = message.data[FirebaseMessageProps.ICON_URL]?.let { LiveUpdateImage(it, true) }

    return LiveUpdateState(
      title = requireNotNull(title) { getMissingOrInvalidErrorMessage(FirebaseMessageProps.TITLE) },
      text = message.data[FirebaseMessageProps.TEXT],
      subText = message.data[FirebaseMessageProps.SUB_TEXT],
      image = image,
      icon = icon,
      progress = progress,
      shortCriticalText = message.data[FirebaseMessageProps.SHORT_CRITICAL_TEXT],
      showTime = message.data[FirebaseMessageProps.SHOW_TIME]?.toBooleanStrictOrNull(),
      time = message.data[FirebaseMessageProps.TIME]?.toLongOrNull(),
    )
  }

  private fun getProgress(message: RemoteMessage): LiveUpdateProgress? {
    val progressMax = message.data[FirebaseMessageProps.PROGRESS_MAX]?.toIntOrNull()
    val progressValue = message.data[FirebaseMessageProps.PROGRESS_VALUE]?.toIntOrNull()
    val progressIndeterminate =
      message.data[FirebaseMessageProps.PROGRESS_INDETERMINATE]?.toBooleanStrictOrNull()

    val pointsJson = message.data[FirebaseMessageProps.PROGRESS_POINTS]
    val points = pointsJson?.let { Json.decodeFromString<ArrayList<LiveUpdateProgressPoint>>(it) }

    val segmentsJson = message.data[FirebaseMessageProps.PROGRESS_SEGMENTS]
    val segments =
      segmentsJson?.let { Json.decodeFromString<ArrayList<LiveUpdateProgressSegment>>(it) }

    return if (progressValue != null || progressIndeterminate == true) {
      LiveUpdateProgress(
        max = progressMax,
        progress = progressValue,
        indeterminate = progressIndeterminate,
        points = points,
        segments = segments,
      )
    } else null
  }

  private fun getLiveUpdateConfig(message: RemoteMessage): LiveUpdateConfig {
    return LiveUpdateConfig(
      deepLinkUrl = message.data[FirebaseMessageProps.DEEP_LINK_URL],
      iconBackgroundColor = message.data[FirebaseMessageProps.ICON_BACKGROUND_COLOR],
    )
  }

  private fun getMissingOrInvalidErrorMessage(propName: String): String {
    return "Property $propName is missing or invalid."
  }

  companion object {
    fun isFirebaseAvailable(context: Context): Boolean {
      return try {
        FirebaseApp.getApps(context).isNotEmpty()
      } catch (e: Exception) {
        Log.w(FIREBASE_TAG, "Error checking Firebase availability: ${e.message}")
        false
      }
    }
  }
}
