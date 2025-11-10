package expo.modules.liveupdates

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log

private const val CHANNEL_ID_KEY = "expo.modules.liveupdates.channelId"
private const val CHANNEL_NAME_KEY = "expo.modules.liveupdates.channelName"
private const val EXPO_MODULE_SCHEME_KEY = "expo.modules.scheme"

private fun getMetadataFromManifest(context: Context, key: String): String {
  val packageManager = context.packageManager
  val packageInfo =
    packageManager.getApplicationInfo(
      context.packageName,
      android.content.pm.PackageManager.GET_META_DATA,
    )
  return packageInfo.metaData?.getString(key)
    ?: run {
      Log.w("ManifestHelpers", "Failed to read $key from manifest")
      throw RuntimeException(
        "ExpoLiveUpdatesModule: $key is required. Please configure withChannelConfig plugin with ${key.split(".").last()} in app.config.ts"
      )
    }
}

fun getChannelId(context: Context): String {
  return getMetadataFromManifest(context, CHANNEL_ID_KEY)
}

fun getChannelName(context: Context): String {
  return getMetadataFromManifest(context, CHANNEL_NAME_KEY)
}

fun getScheme(context: Context): String {
  val applicationInfo =
    context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
  return applicationInfo.metaData?.getString(EXPO_MODULE_SCHEME_KEY)
    ?: throw IllegalStateException("$EXPO_MODULE_SCHEME_KEY not found in AndroidManifest.xml")
}
