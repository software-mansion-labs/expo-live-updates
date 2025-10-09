package expo.modules.liveupdates.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import expo.modules.liveupdates.NotificationAction
import expo.modules.liveupdates.NotificationStateEventEmitter

class NotificationDismissedReceiver : BroadcastReceiver() {
  private val TAG = "NotificationDismissedReceiver"

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
