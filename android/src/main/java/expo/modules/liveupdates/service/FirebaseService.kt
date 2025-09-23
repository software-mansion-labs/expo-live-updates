package expo.modules.liveupdates

import android.text.TextUtils
import android.util.Log
import com.google.android.gms.tasks.OnCanceledListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.String

class FirebaseService: FirebaseMessagingService() {
    private val TAG = "FIREBASE SERVICE"

    override fun onNewToken(token: String) {
        Log.i(TAG, "new token received: $token")
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.i(TAG, "new message received: " + message.notification?.title.toString())
        super.onMessageReceived(message)
    }

    fun printToken(){
        FirebaseMessaging.getInstance().getToken()
            .addOnSuccessListener(OnSuccessListener { token: String? ->
                if (!TextUtils.isEmpty(token)) {
                    Log.d(TAG, "received token: $token")
                } else {
                    Log.w(TAG, "token is null")
                }
            }).addOnFailureListener(OnFailureListener { e: Exception? -> })
            .addOnCanceledListener(OnCanceledListener {})
            .addOnCompleteListener(OnCompleteListener { task: Task<String?>? ->
                Log.v(
                    TAG,
                    "token:" + task!!.getResult()
                )
            })
    }
}