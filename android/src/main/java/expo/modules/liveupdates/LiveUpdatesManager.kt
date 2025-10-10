package expo.modules.liveupdates.service

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.toColorInt
import expo.modules.liveupdates.FIREBASE_TAG
import expo.modules.liveupdates.LiveUpdateConfig
import expo.modules.liveupdates.LiveUpdateState
import expo.modules.liveupdates.NOTIFICATION_ID
import expo.modules.liveupdates.NotificationAction
import expo.modules.liveupdates.NotificationStateEventEmitter
import java.io.File

class LiveUpdatesManager(private val context: Context, private val channelId: String) {
  val notificationManager = NotificationManagerCompat.from(context)

  @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
  fun startLiveUpdateNotification(state: LiveUpdateState, config: LiveUpdateConfig? = null): Int? {
    // TODO: notificationId should be unique value for each live update
    val notificationId = NOTIFICATION_ID

    val notificationExist = notificationManager.activeNotifications.any { it.id == notificationId }
    if (notificationExist) {
      Log.w(
        FIREBASE_TAG,
        "failed to start notification - notification with id $notificationId already exist",
      )
      return null
    }

    val notification = createNotification(channelId, state, notificationId)

    notificationManager.notify(notificationId, notification)
    NotificationStateEventEmitter.emitNotificationStateChange(
      notificationId,
      NotificationAction.STARTED,
    )
    return notificationId
  }

  @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
  fun updateLiveUpdateNotification(notificationId: Int, state: LiveUpdateState) {

    val notificationExist = notificationManager.activeNotifications.any { it.id == notificationId }
    if (!notificationExist) {
      Log.w(
        FIREBASE_TAG,
        "failed to update notification - notification with id $notificationId does not exist",
      )
      return
    }

    val notification = createNotification(channelId, state, notificationId)
    notificationManager.notify(notificationId, notification)
    NotificationStateEventEmitter.emitNotificationStateChange(
      notificationId,
      NotificationAction.UPDATED,
    )
  }

  fun stopNotification(notificationId: Int) {
    val notificationManager = NotificationManagerCompat.from(context)
    notificationManager.cancel(notificationId)
    NotificationStateEventEmitter.emitNotificationStateChange(
      notificationId,
      NotificationAction.STOPPED,
    )
  }

  private fun createNotification(
    channelId: String,
    state: LiveUpdateState,
    notificationId: Int,
    config: LiveUpdateConfig? = null,
  ): Notification {
    val notificationBuilder =
      NotificationCompat.Builder(context, channelId)
        .setContentTitle(state.title)
        .setSmallIcon(android.R.drawable.star_on)
        .setContentText(state.subtitle)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
      notificationBuilder.setShortCriticalText("SWM")
      notificationBuilder.setOngoing(true)
      notificationBuilder.setRequestPromotedOngoing(true)
    }

    state.imageName?.let { imageName ->
      val bitmap = loadBitmapByName(imageName)
      bitmap?.let { bitmap -> notificationBuilder.setLargeIcon(bitmap) }
    }

    state.smallImageName?.let { smallImageName ->
      val bitmap = loadBitmapByName(smallImageName)
      bitmap?.let { bitmap ->
        val icon = IconCompat.createWithBitmap(bitmap)
        notificationBuilder.setSmallIcon(icon)
      }
    }

    // TODO: save config by id to apply it when updating notification
    config?.backgroundColor?.let { backgroundColor ->
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.BAKLAVA) {
        try {
          notificationBuilder.setColor(backgroundColor.toColorInt())
          notificationBuilder.setColorized(true)
        } catch (e: IllegalArgumentException) {
          Log.e("LiveUpdatesManager", "Invalid color format for backgroundColor: $backgroundColor", e)
        }
      }
    }

    setNotificationDeleteIntent(notificationId, notificationBuilder)

    return notificationBuilder.build()
  }

  private fun loadBitmapByName(name: String): android.graphics.Bitmap? {
    val fileUrl = name.replace("file://", "")
    val file = File(fileUrl)
    if (file.exists()) {
      val bitmap = BitmapFactory.decodeFile(file.absolutePath)
      return bitmap
    } else {
      Log.e("NotificationUtils", "FileCheck could not find file at $fileUrl")
      return null
    }
  }

  private fun setNotificationDeleteIntent(
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
}
