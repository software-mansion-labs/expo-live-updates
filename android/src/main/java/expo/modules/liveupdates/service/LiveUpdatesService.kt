package expo.modules.liveupdates

import android.Manifest
import android.app.Notification
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.toColorInt
import expo.modules.liveupdates.service.ServiceAction
import expo.modules.liveupdates.service.ServiceActionExtra
import expo.modules.liveupdates.service.setNotificationDeleteIntent
import java.io.File

const val TAG = "LiveUpdatesService"

class LiveUpdatesService : Service() {
  private var channelId: String? = null
  val broadcastReceiver =
    object : BroadcastReceiver() {
      @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
      override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "Intent received  ${intent.action}")

        val notificationId = intent.extras?.getInt(ServiceActionExtra.notificationId)

        notificationId?.let {
          when (intent.action) {
            ServiceAction.updateLiveUpdate ->
              updateNotificationContent(notificationId, intent.extras)
            ServiceAction.stopLiveUpdate -> stopNotification(notificationId)
          }
        }
      }
    }

  override fun onBind(intent: Intent): IBinder? = null

  @RequiresApi(Build.VERSION_CODES.BAKLAVA)
  override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
    channelId = intent.getStringExtra("channelId")

    val intentFilter = IntentFilter()
    intentFilter.addAction(ServiceAction.updateLiveUpdate)
    intentFilter.addAction(ServiceAction.stopLiveUpdate)

    this.registerReceiver(broadcastReceiver, intentFilter, RECEIVER_EXPORTED)

    return START_STICKY
  }

  override fun onDestroy() {
    this.unregisterReceiver(broadcastReceiver)
    super.onDestroy()
  }

  @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
  private fun updateNotificationContent(notificationId: Int, extras: Bundle?) {
    Log.i(TAG, "Update notification")

    val text = extras?.getString(ServiceActionExtra.text) ?: "[text placeholder]"
    val title = extras?.getString(ServiceActionExtra.title) ?: "[title placeholder]"
    val date = extras?.getLong(ServiceActionExtra.date)
    val imageName = extras?.getString(ServiceActionExtra.imageName)
    val smallImageName = extras?.getString(ServiceActionExtra.smallImageName)
    val backgroundColor = extras?.getString(ServiceActionExtra.backgroundColor)

    val notification =
      createNotification(title, text, backgroundColor, imageName, smallImageName, notificationId)
    val notificationManager = NotificationManagerCompat.from(this)

    if (
      ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED && notification !== null
    ) {
      notificationManager.notify(notificationId, notification)
      NotificationStateEventEmitter.emitNotificationStateChange(
        notificationId,
        NotificationAction.UPDATED,
      )
    }
  }

  private fun stopNotification(notificationId: Int) {
    val notificationManager = NotificationManagerCompat.from(this)
    notificationManager.cancel(notificationId)
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
        NotificationCompat.Builder(this, channelId)
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

      setNotificationDeleteIntent(this, notificationId, notificationBuilder)

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
}
