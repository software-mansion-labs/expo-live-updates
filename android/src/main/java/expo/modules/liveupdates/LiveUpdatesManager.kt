package expo.modules.liveupdates

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.toColorInt
import java.io.File

private const val TAG = "LiveUpdatesManager"

class LiveUpdatesManager(private val context: Context, private val channelId: String) {
  val notificationManager = NotificationManagerCompat.from(context)

  @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
  fun startLiveUpdateNotification(state: LiveUpdateState, config: LiveUpdateConfig? = null): Int? {
    // TODO: notificationId should be unique value for each live update
    val notificationId = NOTIFICATION_ID

    if (notificationExists(notificationId)) {
      Log.w(
        TAG,
        "failed to start notification - notification with id $notificationId already exist",
      )
      return null
    }

    val notification = createNotification(channelId, state, notificationId, config)
    notificationManager.notify(notificationId, notification)
    NotificationStateEventEmitter.emitNotificationStateChange(
      notificationId,
      NotificationAction.STARTED,
    )
    return notificationId
  }

  @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
  fun updateLiveUpdateNotification(notificationId: Int, state: LiveUpdateState) {
    if (!notificationExists(notificationId)) {
      Log.w(
        TAG,
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
    notificationManager.cancel(notificationId)
    NotificationStateEventEmitter.emitNotificationStateChange(
      notificationId,
      NotificationAction.STOPPED,
    )
  }

  private fun notificationExists(notificationId: Int): Boolean {
    return notificationManager.activeNotifications.any { it.id == notificationId }
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
          Log.e(TAG, "Invalid color format for backgroundColor: $backgroundColor", e)
        }
      }
    }

    setNotificationDeleteIntent(notificationId, notificationBuilder)
    setNotificationClickIntent(notificationId, config, notificationBuilder)

    return notificationBuilder.build()
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

  private fun setNotificationClickIntent(
    notificationId: Int,
    config: LiveUpdateConfig?,
    notificationBuilder: NotificationCompat.Builder,
  ) {
    val clickIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)

    clickIntent?.apply {
      action = Intent.ACTION_VIEW
      setPackage(context.packageName)
      config?.deepLinkUrl?.let { deepLink ->
        val scheme = getScheme(context)
        data = Uri.parse("$scheme://${deepLink.removePrefix("/")}")
      }
      putExtra("notificationAction", NotificationAction.CLICKED)
      putExtra("notificationId", notificationId)
    }

    val clickPendingIntent =
      PendingIntent.getActivity(
        context,
        notificationId,
        clickIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
      )
    notificationBuilder.setContentIntent(clickPendingIntent)
  }

  fun getScheme(context: Context): String {
    val ai =
      context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
    return ai.metaData?.getString("expo.modules.scheme")
      ?: throw IllegalStateException("expo.modules.scheme not found in AndroidManifest.xml")
  }
}
