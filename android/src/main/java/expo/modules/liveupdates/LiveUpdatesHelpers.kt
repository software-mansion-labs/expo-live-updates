package expo.modules.liveupdates

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat

private const val CHANNEL_ID_KEY = "expo.modules.liveupdates.channelId"
private const val CHANNEL_NAME_KEY = "expo.modules.liveupdates.channelName"

private fun getMetadataFromManifest(context: Context, key: String): String {
  val packageManager = context.packageManager
  val packageInfo =
    packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
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

fun checkPostNotificationPermission(context: Context) {
  if (
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
      ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
        PackageManager.PERMISSION_GRANTED
  ) {
    throw Exception("Cannot manage Live Updates: ${Manifest.permission.POST_NOTIFICATIONS} permission is not granted.")
  }
}
