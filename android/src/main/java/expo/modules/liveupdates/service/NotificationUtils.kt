package expo.modules.liveupdates.service

import android.app.Notification
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.toColorInt
import expo.modules.liveupdates.LiveUpdateState
import java.io.File

object NotificationUtils {


  fun createNotification(
    context: Context,
    channelId: String,
    state: LiveUpdateState
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

      // TODO: Handle progress and progressPoints - these fields don't exist in LiveUpdateState
      // if (state.progress != null || !state.progressPoints.isNullOrEmpty()) {
      //   val progressStyle = createProgressStyle(state)
      //   notificationBuilder.setStyle(progressStyle)
      // } else {
        notificationBuilder.setStyle(
          NotificationCompat.ProgressStyle().setProgressIndeterminate(true)
        )
      // }
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

    // TODO: Handle backgroundColor - this field doesn't exist in LiveUpdateState
    // state.backgroundColor?.let { backgroundColor ->
    //   if (Build.VERSION.SDK_INT < Build.VERSION_CODES.BAKLAVA) {
    //     notificationBuilder.setColor(backgroundColor.toColorInt())
    //     notificationBuilder.setColorized(true)
    //   }
    // }

    // TODO: Handle notificationId - this field doesn't exist in LiveUpdateState
    // state.notificationId?.let { notificationId ->
    //   setNotificationDeleteIntent(context, notificationId, notificationBuilder)
    // }

    return notificationBuilder.build()
  }

  // TODO: Update this function to work with LiveUpdateState when progress fields are added
  // private fun createProgressStyle(
  //   state: LiveUpdateState
  // ): NotificationCompat.ProgressStyle {
  //   val progressStyle = NotificationCompat.ProgressStyle()

  //   // state.progress?.let { progressStyle.setProgress(it) }

  //   // state.progressPoints?.forEach {
  //   //   progressStyle.addProgressPoint(NotificationCompat.ProgressStyle.Point(it))
  //   // }

  //   return progressStyle
  // }

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
}
