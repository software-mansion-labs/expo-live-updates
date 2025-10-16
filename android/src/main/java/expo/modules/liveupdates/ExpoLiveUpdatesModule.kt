package expo.modules.liveupdates

import android.app.NotificationChannel
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import expo.modules.kotlin.records.Field
import expo.modules.kotlin.records.Record
import expo.modules.liveupdates.TokenChangeHandler.Companion.setHandlerSendEvent

data class LiveUpdateState(
  @Field val title: String,
  @Field val subtitle: String? = null,
  @Field val imageName: String? = null,
  @Field val smallImageName: String? = null,
) : Record

data class LiveUpdateConfig(
  @Field val backgroundColor: String? = null,
  @Field val deepLinkUrl: String? = null,
) : Record

const val MODULE_TAG = "ExpoLiveUpdatesModule"

class ExpoLiveUpdatesModule : Module() {
  private lateinit var liveUpdatesManager: LiveUpdatesManager

  // Each module class must implement the definition function. The definition consists of components
  // that describes the module's functionality and behavior.
  // See https://docs.expo.dev/modules/module-api for more details about available components.
  @RequiresApi(Build.VERSION_CODES.DONUT)
  override fun definition() = ModuleDefinition {
    // Sets the name of the module that JavaScript code will use to refer to the module. Takes a
    // string as an argument.
    // Can be inferred from module's class name, but it's recommended to set it explicitly for
    // clarity.
    // The module will be accessible from `requireNativeModule('ExpoLiveUpdatesModule')` in
    // JavaScript.
    Name("ExpoLiveUpdatesModule")

    Events(LiveUpdatesEvents.onNotificationStateChange, LiveUpdatesEvents.onTokenChange)

    OnCreate { initializeModule() }

    Function("startLiveUpdate") { state: LiveUpdateState, config: LiveUpdateConfig ->
      liveUpdatesManager.startLiveUpdateNotification(state, config)
    }
    Function("stopLiveUpdate") { notificationId: Int ->
      liveUpdatesManager.stopNotification(notificationId)
    }
    Function("updateLiveUpdate") { notificationId: Int, state: LiveUpdateState ->
      liveUpdatesManager.updateLiveUpdateNotification(notificationId, state)
    }

    OnStartObserving { setHandlerSendEvent(this@ExpoLiveUpdatesModule::sendEvent) }

    OnNewIntent { intent ->
      intent?.let {
        if (isIntentSafe(it)) {
          emitNotificationClickedEvent(it)
        } else {
          Log.w(MODULE_TAG, "Rejected unsafe intent")
        }
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
