package expo.modules.liveupdates

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import expo.modules.liveupdates.service.NotificationData
import expo.modules.liveupdates.service.NotificationIntentUtils
import kotlin.String

const val FIREBASE_TAG = "FIREBASE SERVICE"

class FirebaseService : FirebaseMessagingService() {

  var notificationManager: NotificationManager? = null

  @RequiresApi(Build.VERSION_CODES.O)
  override fun onCreate() {
    val androidNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    val channel =
      NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)

    androidNotificationManager.createNotificationChannel(channel)
    notificationManager = androidNotificationManager
  }

  // TODO: update token in RN
  override fun onNewToken(token: String) {
    Log.i(FIREBASE_TAG, "new token received: $token")
    super.onNewToken(token)
  }

  @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
  override fun onMessageReceived(message: RemoteMessage) {
    Log.i(FIREBASE_TAG, "message received")

    val notificationData = NotificationData(message.data)
    val notification = createNotification(notificationData)

    notificationData.notificationId?.let { notificationId ->
      notificationManager?.let { notificationManager ->
        Log.i(FIREBASE_TAG, "message displayed")
        notificationManager.notify(notificationId, notification)
        ExpoLiveUpdatesModule.emitNotificationStateChange(
          notificationId,
          NotificationAction.UPDATED,
        )
      }
    }
  }

  private fun createNotification(notificationData: NotificationData): Notification {
    val notificationBuilder =
      NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle(notificationData.title)
        .setSmallIcon(android.R.drawable.star_on)
        .setContentText(notificationData.subtitle)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
      notificationBuilder.setShortCriticalText("SWM")
      notificationBuilder.setOngoing(true)
      notificationBuilder.setRequestPromotedOngoing(true)

      val progressStyle = createProgressStyle(notificationData)
      notificationBuilder.setStyle(progressStyle)
    }

    notificationData.notificationId?.let { notificationId ->
      NotificationIntentUtils.setDeleteIntent(this, notificationId, notificationBuilder)
    }

    return notificationBuilder.build()
  }

  private fun createProgressStyle(
    notificationData: NotificationData
  ): NotificationCompat.ProgressStyle {
    val progressStyle = NotificationCompat.ProgressStyle()

    notificationData.progress?.let { progressStyle.setProgress(it) }

    notificationData.progressPoints?.forEach {
      progressStyle.addProgressPoint(NotificationCompat.ProgressStyle.Point(it))
    }

    return progressStyle
  }
}
