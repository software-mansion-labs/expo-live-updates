package expo.modules.liveupdates.service

import android.content.Context
import android.content.Intent
import expo.modules.liveupdates.LiveUpdateConfig
import expo.modules.liveupdates.LiveUpdateState
import expo.modules.liveupdates.LiveUpdatesService
import java.util.Random

class NotificationManager(private var context: Context, private val channelId: String) {
  // TODO: keep separate last config for each live update
  var lastConfig: LiveUpdateConfig? = null

  fun startLiveUpdatesService() {
    val serviceIntent = Intent(context, LiveUpdatesService::class.java)
    serviceIntent.putExtra("channelId", channelId)

    context.startService(serviceIntent)
  }

  fun startNotification(state: LiveUpdateState, config: LiveUpdateConfig? = null): Int {
    val notificationId = getNotificationId()
    updateNotification(notificationId, state, config)
    return notificationId
  }

  // TODO: getNotificationId should be safer
  private fun getNotificationId(): Int {
    val random = Random()
    val from = 100
    val to = 1000
    return random.nextInt(to - from) + from
  }

  fun updateNotification(
    notificationId: Int,
    state: LiveUpdateState,
    config: LiveUpdateConfig? = null,
  ) {
    config?.let { lastConfig = it }

    val intent = Intent(ServiceAction.updateLiveUpdate)

    intent.putExtra(ServiceActionExtra.notificationId, notificationId)
    intent.putExtra(ServiceActionExtra.title, state.title)
    intent.putExtra(ServiceActionExtra.text, state.subtitle ?: "")
    intent.putExtra(ServiceActionExtra.date, state.date)
    intent.putExtra(ServiceActionExtra.imageName, state.imageName)
    intent.putExtra(ServiceActionExtra.smallImageName, state.smallImageName)
    intent.putExtra(ServiceActionExtra.backgroundColor, lastConfig?.backgroundColor ?: "000")

    context.sendBroadcast(intent)
  }

  fun stopNotification(notificationId: Int) {
    val intent = Intent(ServiceAction.stopLiveUpdate)
    intent.putExtra(ServiceActionExtra.notificationId, notificationId)
    context.sendBroadcast(intent)
  }
}
