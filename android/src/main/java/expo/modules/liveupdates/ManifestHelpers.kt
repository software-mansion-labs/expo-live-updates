package expo.modules.liveupdates

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log

private const val CHANNEL_ID_KEY = "expo.modules.liveupdates.channelId"
private const val CHANNEL_NAME_KEY = "expo.modules.liveupdates.channelName"
private const val EXPO_MODULE_SCHEME_KEY = "expo.modules.scheme"
private const val TAG = "ManifestHelpers"

private fun getMetadataFromManifest(context: Context, key: String): String? {
  val packageManager = context.packageManager
  val packageInfo =
    packageManager.getApplicationInfo(
      context.packageName,
      PackageManager.GET_META_DATA,
    )
  return packageInfo.metaData?.getString(key)
}
private fun getRequiredMetadataFromManifest(context: Context, key: String): String {
  val metadata = getMetadataFromManifest(context, key)
  return metadata
    ?: run {
      Log.w(TAG, "Failed to read $key from manifest.")
      throw RuntimeException(
        "ExpoLiveUpdatesModule: $key is required. Please configure withChannelConfig plugin with ${key.split(".").last()} in app.config.ts"
      )
    }
}
fun getChannelId(context: Context): String {
  return getRequiredMetadataFromManifest(context, CHANNEL_ID_KEY)
}

fun getChannelName(context: Context): String {
  return getRequiredMetadataFromManifest(context, CHANNEL_NAME_KEY)
}

fun getScheme(context: Context): String? {
  return getMetadataFromManifest(context, EXPO_MODULE_SCHEME_KEY)
}
