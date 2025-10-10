package expo.modules.liveupdates.service

import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import com.google.firebase.messaging.FirebaseMessaging

const val TOKEN_CHANGE_HANDLER_TAG = "TokenChangeHandler"

class TokenChangeHandler() {
  companion object {
    var sendEvent: ((String, Bundle) -> Unit)? = null
    var lastReceivedToken: String? = null

    @JvmStatic
    fun setHandlerSendEvent(sendEvent: (String, Bundle) -> Unit) {
      this.sendEvent = sendEvent
      Log.i(TOKEN_CHANGE_HANDLER_TAG, "Token change handler setEvent added")

      lastReceivedToken?.let { token -> sendTokenChangeEvent(token) }
        ?: run {
          FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            task.result?.let { token -> sendTokenChangeEvent(token) }
          }
        }
    }

    fun sendTokenChangeEvent(token: String) {
      sendEvent?.let { it(LiveUpdatesEvents.onTokenChange, bundleOf("token" to token)) }
    }
  }

  fun onNewToken(newToken: String) {
    Log.i(TOKEN_CHANGE_HANDLER_TAG, "New token received: $newToken")
    lastReceivedToken = newToken
    sendTokenChangeEvent(newToken)
  }
}
