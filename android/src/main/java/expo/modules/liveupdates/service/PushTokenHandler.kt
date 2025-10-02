package expo.modules.liveupdates.service

import android.util.Log

interface PushTokenHandler {
  fun onNewToken(token: String)
}

interface PushTokenListener {
  fun onNewToken(token: String)
}

const val TAG = "FIREBASE TOKEN HANDLER"

class FirebaseTokenHandler() : PushTokenHandler {
  companion object {
    var listener: PushTokenListener? = null

    @JvmStatic
    fun addTokenListener(listener: PushTokenListener) {
      Log.i(TAG, "New token change listener added")
      this.listener = listener
    }
  }

  override fun onNewToken(token: String) {
    listener?.onNewToken(token)
  }
}
