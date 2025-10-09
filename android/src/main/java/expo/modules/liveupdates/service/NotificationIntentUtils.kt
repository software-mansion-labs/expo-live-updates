package expo.modules.liveupdates.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

/**
 * Creates and sets a delete intent for a notification builder to detect user swipe dismissals.
 *
 * @param context The context to create the intent with
 * @param notificationId The ID of the notification
 * @param notificationBuilder The notification builder to set the delete intent on
 */
fun setNotificationDeleteIntent(
  context: Context,
  notificationId: Int,
  notificationBuilder: NotificationCompat.Builder,
) {
  val deleteIntent = Intent(context, NotificationDismissedReceiver::class.java)
  deleteIntent.putExtra("notificationId", notificationId)
  val deletePendingIntent =
    PendingIntent.getBroadcast(
      context,
      notificationId,
      deleteIntent,
      PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
  notificationBuilder.setDeleteIntent(deletePendingIntent)
}
