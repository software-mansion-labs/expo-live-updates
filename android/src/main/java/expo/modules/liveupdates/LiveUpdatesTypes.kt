package expo.modules.liveupdates

import expo.modules.kotlin.records.Field
import expo.modules.kotlin.records.Record

data class LiveUpdateProgress(
  @Field val max: Int?,
  @Field val progress: Int?,
  @Field val indeterminate: Boolean?,
) : Record

data class LiveUpdateState(
  @Field val title: String,
  @Field val text: String? = null,
  @Field val subText: String? = null,
  @Field val imageName: String? = null,
  @Field val smallImageName: String? = null,
  @Field val shortCriticalText: String? = null,
  @Field val progress: LiveUpdateProgress? = null,
) : Record

data class LiveUpdateConfig(
  @Field val backgroundColor: String? = null,
  @Field val deepLinkUrl: String? = null,
) : Record

object LiveUpdatesModuleEvents {
  const val ON_TOKEN_CHANGE = "onTokenChange"
  const val ON_NOTIFICATION_STATE_CHANGE = "onNotificationStateChange"
}
