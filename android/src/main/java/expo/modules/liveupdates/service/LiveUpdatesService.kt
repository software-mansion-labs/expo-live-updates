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
            ServiceAction.updateLiveUpdate -> updateNotificationContent(notificationId, intent.extras)
            ServiceAction.cancelLiveUpdate -> cancelNotification(notificationId)
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
    intentFilter.addAction(ServiceAction.cancelLiveUpdate)

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

    val notification = createNotification(title, text, backgroundColor, imageName, smallImageName)
    val notificationManager = NotificationManagerCompat.from(this)

    if (
      ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
      PackageManager.PERMISSION_GRANTED && notification !== null
    ) {
      notificationManager.notify(notificationId, notification)
    }
  }


  private fun cancelNotification(notificationId: Int) {
    val notificationManager = NotificationManagerCompat.from(this)
    notificationManager.cancel(notificationId)
  }

  private fun createNotification(
    title: String,
    text: String,
    backgroundColor: String? = null,
    image: String? = null,
    smallImageName: String? = null,
  ): Notification? {

    if (channelId !== null) {
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
        NotificationCompat.Builder(this, channelId!!)
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

      if (image != null) {
        val bitmap = loadBitmapByName(image)
        if (bitmap != null) {
          notificationBuilder.setLargeIcon(bitmap)
        }
      }

      if (smallImageName != null) {
        val bitmap = loadBitmapByName(smallImageName)
        if (bitmap != null) {
          val icon = IconCompat.createWithBitmap(bitmap)
          notificationBuilder.setSmallIcon(icon)
        }
      }

      if (backgroundColor !== null && Build.VERSION.SDK_INT < Build.VERSION_CODES.BAKLAVA) {
        notificationBuilder.setColor(backgroundColor.toColorInt())
        notificationBuilder.setColorized(true)
      }

      return notificationBuilder.build()
    } else {
      return null
    }
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
