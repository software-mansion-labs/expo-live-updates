package expo.modules.liveupdates

import android.app.NotificationChannel
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import expo.modules.liveupdates.TokenChangeHandler.Companion.setHandlerSendEvent

const val MODULE_TAG = "ExpoLiveUpdatesModule"

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

    Events(
      LiveUpdatesModuleEvents.ON_NOTIFICATION_STATE_CHANGE,
      LiveUpdatesModuleEvents.ON_TOKEN_CHANGE,
    )

    OnCreate { initializeModule() }

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

    OnStartObserving {
      if (FirebaseService.isFirebaseAvailable()) {
        setHandlerSendEvent(this@ExpoLiveUpdatesModule::sendEvent)
      }
    }

    OnNewIntent { intent ->
      if (isIntentSafe(intent)) {
        emitNotificationClickedEvent(intent)
      } else {
        Log.w(MODULE_TAG, "Rejected unsafe intent")
      }
    }
  }

  private fun isIntentSafe(intent: Intent): Boolean =
    intent.action == Intent.ACTION_VIEW && intent.`package` == context.packageName

  private fun emitNotificationClickedEvent(intent: Intent) {
    val (action, notificationId) = getNotificationClickIntentExtra(intent)

    notificationId
      .takeIf { action == NotificationAction.CLICKED }
      ?.let { id ->
        NotificationStateEventEmitter.emitNotificationStateChange(id, NotificationAction.CLICKED)
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

private fun getNotificationClickIntentExtra(intent: Intent): Pair<NotificationAction?, Int?> {
  val action =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      intent.getSerializableExtra(
        NotificationActionExtra.NOTIFICATION_ACTION,
        NotificationAction::class.java,
      )
    } else {
      @Suppress("DEPRECATION")
      intent.getSerializableExtra(NotificationActionExtra.NOTIFICATION_ACTION)
        as? NotificationAction
    }

  val notificationId =
    intent.getIntExtra(NotificationActionExtra.NOTIFICATION_ID, -1).takeIf { it != -1 }

  return action to notificationId
}
