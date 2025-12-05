package expo.modules.liveupdates

import android.os.Bundle
import androidx.core.os.bundleOf

enum class NotificationAction {
  DISMISSED,
  UPDATED,
  STARTED,
  STOPPED,
  CLICKED,
}

object NotificationStateEventEmitter {
  var sendEvent: ((String, Bundle) -> Unit)? = null

  fun emit(notificationId: Int, action: NotificationAction) {
    val event =
      NotificationStateChangeEventData(
        notificationId = notificationId,
        action = action.name.lowercase(),
        timestamp = System.currentTimeMillis(),
      )
    sendEvent?.invoke(LiveUpdatesModuleEvents.ON_NOTIFICATION_STATE_CHANGE, event.toBundle())
  }
}

data class NotificationStateChangeEventData(
  val notificationId: Int,
  val action: String,
  val timestamp: Long,
) {
  fun toBundle(): Bundle =
    bundleOf("notificationId" to notificationId, "action" to action, "timestamp" to timestamp)
}
