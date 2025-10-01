package expo.modules.liveupdates.service

import android.content.Context
import android.util.Log

interface FirebaseMessagingDelegate {
    fun onNewToken(token: String)
}

const val TAG = "FIREBASE SERVICE DELEGATE"
open class FirebaseServiceDelegate(protected val context: Context) : FirebaseMessagingDelegate {
    companion object {
        protected var lastToken: String? = null
        protected var listener: FirebaseTokenListener? = null

        @JvmStatic
        fun addTokenListener(listener: FirebaseTokenListener) {
            Log.i(TAG, "new listener added, old token $lastToken")
            this.listener = listener
            lastToken?.let {
                listener.onNewToken(it)
            }
        }
    }

    override fun onNewToken(token: String) {
        Log.i(TAG, "New token received: $token")
        lastToken = token
        listener?.onNewToken(token)
    }
}
