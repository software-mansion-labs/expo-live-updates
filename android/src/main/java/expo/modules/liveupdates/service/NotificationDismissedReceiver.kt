package expo.modules.liveupdates.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import expo.modules.liveupdates.ExpoLiveUpdatesModule
import expo.modules.liveupdates.NotificationAction

class NotificationDismissedReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    Log.i("NotificationDismissedReceiver", "Notification dismissed by user")
    val notificationId = intent.getIntExtra("notificationId", -1)
    if (notificationId != -1) {
      ExpoLiveUpdatesModule.emitNotificationStateChange(
        notificationId,
        NotificationAction.DISMISSED,
      )
    }
  }
}
