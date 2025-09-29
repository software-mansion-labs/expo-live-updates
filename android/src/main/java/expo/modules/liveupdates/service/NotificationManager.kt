package expo.modules.liveupdates.service

import android.content.Context
import android.content.Intent
import expo.modules.liveupdates.LiveUpdateConfig
import expo.modules.liveupdates.LiveUpdateState
import expo.modules.liveupdates.LiveUpdatesForegroundService

val TAG = "FIREBASE SERVICE"

class NotificationManager(private var context: Context, private val channelId: String) {
  var lastConfig: LiveUpdateConfig? = null

  fun startLiveUpdatesService() {
    val serviceIntent = Intent(context, LiveUpdatesForegroundService::class.java)
    serviceIntent.putExtra("channelId", channelId)

    context.startService(serviceIntent)
  }

  fun updateNotification(state: LiveUpdateState, config: LiveUpdateConfig? = null) {
    if (config !== null) {
      lastConfig = config
    }
    val intent = Intent(ServiceAction.updateLiveUpdate)

    intent.putExtra(ServiceActionExtra.title, state.title)
    intent.putExtra(ServiceActionExtra.text, state.subtitle ?: "")
    intent.putExtra(ServiceActionExtra.date, state.date)
    intent.putExtra(ServiceActionExtra.imageName, state.imageName)
    intent.putExtra(ServiceActionExtra.smallImageName, state.smallImageName)
    intent.putExtra(ServiceActionExtra.backgroundColor, lastConfig?.backgroundColor ?: "000")

    context.sendBroadcast(intent)
  }

  fun cancelNotification() {
    val intent = Intent(ServiceAction.cancelLiveUpdate)
    context.sendBroadcast(intent)
  }
}
