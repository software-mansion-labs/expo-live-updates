package expo.modules.liveupdates.service

interface PushTokenListener {
  fun onNewToken(token: String)
}
