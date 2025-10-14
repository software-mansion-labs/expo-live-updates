package expo.modules.liveupdates

import android.content.Context
import android.util.Log

private const val CHANNEL_ID_KEY = "expo.modules.liveupdates.channelId"
private const val CHANNEL_NAME_KEY = "expo.modules.liveupdates.channelName"

private fun getMetadataFromManifest(context: Context, key: String): String {
  return try {
    val packageManager = context.packageManager
    val packageInfo =
      packageManager.getApplicationInfo(
        context.packageName,
        android.content.pm.PackageManager.GET_META_DATA,
      )
    val value = packageInfo.metaData?.getString(key)
    if (value == null) {
      throw RuntimeException()
    }
    value
  } catch (e: Exception) {
    Log.w("ManifestHelpers", "Failed to read $key from manifest: ${e.message}")
    throw RuntimeException(
      "ExpoLiveUpdatesModule: $key is required. Please configure withChannelConfig plugin with ${key.split(".").last()} in app.config.ts",
      e,
    )
  }
}

fun getChannelId(context: Context): String {
  return getMetadataFromManifest(context, CHANNEL_ID_KEY)
}

fun getChannelName(context: Context): String {
  return getMetadataFromManifest(context, CHANNEL_NAME_KEY)
}
