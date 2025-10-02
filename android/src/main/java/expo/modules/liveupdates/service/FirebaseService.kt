package expo.modules.liveupdates

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import expo.modules.liveupdates.service.NotificationData
import expo.modules.liveupdates.service.NotificationEvent

const val FIREBASE_TAG = "FIREBASE SERVICE"

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
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

  @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
  override fun onMessageReceived(message: RemoteMessage) {
    val notificationData = NotificationData(message.data)
    val notification = createNotification(notificationData)

    Log.i(FIREBASE_TAG, "[${notificationData.notificationId}] message received: ${notificationData.event}")

    notificationData.notificationId?.let { notificationId ->

      notificationData.event?.let { event ->
        Log.i(FIREBASE_TAG, "EVENTTTTT $event")
        when (event) {
          NotificationEvent.START -> startNotification(notificationId, notification)
          NotificationEvent.UPDATE -> updateNotification(notificationId, notification)
          NotificationEvent.STOP -> stopNotification(notificationId)
        }
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

  private fun startNotification(notificationId: Int, notification: Notification) {
    Log.i(FIREBASE_TAG, "start started")
    if (isNotificationIdFree(notificationId)) {
      Log.i(FIREBASE_TAG, "$notificationManager $notificationId start ")
      notificationManager?.notify(notificationId, notification)
    } else {
      Log.i(FIREBASE_TAG, "Notification of given id is already created")
    }
  }

  private fun isNotificationIdFree(notificationId: Int): Boolean {
    val notifications: Array<out StatusBarNotification?>? = notificationManager?.activeNotifications

    val isIdFree = notifications?.none { notification -> notification?.id == notificationId } ?: true
    return isIdFree
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
