package expo.modules.liveupdates.service

import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import com.google.firebase.messaging.FirebaseMessaging

const val TAG = "TokenChangeHandler"
const val TOKEN_CHANGE_EVENT = "onTokenChange"

class TokenChangeHandler() {
  companion object {
    var sendEvent: ((String, Bundle) -> Unit)? = null
    var lastReceivedToken: String? = null

    @JvmStatic
    fun setHandlerSendEvent(sendEvent: (String, Bundle) -> Unit) {
      this.sendEvent = sendEvent
      Log.i(TAG, "Token change handler setEvent added")

      lastReceivedToken?.let { token -> sendTokenChangeEvent(token) }
        ?: run {
          FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            task.result?.let { token -> sendTokenChangeEvent(token) }
          }
        }
    }

    fun sendTokenChangeEvent(token: String) {
      sendEvent?.let { it(TOKEN_CHANGE_EVENT, bundleOf("token" to token)) }
    }
  }

  fun onNewToken(newToken: String) {
    Log.i(TAG, "New token received: $newToken")
    lastReceivedToken = newToken
    sendTokenChangeEvent(newToken)
  }
}
