package expo.modules.liveupdates.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

const val TAG = "PUSH TOKEN HANDLER"

class PushTokenHandler() {
  companion object {
    var listener: PushTokenListener? = null
    var lastReceivedToken: String? = null

    @JvmStatic
    fun addTokenListener(listener: PushTokenListener) {
      this.listener = listener
      Log.i(TAG, "Push token listener added")

      lastReceivedToken?.let { listener.onNewToken(it) }
        ?: run {
          FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            task.result?.let { token -> listener.onNewToken(token) }
          }
        }
    }
  }

  fun onNewToken(newToken: String) {
    Log.i(TAG, "New token received: $newToken")
    lastReceivedToken = newToken
    listener?.onNewToken(newToken)
  }
}
