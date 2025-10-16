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

class NotificationStateEventEmitter(private val sendEvent: (String, Bundle) -> Unit) {
  companion object {
    private var instance: NotificationStateEventEmitter? = null

    fun setInstance(emitter: NotificationStateEventEmitter) {
      instance = emitter
    }

    fun emitNotificationStateChange(notificationId: Int, action: NotificationAction) {
      instance?.emit(notificationId, action)
    }
  }

  fun emit(notificationId: Int, action: NotificationAction) {
    val event =
      NotificationStateChangeEventData(
        notificationId = notificationId,
        action = action.name.lowercase(),
        timestamp = System.currentTimeMillis(),
      )
    sendEvent(LiveUpdatesModuleEvents.ON_NOTIFICATION_STATE_CHANGE, event.toBundle())
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
