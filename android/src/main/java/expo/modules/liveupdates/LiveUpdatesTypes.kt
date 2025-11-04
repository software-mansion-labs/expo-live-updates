package expo.modules.liveupdates

import expo.modules.kotlin.records.Field
import expo.modules.kotlin.records.Record
import kotlinx.serialization.Serializable

data class LiveUpdateImage(@Field val url: String, @Field val isRemote: Boolean) : Record

@Serializable
data class LiveUpdateProgressPoint(@Field val position: Int, @Field val color: String? = null) :
  Record

@Serializable
data class LiveUpdateProgressSegment(@Field val length: Int, @Field val color: String? = null) :
  Record

data class LiveUpdateProgress(
  @Field val max: Int?,
  @Field val progress: Int?,
  @Field val indeterminate: Boolean?,
  @Field val points: ArrayList<LiveUpdateProgressPoint>? = null,
  @Field val segments: ArrayList<LiveUpdateProgressSegment>? = null,
) : Record

data class LiveUpdateState(
  @Field val title: String,
  @Field val text: String? = null,
  @Field val subText: String? = null,
  @Field val image: LiveUpdateImage? = null,
  @Field val icon: LiveUpdateImage? = null,
  @Field val shortCriticalText: String? = null,
  @Field val progress: LiveUpdateProgress? = null,
  @Field val showTime: Boolean? = null,
  @Field val time: Long? = null,
) : Record

data class LiveUpdateConfig(
  @Field val backgroundColor: String? = null,
  @Field val deepLinkUrl: String? = null,
) : Record

object LiveUpdatesModuleEvents {
  const val ON_TOKEN_CHANGE = "onTokenChange"
  const val ON_NOTIFICATION_STATE_CHANGE = "onNotificationStateChange"
}
