package expo.modules.liveupdates.service

import android.app.NotificationManager

fun checkNotificationExistence(
  notificationManager: NotificationManager,
  notificationId: Int,
  shouldExist: Boolean,
) {
  val notificationExists = doesNotificationExist(notificationManager, notificationId)

  if (shouldExist && !notificationExists) {
    throw Exception("Notification of given id doesn't exist")
  }

  if (!shouldExist && notificationExists) {
    throw Exception("Notification of given id already exists")
  }
}

fun doesNotificationExist(notificationManager: NotificationManager, notificationId: Int): Boolean {
  val notifications = notificationManager.activeNotifications
  val notification = notifications.find { notification -> notification?.id == notificationId }
  return notification !== null
}
