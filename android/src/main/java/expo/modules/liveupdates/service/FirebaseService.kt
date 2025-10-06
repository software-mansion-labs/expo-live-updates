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
import expo.modules.liveupdates.service.NotificationEvent
import java.lang.Exception

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
    try {
      val notificationData = NotificationData(message.data)
      val notification = createNotification(notificationData)

      val (event, notificationId) = notificationData

      Log.i(FIREBASE_TAG, "[${notificationId}] message received: $event")

      when (notificationData.event) {
        NotificationEvent.START -> startNotification(notificationId, notification)
        NotificationEvent.UPDATE -> updateNotification(notificationId, notification)
        NotificationEvent.STOP -> stopNotification(notificationId)
      }
    } catch (e: Exception) {
      Log.e(FIREBASE_TAG, e.message.toString())
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

  private fun isNotificationIdFree(notificationId: Int): Boolean {
    val notifications = notificationManager?.activeNotifications

    val isIdFree =
      notifications?.none { notification -> notification?.id == notificationId } ?: true
    return isIdFree
  }

  private fun startNotification(notificationId: Int, notification: Notification) {
    Log.i(FIREBASE_TAG, "start started")
    if (isNotificationIdFree(notificationId)) {
      Log.i(FIREBASE_TAG, "$notificationManager $notificationId start ")
      notificationManager?.notify(notificationId, notification)
    } else {
      Log.i(FIREBASE_TAG, "Notification of given id is already created")
    }
  }

  private fun updateNotification(notificationId: Int, notification: Notification) {
    if (!isNotificationIdFree(notificationId)) {
      Log.i(FIREBASE_TAG, "$notificationManager $notificationId update ")
      notificationManager?.notify(notificationId, notification)
    } else {
      Log.i(FIREBASE_TAG, "Notification of given id doesn't exist")
    }
  }

  private fun stopNotification(notificationId: Int) {
    if (!isNotificationIdFree(notificationId)) {
      notificationManager?.cancel(notificationId)
    } else {
      Log.i(FIREBASE_TAG, "Notification of given id doesn't exist")
    }
  }
}
