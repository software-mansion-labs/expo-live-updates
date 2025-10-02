package expo.modules.liveupdates.service

import android.util.Log

interface FirebaseTokenListener {
  fun onNewToken(token: String)
}

const val TAG = "FIREBASE TOKEN HANDLER"

class FirebaseTokenHandler() {
  companion object {
    var listener: FirebaseTokenListener? = null
    var token: String? = null

    @JvmStatic
    fun addTokenListener(listener: FirebaseTokenListener) {
      Log.i(TAG, "Listener added")
      this.listener = listener
      token?.let { listener.onNewToken(it) }
    }
  }

  fun onNewToken(newToken: String) {
    Log.i(TAG, "New token change received: $newToken")
    token = newToken
    listener?.onNewToken(newToken)
  }
}
