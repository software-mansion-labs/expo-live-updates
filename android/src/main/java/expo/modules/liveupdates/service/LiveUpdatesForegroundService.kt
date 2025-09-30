package expo.modules.liveupdates

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
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
import java.io.File

const val TAG = "LiveUpdatesForegroundService"

class LiveUpdatesForegroundService : Service() {
  private var channelId: String? = null
  val broadcastReceiver =
    object : BroadcastReceiver() {
      @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
      override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "Intent received  ${intent.action}")
        when (intent.action) {
          ServiceAction.updateLiveUpdate -> updateNotificationContent(intent.extras)
        }
      }
    }

  override fun onBind(intent: Intent): IBinder? = null

  @RequiresApi(Build.VERSION_CODES.BAKLAVA)
  override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
    channelId = intent.getStringExtra("channelId")

    this.registerReceiver(
      broadcastReceiver,
      IntentFilter(ServiceAction.updateLiveUpdate),
      RECEIVER_EXPORTED,
    )

    val notification = createNotification("Starting Live Updates...", "")
    notification?.let { startForeground(NOTIFICATION_ID, notification) }

    return START_STICKY
  }

  override fun onDestroy() {
    this.unregisterReceiver(broadcastReceiver)
    super.onDestroy()
  }

  @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
  private fun updateNotificationContent(extras: Bundle?) {
    Log.i(TAG, "Update notification")

    val text = extras?.getString(ServiceActionExtra.text) ?: "[text placeholder]"
    val title = extras?.getString(ServiceActionExtra.title) ?: "[title placeholder]"
    val date = extras?.getLong(ServiceActionExtra.date)
    val imageName = extras?.getString(ServiceActionExtra.imageName)
    val smallImageName = extras?.getString(ServiceActionExtra.smallImageName)
    val backgroundColor = extras?.getString(ServiceActionExtra.backgroundColor)

    val notification = createNotification(title, text, backgroundColor, imageName, smallImageName)
    val notificationManager = NotificationManagerCompat.from(this)

    notification?.let { notification ->
      if (
        ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
          PackageManager.PERMISSION_GRANTED
      ) {
        notificationManager.notify(NOTIFICATION_ID, notification)
      }
    }
  }

  private fun createNotification(
    title: String,
    text: String,
    backgroundColor: String? = null,
    image: String? = null,
    smallImageName: String? = null,
  ): Notification? {

    channelId?.let { channelId ->
      val notificationIntent = Intent("android.intent.action.MAIN")

      notificationIntent.setComponent(
        ComponentName(
          "expo.modules.liveupdates.example",
          "expo.modules.liveupdates.example.MainActivity",
        )
      )
      notificationIntent.addCategory("android.intent.category.LAUNCHER")
      val pendingIntent =
        PendingIntent.getActivity(
          this,
          0,
          notificationIntent,
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

      val notificationBuilder =
        NotificationCompat.Builder(this, channelId)
          .setContentTitle(title)
          .setSmallIcon(android.R.drawable.star_on)
          .setContentText(text)
          .setContentIntent(pendingIntent)

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

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        notificationBuilder.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
      }

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
