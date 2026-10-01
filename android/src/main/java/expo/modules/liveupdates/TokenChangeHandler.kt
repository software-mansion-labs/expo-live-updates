package expo.modules.liveupdates

import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import com.google.firebase.messaging.FirebaseMessaging

object TokenChangeHandler {
  private const val TAG = "TokenChangeHandler"

  private var lastReceivedToken: String? = null

  var sendEvent: ((String, Bundle) -> Unit)? = null
    set(value) {
      field = value
      Log.i(TAG, "Token change handler setEvent added")

      lastReceivedToken?.let(::sendTokenChangeEvent)
        ?: FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
          // task.result throws on a failed task (e.g. SERVICE_NOT_AVAILABLE), which would crash
          // the app on the main thread.
          if (!task.isSuccessful) {
            Log.w(TAG, "Failed to fetch FCM token", task.exception)
            return@addOnCompleteListener
          }
          task.result?.let(::sendTokenChangeEvent)
        }
    }

  private fun sendTokenChangeEvent(token: String) {
    sendEvent?.invoke(LiveUpdatesModuleEvents.ON_TOKEN_CHANGE, bundleOf("token" to token))
  }

  fun onNewToken(newToken: String) {
    Log.i(TAG, "New token received: $newToken")
    lastReceivedToken = newToken
    sendTokenChangeEvent(newToken)
  }
}
