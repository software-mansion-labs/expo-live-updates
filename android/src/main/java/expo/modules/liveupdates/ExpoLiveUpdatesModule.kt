package expo.modules.liveupdates

import android.app.NotificationChannel
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import expo.modules.liveupdates.TokenChangeHandler.Companion.setHandlerSendEvent

class ExpoLiveUpdatesModule : Module() {
  private lateinit var liveUpdatesManager: LiveUpdatesManager

  // Each module class must implement the definition function. The definition consists of components
  // that describes the module's functionality and behavior.
  // See https://docs.expo.dev/modules/module-api for more details about available components.
  override fun definition() = ModuleDefinition {
    // Sets the name of the module that JavaScript code will use to refer to the module. Takes a
    // string as an argument.
    // Can be inferred from module's class name, but it's recommended to set it explicitly for
    // clarity.
    // The module will be accessible from `requireNativeModule('ExpoLiveUpdatesModule')` in
    // JavaScript.
    Name("ExpoLiveUpdatesModule")

    Events(LiveUpdatesEvents.ON_NOTIFICATION_STATE_CHANGE, LiveUpdatesEvents.ON_TOKEN_CHANGE)

    OnCreate { initializeModule() }

    OnStartObserving { setHandlerSendEvent(this@ExpoLiveUpdatesModule::sendEvent) }

    Function("startLiveUpdate") { state: LiveUpdateState, config: LiveUpdateConfig? ->
      liveUpdatesManager.startLiveUpdateNotification(state, config)
    }
    Function("stopLiveUpdate") { notificationId: Int ->
      liveUpdatesManager.stopNotification(notificationId)
    }
    Function("updateLiveUpdate") {
      notificationId: Int,
      state: LiveUpdateState,
      config: LiveUpdateConfig? ->
      liveUpdatesManager.updateLiveUpdateNotification(notificationId, state, config)
    }
  }

  private val context
    get() = requireNotNull(appContext.reactContext)

  private fun initializeModule() {
    val channelId: String = getChannelId(context)
    val channelName: String = getChannelName(context)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val serviceChannel =
        NotificationChannel(
          channelId,
          channelName,
          android.app.NotificationManager.IMPORTANCE_DEFAULT,
        )

      val androidNotificationManager =
        getSystemService(context, android.app.NotificationManager::class.java)
      androidNotificationManager?.createNotificationChannel(serviceChannel)

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
        val status =
          if (androidNotificationManager?.canPostPromotedNotifications() == true) "✅ can"
          else "❌ cannot"
        Log.i("ExpoLiveUpdatesModule", "$status post live updates")
      }
    }

    liveUpdatesManager = LiveUpdatesManager(context)
    NotificationStateEventEmitter.setInstance(NotificationStateEventEmitter(::sendEvent))
  }
}
