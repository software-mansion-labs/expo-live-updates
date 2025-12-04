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
          if (task.result != null) sendTokenChangeEvent(task.result)
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
