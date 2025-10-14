package expo.modules.liveupdates

import expo.modules.kotlin.records.Field
import expo.modules.kotlin.records.Record

data class LiveUpdateState(
  @Field val title: String,
  @Field val subtitle: String? = null,
  @Field val imageName: String? = null,
  @Field val smallImageName: String? = null,
) : Record {
  data object Props {
    const val TITLE = "title"
    const val SUBTITLE = "subtitle"
    const val IMAGE_NAME = "imageName"
    const val SMALL_IMAGE_NAME = "smallImageName"
  }
}

data class LiveUpdateConfig(
  @Field val backgroundColor: String? = null,
  @Field val shortCriticalText: String? = null,
) : Record {
  data object Props {
    const val BACKGROUND_COLOR = "backgroundColor"
    const val SHORT_CRITICAL_TEXT = "shortCriticalText"
  }
}

object LiveUpdatesEvents {
  const val ON_TOKEN_CHANGE = "onTokenChange"
  const val ON_NOTIFICATION_STATE_CHANGE = "onNotificationStateChange"
}
