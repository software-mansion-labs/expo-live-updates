package expo.modules.liveupdates.service

interface FirebaseTokenListener {
    fun onNewToken(token: String)
}