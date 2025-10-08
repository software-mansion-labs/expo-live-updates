package expo.modules.liveupdates.service

import android.Manifest
import android.app.Notification
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.toColorInt
import expo.modules.liveupdates.LiveUpdateConfig
import expo.modules.liveupdates.LiveUpdateState
import expo.modules.liveupdates.NOTIFICATION_ID
import expo.modules.liveupdates.NotificationAction
import expo.modules.liveupdates.NotificationStateEventEmitter
import java.io.File

private const val TAG = "NotificationManager"

class NotificationManager(private var context: Context, private val channelId: String) {
  // TODO: keep separate last config for each live update
  var lastConfig: LiveUpdateConfig? = null

  @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
  fun startNotification(state: LiveUpdateState, config: LiveUpdateConfig? = null): Int {
    // TODO: notificationId should be unique value for each live update
    val notificationId = NOTIFICATION_ID
    updateNotification(notificationId, state, config)
    return notificationId
  }

  @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
  fun updateNotification(
    notificationId: Int,
    state: LiveUpdateState,
    config: LiveUpdateConfig? = null,
  ) {
    config?.let { lastConfig = it }

    val notification =
      createNotification(
        state.title,
        state.subtitle ?: "",
        config?.backgroundColor ?: "000",
        state.imageName,
        state.smallImageName,
        notificationId,
      )
    val notificationManager = NotificationManagerCompat.from(context)

    if (
      ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED && notification !== null
    ) {
      notificationManager.notify(notificationId, notification)
      NotificationStateEventEmitter.emitNotificationStateChange(
        notificationId,
        NotificationAction.UPDATED,
      )
    }
  }

  private fun createNotification(
    title: String,
    text: String,
    backgroundColor: String? = null,
    image: String? = null,
    smallImageName: String? = null,
    notificationId: Int,
  ): Notification? {

    channelId?.let { channelId ->
      val notificationBuilder =
        NotificationCompat.Builder(context, channelId)
          .setContentTitle(title)
          .setSmallIcon(android.R.drawable.star_on)
          .setContentText(text)

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
        notificationBuilder.setStyle(
          NotificationCompat.ProgressStyle().setProgressIndeterminate(true)
        )
        notificationBuilder.setShortCriticalText("SWM")
        notificationBuilder.setOngoing(true)
        notificationBuilder.setRequestPromotedOngoing(true)
      }

      image?.let { image ->
        val bitmap = loadBitmapByName(image)
        bitmap?.let { bitmap -> notificationBuilder.setLargeIcon(bitmap) }
      }

      smallImageName?.let { smallImageName ->
        val bitmap = loadBitmapByName(smallImageName)
        bitmap?.let { bitmap ->
          val icon = IconCompat.createWithBitmap(bitmap)
          notificationBuilder.setSmallIcon(icon)
        }
      }

      backgroundColor?.let { backgroundColor ->
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.BAKLAVA) {
          notificationBuilder.setColor(backgroundColor.toColorInt())
          notificationBuilder.setColorized(true)
        }
      }

      setNotificationDeleteIntent(context, notificationId, notificationBuilder)

      return notificationBuilder.build()
    }

    return null
  }

  private fun loadBitmapByName(name: String): android.graphics.Bitmap? {
    val fileUrl = name.replace("file://", "")
    val file = File(fileUrl)
    if (file.exists()) {
      val bitmap = BitmapFactory.decodeFile(file.absolutePath)
      return bitmap
    } else {
      Log.e(TAG, "FileCheck could not find file at $fileUrl")
      return null
    }
  }

  fun stopNotification(notificationId: Int) {
    val notificationManager = NotificationManagerCompat.from(context)
    notificationManager.cancel(notificationId)
  }
}
