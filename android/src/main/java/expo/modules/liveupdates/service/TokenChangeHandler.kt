package expo.modules.liveupdates.service

import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import com.google.firebase.messaging.FirebaseMessaging

const val TAG = "TokenChangeHandler"

class TokenChangeHandler() {
  companion object {
    var callback: ((String, Bundle) -> Unit)? = null
    var lastReceivedToken: String? = null

    @JvmStatic
    fun setTokenChangeCallback(callback: (String, Bundle) -> Unit) {
      this.callback = callback
      Log.i(TAG, "Push token callback added")

      lastReceivedToken?.let { token -> sendTokenChangeEvent(token) }
        ?: run {
          FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            task.result?.let { token -> sendTokenChangeEvent(token) }
          }
        }
    }

    fun sendTokenChangeEvent(token: String) {
      callback?.let { it("onTokenChange", bundleOf("token" to token)) }
    }
  }

  fun onNewToken(newToken: String) {
    Log.i(TAG, "New token received: $newToken")
    lastReceivedToken = newToken
    sendTokenChangeEvent(newToken)
  }
}
