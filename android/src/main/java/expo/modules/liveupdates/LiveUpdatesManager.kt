package expo.modules.liveupdates

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import java.io.File

private const val TAG = "LiveUpdatesManager"
private const val EXPO_MODULE_SCHEME_KEY = "expo.modules.scheme"
private const val DEFAULT_MAX_PROGRESS = 100

object NotificationActionExtra {
  const val NOTIFICATION_ACTION = "notificationAction"
  const val NOTIFICATION_ID = "notificationId"
}

class LiveUpdatesManager(private val context: Context) {
  private val channelId = getChannelId(context)
  private val notificationManager = NotificationManagerCompat.from(context)
  private val idGenerator = IdGenerator(context)

  @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
  fun startLiveUpdateNotification(state: LiveUpdateState, config: LiveUpdateConfig? = null): Int? {
    val notificationId = idGenerator.generateNextId()

    if (notificationExists(notificationId)) {
      Log.w(
        TAG,
        "failed to start notification - notification with id $notificationId already exists",
      )
      return null
    }

    val notification = createNotification(state, notificationId, config)
    notificationManager.notify(notificationId, notification)
    NotificationStateEventEmitter.emitNotificationStateChange(
      notificationId,
      NotificationAction.STARTED,
    )
    return notificationId
  }

  @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
  fun updateLiveUpdateNotification(
    notificationId: Int,
    state: LiveUpdateState,
    config: LiveUpdateConfig?,
  ) {
    if (!notificationExists(notificationId)) {
      Log.w(
        TAG,
        "failed to update notification - notification with id $notificationId does not exists",
      )
      return
    }

    val notification = createNotification(state, notificationId, config)
    notificationManager.notify(notificationId, notification)
    NotificationStateEventEmitter.emitNotificationStateChange(
      notificationId,
      NotificationAction.UPDATED,
    )
  }

  fun stopNotification(notificationId: Int) {
    if (!notificationExists(notificationId)) {
      Log.w(
        TAG,
        "failed to stop notification - notification with id $notificationId does not exists",
      )
      return
    }

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
    state: LiveUpdateState,
    notificationId: Int,
    config: LiveUpdateConfig? = null,
  ): Notification {
    val notificationBuilder =
      NotificationCompat.Builder(context, channelId)
        .setContentTitle(state.title)
        .setSmallIcon(android.R.drawable.star_on)
        .setContentText(state.text)
        .setSubText(state.subText)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
      notificationBuilder.setShortCriticalText(state.shortCriticalText)
      notificationBuilder.setOngoing(true)
      notificationBuilder.setRequestPromotedOngoing(true)
    }

    state.imageLocalUri?.let { imageName ->
      val bitmap = loadBitmapByName(imageName)
      bitmap?.let { bitmap -> notificationBuilder.setLargeIcon(bitmap) }
    }

    state.iconLocalUri?.let { smallImageName ->
      val bitmap = loadBitmapByName(smallImageName)
      bitmap?.let { bitmap ->
        val icon = IconCompat.createWithBitmap(bitmap)
        notificationBuilder.setSmallIcon(icon)
      }
    }

    state.progress?.let { progress ->
      if (progress.indeterminate == true) {
        notificationBuilder.setProgress(0, 0, true)
      } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
          val style = createProgressStyle(progress)
          notificationBuilder.setStyle(style)
        } else {
          progress.progress?.let {
            notificationBuilder.setProgress(progress.max ?: DEFAULT_MAX_PROGRESS, it, false)
          }
        }
      }
    }

    if (state.showTime == false) {
      notificationBuilder.setShowWhen(false)
    } else {
      state.time?.let { time -> notificationBuilder.setWhen(time) }
    }

    // TODO: save config by id to apply it when updating notification
    config?.backgroundColor?.let { backgroundColor ->
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.BAKLAVA) {
        try {
          notificationBuilder.setColor(backgroundColor.toColorInt())
          notificationBuilder.setColorized(true)
        } catch (e: IllegalArgumentException) {
          Log.e(TAG, getInvalidColorFormatErrorMessage(backgroundColor), e)
        }
      }
    }

    setNotificationDeleteIntent(notificationId, notificationBuilder)
    setNotificationClickIntent(notificationId, config, notificationBuilder)

    return notificationBuilder.build()
  }

  private fun createProgressStyle(progress: LiveUpdateProgress): NotificationCompat.ProgressStyle {
    val points =
      progress.points?.map {
        val (position, color) = it
        val point = NotificationCompat.ProgressStyle.Point(position)
        color?.let { color ->
          try {
            point.setColor(color.toColorInt())
          } catch (e: IllegalArgumentException) {
            Log.e(TAG, getInvalidColorFormatErrorMessage(color), e)
          }
        }
        point
      }

    val segments =
      progress.segments?.map {
        val (length, color) = it
        val segment = NotificationCompat.ProgressStyle.Segment(length)
        color?.let { color ->
          try {
            segment.setColor(color.toColorInt())
          } catch (e: IllegalArgumentException) {
            Log.e(TAG, getInvalidColorFormatErrorMessage(color), e)
          }
        }
        segment
      } ?: listOf(NotificationCompat.ProgressStyle.Segment(progress.max ?: DEFAULT_MAX_PROGRESS))

    val style = NotificationCompat.ProgressStyle().setProgressSegments(segments)

    points?.let { style.setProgressPoints(it) }
    progress.progress?.let { style.setProgress(it) }

    return style
  }

  private fun getInvalidColorFormatErrorMessage(color: String): String {
    return "Invalid color format: $color"
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
    deleteIntent.putExtra(NotificationActionExtra.NOTIFICATION_ID, notificationId)
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
      config?.deepLinkUrl?.let { deepLink ->
        val scheme = getScheme(context)
        data = "$scheme://${deepLink.removePrefix("/")}".toUri()
      }
      putExtra(NotificationActionExtra.NOTIFICATION_ACTION, NotificationAction.CLICKED)
      putExtra(NotificationActionExtra.NOTIFICATION_ID, notificationId)
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
    val applicationInfo =
      context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
    return applicationInfo.metaData?.getString(EXPO_MODULE_SCHEME_KEY)
      ?: throw IllegalStateException("$EXPO_MODULE_SCHEME_KEY not found in AndroidManifest.xml")
  }
}
