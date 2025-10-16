package expo.modules.liveupdates

import expo.modules.kotlin.records.Field
import expo.modules.kotlin.records.Record

data class LiveUpdateState(
  @Field val title: String,
  @Field val subtitle: String? = null,
  @Field val imageName: String? = null,
  @Field val smallImageName: String? = null,
  @Field val shortCriticalText: String? = null,
) : Record

data class LiveUpdateConfig(
  @Field val backgroundColor: String? = null,
) : Record {
  data object Props {
    const val BACKGROUND_COLOR = "backgroundColor"
  }
}

object LiveUpdatesModuleEvents {
  const val ON_TOKEN_CHANGE = "onTokenChange"
  const val ON_NOTIFICATION_STATE_CHANGE = "onNotificationStateChange"
}
