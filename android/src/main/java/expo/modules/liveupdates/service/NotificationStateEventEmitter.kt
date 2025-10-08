package expo.modules.liveupdates

import android.os.Bundle
import androidx.core.os.bundleOf

class NotificationStateEventEmitter(private val sendEvent: (String, Bundle) -> Unit) {
  fun emit(notificationId: Int, action: NotificationAction) {
    val event =
      NotificationStateChangeEventData(
        notificationId = notificationId,
        action = action.name.lowercase(),
        timestamp = System.currentTimeMillis(),
      )
    sendEvent("onNotificationStateChange", event.toBundle())
  }

  companion object {
    private var instance: NotificationStateEventEmitter? = null

    fun setInstance(event: NotificationStateEventEmitter) {
      instance = event
    }

    fun emitNotificationStateChange(notificationId: Int, action: NotificationAction) {
      instance?.emit(notificationId, action)
    }
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
