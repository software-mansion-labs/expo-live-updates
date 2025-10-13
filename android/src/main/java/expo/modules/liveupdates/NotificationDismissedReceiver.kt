package expo.modules.liveupdates

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

private const val TAG = "NotificationDismissedReceiver"

class NotificationDismissedReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    Log.i(TAG, "Notification dismissed by user")
    val notificationId = intent.getIntExtra("notificationId", -1)
    if (notificationId != -1) {
      NotificationStateEventEmitter.emitNotificationStateChange(
        notificationId,
        NotificationAction.DISMISSED,
      )
    }
  }
}
