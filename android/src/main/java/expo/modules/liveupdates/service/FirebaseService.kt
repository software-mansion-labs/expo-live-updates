package expo.modules.liveupdates

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import com.google.android.gms.tasks.OnCanceledListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import expo.modules.liveupdates.service.NotificationData
import kotlin.String

const val FIREBASE_TAG = "FIREBASE SERVICE"
const val CHANNEL_ID = "Firebase notifications channel"
const val CHANNEL_DESCRIPTION = "Channel to handle push notifications form Firebase"
const val  FIREBASE_NOTIFICATION_ID = 32


class FirebaseService: FirebaseMessagingService() {

    var notificationManager: NotificationManager? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        val androidNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_DESCRIPTION,
            NotificationManager.IMPORTANCE_HIGH
        )

        androidNotificationManager.createNotificationChannel(channel)
        notificationManager = androidNotificationManager
    }

    // TODO: this needs to go to react native
    override fun onNewToken(token: String) {
        Log.i(FIREBASE_TAG, "new token received: $token")
        super.onNewToken(token)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(message: RemoteMessage) {
        Log.i(FIREBASE_TAG, "message received")

        val notificationData = NotificationData(message.data)
        val notification = createNotification(notificationData)

        if(notificationManager !== null){
            Log.i(FIREBASE_TAG, "message displayed")
            notificationManager!!.notify(FIREBASE_NOTIFICATION_ID, notification)
        }
    }

    private fun createNotification(
        notificationData: NotificationData
    ): Notification {
        val notificationBuilder =
            NotificationCompat
                .Builder(
                    this,
                    CHANNEL_ID,
                )
                .setContentTitle(notificationData.title)
                .setSmallIcon(android.R.drawable.star_on)
                .setContentText(notificationData.body)


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA){
            notificationBuilder.setShortCriticalText("SWM")
            notificationBuilder.setOngoing(true)
            notificationBuilder.setRequestPromotedOngoing(true)

            val progressStyle = createProgressStyle(notificationData)
            notificationBuilder.setStyle(progressStyle)
        }

        return notificationBuilder.build()
    }

    private fun createProgressStyle(notificationData: NotificationData): NotificationCompat.ProgressStyle {
        val progressStyle = NotificationCompat.ProgressStyle()

        val progress = notificationData.currentProgress
        val first = notificationData.currentProgressPointOne
        val second = notificationData.currentProgressPointTwo

        if(progress !== null){
            progressStyle.setProgress(progress)
        }
        if(first !== null){
            progressStyle.addProgressPoint(
                NotificationCompat.ProgressStyle.Point(first))
        }
        if(second !== null){
            progressStyle.addProgressPoint(
                NotificationCompat.ProgressStyle.Point(second))
        }

        return progressStyle
    }

    fun printToken() {
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

