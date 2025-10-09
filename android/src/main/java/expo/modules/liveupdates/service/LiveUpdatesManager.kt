package expo.modules.liveupdates.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import expo.modules.liveupdates.FIREBASE_TAG
import expo.modules.liveupdates.LiveUpdateConfig
import expo.modules.liveupdates.LiveUpdateState
import expo.modules.liveupdates.NOTIFICATION_ID
import expo.modules.liveupdates.NotificationAction
import expo.modules.liveupdates.NotificationStateEventEmitter

class LiveUpdatesManager(private val context: Context, private val channelId: String) {
  val notificationManager = NotificationManagerCompat.from(context)

  // TODO: keep separate last config for each live update
  var lastConfig: LiveUpdateConfig? = null

  @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
  fun startLiveUpdateNotification(state: LiveUpdateState, config: LiveUpdateConfig? = null): Int {
    // TODO: notificationId should be unique value for each live update
    val notificationId = NOTIFICATION_ID

    val notification = NotificationUtils.createNotification(context, channelId, state)
    // TODO: handle passing config
    notificationManager.notify(notificationId, notification)
    NotificationStateEventEmitter.emitNotificationStateChange(notificationId, NotificationAction.STARTED)
    return notificationId
  }

  @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
  fun updateLiveUpdateNotification(
    notificationId: Int,
    state: LiveUpdateState,
  ) {

    val notificationExist = notificationManager.activeNotifications.any { it.id == notificationId }
    if (!notificationExist) {
      Log.w(FIREBASE_TAG, "failed to display notification - no permission or invalid data")
      return
    }

    val notification = NotificationUtils.createNotification(context, channelId, state)
    notificationManager.notify(notificationId, notification)
    NotificationStateEventEmitter.emitNotificationStateChange(notificationId, NotificationAction.UPDATED)
  }

  fun stopNotification(notificationId: Int) {
    val notificationManager = NotificationManagerCompat.from(context)
    notificationManager.cancel(notificationId)
    NotificationStateEventEmitter.emitNotificationStateChange(notificationId, NotificationAction.DISMISSED)
  }

  private fun hasNotificationPermission(): Boolean {
    return ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED
  }
}
